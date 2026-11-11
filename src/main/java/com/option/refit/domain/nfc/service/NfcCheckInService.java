package com.option.refit.domain.nfc.service;

import com.option.refit.domain.attendance.dto.AttendanceResponse;
import com.option.refit.domain.attendance.service.AttendanceCheckInService;
import com.option.refit.domain.nfc.dto.NfcAttendanceRequest;
import com.option.refit.domain.nfc.entity.NfcTag;
import com.option.refit.domain.nfc.repository.NfcTagRepository;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.attendanceError.exception.NfcTagInactiveException;
import com.option.refit.global.error.attendanceError.exception.NfcTagNotFoundException;
import com.option.refit.global.error.attendanceError.exception.NfcTagNotLinkedException;
import com.option.refit.global.error.securityError.exception.RoleAccessDeniedException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

// 학생의 NFC 출석. 태그(토큰)를 확인해 연결된 수업을 찾고, 출석 검증·저장은 attendance 서비스에 맡긴다.
// 토큰은 태그를 식별할 뿐이므로 로그인(JWT)한 학생의 요청이어야 한다. 출석 시각은 서버 시간으로 기록한다.
@Service
@RequiredArgsConstructor
public class NfcCheckInService {

    private final NfcTagRepository nfcTagRepository;
    private final AttendanceCheckInService attendanceCheckInService;

    public AttendanceResponse checkIn(JwtPrincipal principal, NfcAttendanceRequest request) {
        // 학생만 출석 가능 (403). 토큰 존재 여부를 학생이 아닌 사용자에게 알려주지 않도록 먼저 검사
        if (principal.role() != Role.STUDENT) {
            throw new RoleAccessDeniedException();
        }

        // 존재하지 않거나 삭제된 태그 (404), 비활성 태그 (400), 수업에 연결되지 않은 태그 (400)
        NfcTag tag = nfcTagRepository.findByToken(request.getToken().trim())
                .orElseThrow(NfcTagNotFoundException::new);
        if (!tag.isActive()) {
            throw new NfcTagInactiveException();
        }
        if (!tag.isLinked()) {
            throw new NfcTagNotLinkedException();
        }

        return attendanceCheckInService.checkInByNfc(
                principal, tag.getClassId(), request.getScheduleId(), LocalDateTime.now());
    }
}
