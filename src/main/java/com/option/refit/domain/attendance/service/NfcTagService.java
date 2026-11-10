package com.option.refit.domain.attendance.service;

import com.option.refit.domain.attendance.dto.NfcTagResponse;
import com.option.refit.global.jwt.JwtPrincipal;

public interface NfcTagService {

    // 선생님이 담당 수업에 NFC 태그 등록
    NfcTagResponse registerTag(JwtPrincipal principal, Long classId, String tagUid);

    // 태그가 해당 수업에 등록된 유효한 태그인지 인증 (출석은 기록하지 않음)
    NfcTagResponse verifyTag(JwtPrincipal principal, Long classId, String tagUid);
}
