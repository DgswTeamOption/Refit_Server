package com.option.refit.domain.notice.service.impl;

import com.option.refit.domain.notice.dto.NoticeResponse;
import com.option.refit.domain.notice.repository.NoticeRepository;
import com.option.refit.domain.notice.service.NoticeGetService;
import com.option.refit.global.error.noticeError.exception.NoticeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeGetServiceImpl implements NoticeGetService {

    private final NoticeRepository noticeRepository;

    // TODO: classRoom 머지 후 반 멤버/담당 선생님인지 검사 추가
    @Override
    public List<NoticeResponse> getNotices(Long classId) {
        return noticeRepository.findByClassIdOrderByCreatedAtDesc(classId).stream()
                .map(NoticeResponse::new)
                .toList();
    }

    @Override
    public NoticeResponse getNotice(Long classId, Long noticeId) {
        return noticeRepository.findByIdAndClassId(noticeId, classId)
                .map(NoticeResponse::new)
                .orElseThrow(NoticeNotFoundException::new);
    }
}