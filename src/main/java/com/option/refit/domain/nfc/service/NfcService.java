package com.option.refit.domain.nfc.service;

import com.option.refit.domain.classroom.service.ClassRoomAccessValidator;
import com.option.refit.domain.nfc.dto.NfcTagCreateRequest;
import com.option.refit.domain.nfc.dto.NfcTagResponse;
import com.option.refit.domain.nfc.dto.NfcTagUpdateRequest;
import com.option.refit.domain.nfc.entity.NfcTag;
import com.option.refit.domain.nfc.repository.NfcTagRepository;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.attendanceError.exception.NfcClassAlreadyHasTagException;
import com.option.refit.global.error.attendanceError.exception.NfcTagAlreadyLinkedException;
import com.option.refit.global.error.attendanceError.exception.NfcTagInUseException;
import com.option.refit.global.error.attendanceError.exception.NfcTagNotFoundException;
import com.option.refit.global.error.securityError.exception.RoleAccessDeniedException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

// NFC 태그 관리(등록·조회·수정·삭제·수업 연결·해제). 모든 메서드는 선생님 역할 + 태그 소유자 + 담당 수업을 서버에서 검사한다.
// 여기서 성공해도 실제 스티커에 URL이 기록됐다는 뜻은 아니다(쓰기는 웹·앱·쓰기 도구가 담당).
@Service
@RequiredArgsConstructor
public class NfcService {

    private final NfcTagRepository nfcTagRepository;
    private final NfcTokenGenerator tokenGenerator;
    private final ClassRoomAccessValidator accessValidator;

    @Value("${nfc.attendance-url}")
    private String attendanceUrl;

    @Transactional
    public NfcTagResponse register(JwtPrincipal principal, NfcTagCreateRequest request) {
        requireTeacherRole(principal);

        NfcTag tag = NfcTag.builder()
                .teacherId(principal.userId())
                .token(tokenGenerator.generate())
                .name(request.getName().trim())
                .build();

        return toResponse(nfcTagRepository.save(tag));
    }

    @Transactional(readOnly = true)
    public List<NfcTagResponse> getMyTags(JwtPrincipal principal) {
        requireTeacherRole(principal);

        return nfcTagRepository.findAllByTeacherIdOrderByCreatedAtDesc(principal.userId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public NfcTagResponse getTag(JwtPrincipal principal, Long tagId) {
        requireTeacherRole(principal);
        return toResponse(getOwnedTag(principal, tagId));
    }

    @Transactional
    public NfcTagResponse update(JwtPrincipal principal, Long tagId, NfcTagUpdateRequest request) {
        requireTeacherRole(principal);

        NfcTag tag = getOwnedTagForUpdate(principal, tagId);
        tag.update(request.getName() != null ? request.getName().trim() : null, request.getActive());
        return toResponse(tag);
    }

    // 수업에 연결된 태그는 삭제할 수 없다(409). 삭제는 소프트 삭제이고 과거 출석 기록은 태그를 참조하지 않아 그대로 남는다.
    @Transactional
    public void delete(JwtPrincipal principal, Long tagId) {
        requireTeacherRole(principal);

        NfcTag tag = getOwnedTagForUpdate(principal, tagId);
        if (tag.isLinked()) {
            throw new NfcTagInUseException();
        }
        nfcTagRepository.delete(tag);
    }

    // 태그를 수업에 연결. 이미 연결된 태그나 이미 태그가 있는 수업이면 덮어쓰지 않고 409.
    @Transactional
    public NfcTagResponse link(JwtPrincipal principal, Long classId, Long tagId) {
        requireTeacherRole(principal);
        accessValidator.requireTeacher(classId, principal.userId());

        NfcTag tag = getOwnedTagForUpdate(principal, tagId);
        if (tag.isLinked()) {
            throw new NfcTagAlreadyLinkedException();
        }
        if (nfcTagRepository.findByClassId(classId).isPresent()) {
            throw new NfcClassAlreadyHasTagException();
        }

        tag.linkTo(classId);
        try {
            nfcTagRepository.flush();
        } catch (DataIntegrityViolationException e) {
            // 동시에 같은 수업에 다른 태그를 연결한 경우 class_id 유니크 제약으로 걸러진다.
            throw new NfcClassAlreadyHasTagException();
        }
        return toResponse(tag);
    }

    @Transactional(readOnly = true)
    public NfcTagResponse getLinkedTag(JwtPrincipal principal, Long classId) {
        requireTeacherRole(principal);
        accessValidator.requireTeacher(classId, principal.userId());

        return toResponse(nfcTagRepository.findByClassId(classId).orElseThrow(NfcTagNotFoundException::new));
    }

    // 수업에서 태그 연결 해제. 태그는 남아 있어 다른 수업에 다시 연결할 수 있다.
    @Transactional
    public void unlink(JwtPrincipal principal, Long classId) {
        requireTeacherRole(principal);
        accessValidator.requireTeacher(classId, principal.userId());

        NfcTag linked = nfcTagRepository.findByClassId(classId).orElseThrow(NfcTagNotFoundException::new);
        // 같은 태그에 대한 연결·삭제 요청과 순차 처리되도록 행 잠금
        NfcTag tag = nfcTagRepository.findByIdForUpdate(linked.getId()).orElseThrow(NfcTagNotFoundException::new);
        tag.unlink();
    }

    private void requireTeacherRole(JwtPrincipal principal) {
        if (principal.role() != Role.TEACHER) {
            throw new RoleAccessDeniedException();
        }
    }

    // 없으면 404, 다른 선생님의 태그면 403
    private NfcTag getOwnedTag(JwtPrincipal principal, Long tagId) {
        return requireOwner(principal, nfcTagRepository.findById(tagId).orElseThrow(NfcTagNotFoundException::new));
    }

    private NfcTag getOwnedTagForUpdate(JwtPrincipal principal, Long tagId) {
        return requireOwner(principal, nfcTagRepository.findByIdForUpdate(tagId).orElseThrow(NfcTagNotFoundException::new));
    }

    private NfcTag requireOwner(JwtPrincipal principal, NfcTag tag) {
        if (!tag.isOwnedBy(principal.userId())) {
            throw new RoleAccessDeniedException();
        }
        return tag;
    }

    private NfcTagResponse toResponse(NfcTag tag) {
        String url = UriComponentsBuilder.fromUriString(attendanceUrl)
                .queryParam("token", tag.getToken())
                .build()
                .toUriString();
        return new NfcTagResponse(tag, url);
    }
}
