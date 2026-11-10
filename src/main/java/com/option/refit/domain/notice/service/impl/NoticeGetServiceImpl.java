package com.option.refit.domain.notice.service.impl;

import com.option.refit.domain.notice.dto.NoticeResponse;
import com.option.refit.domain.notice.repository.NoticeRepository;
import com.option.refit.domain.notice.service.NoticeGetService;
import com.option.refit.domain.user.entity.Role;
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

    @Override
    public List<NoticeResponse> getNotices(Long classId, Long userId, Role role) {
        // TODO: 클래스 멤버십 기능이 연결되면
        // userId와 role이 classId에 접근 가능한지 검사

        return noticeRepository.findByClassIdOrderByCreatedAtDesc(classId).stream()
                .map(NoticeResponse::new)
                .toList();
    }

    @Override
    public NoticeResponse getNotice(
            Long classId,
            Long noticeId,
            Long userId,
            Role role
    ) {
        // TODO: 클래스 멤버십 기능이 연결되면
        // userId와 role이 classId에 접근 가능한지 검사

        return noticeRepository.findByIdAndClassId(noticeId, classId)
                .map(NoticeResponse::new)
                .orElseThrow(NoticeNotFoundException::new);
    }
}