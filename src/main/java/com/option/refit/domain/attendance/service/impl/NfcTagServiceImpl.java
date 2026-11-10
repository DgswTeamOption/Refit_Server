package com.option.refit.domain.attendance.service.impl;

import com.option.refit.domain.attendance.dto.NfcTagResponse;
import com.option.refit.domain.attendance.entity.NfcTag;
import com.option.refit.domain.attendance.repository.NfcTagRepository;
import com.option.refit.domain.attendance.service.NfcTagService;
import com.option.refit.domain.classroom.service.ClassRoomAccessValidator;
import com.option.refit.global.error.attendanceError.exception.DuplicateNfcTagException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NfcTagServiceImpl implements NfcTagService {

    private final NfcTagRepository nfcTagRepository;
    private final ClassRoomAccessValidator accessValidator;
    private final NfcTags nfcTags;

    @Override
    @Transactional
    public NfcTagResponse registerTag(JwtPrincipal principal, Long classId, String tagUid) {
        // 담당 교사만 등록 가능 (수업이 없으면 404, 담당이 아니면 403)
        accessValidator.requireTeacher(classId, principal.userId());

        String normalized = nfcTags.normalize(tagUid);

        // 이미 등록된 태그는 어느 수업이든 중복 (409)
        if (nfcTagRepository.existsByTagUid(normalized)) {
            throw new DuplicateNfcTagException();
        }

        NfcTag tag = NfcTag.builder()
                .classId(classId)
                .tagUid(normalized)
                .registeredBy(principal.userId())
                .build();

        try {
            return new NfcTagResponse(nfcTagRepository.saveAndFlush(tag));
        } catch (DataIntegrityViolationException e) {
            // 동시에 같은 태그를 등록한 경우 유니크 제약으로 걸러짐
            throw new DuplicateNfcTagException();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public NfcTagResponse verifyTag(JwtPrincipal principal, Long classId, String tagUid) {
        // 담당 교사이거나 참여 학생만 인증 가능
        accessValidator.requireTeacherOrMember(classId, principal.userId());

        return new NfcTagResponse(nfcTags.getTagOfClass(classId, tagUid));
    }
}
