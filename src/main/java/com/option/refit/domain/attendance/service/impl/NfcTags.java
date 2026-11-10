package com.option.refit.domain.attendance.service.impl;

import com.option.refit.domain.attendance.entity.NfcTag;
import com.option.refit.domain.attendance.repository.NfcTagRepository;
import com.option.refit.global.error.attendanceError.exception.NfcTagClassMismatchException;
import com.option.refit.global.error.attendanceError.exception.NfcTagNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;

// NFC 태그 값 정규화와 수업별 태그 조회를 모아 둔 공통 로직
@Component
@RequiredArgsConstructor
class NfcTags {

    private final NfcTagRepository nfcTagRepository;

    // 앞뒤 공백 제거 + 대문자로 통일 (같은 태그가 대소문자만 달라 중복 등록되는 것을 방지)
    String normalize(String tagUid) {
        return tagUid.trim().toUpperCase(Locale.ROOT);
    }

    // 등록된 태그이고(404) 요청한 수업의 태그인지(400) 확인
    NfcTag getTagOfClass(Long classId, String tagUid) {
        NfcTag tag = nfcTagRepository.findByTagUid(normalize(tagUid))
                .orElseThrow(NfcTagNotFoundException::new);

        if (!tag.belongsTo(classId)) {
            throw new NfcTagClassMismatchException();
        }
        return tag;
    }
}
