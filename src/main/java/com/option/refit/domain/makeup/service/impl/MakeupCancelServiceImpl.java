package com.option.refit.domain.makeup.service.impl;

import com.option.refit.domain.makeup.dto.MakeupApproveResponse;
import com.option.refit.domain.makeup.entity.Makeup;
import com.option.refit.domain.makeup.repository.MakeupRepository;
import com.option.refit.domain.makeup.service.MakeupCancelService;
import com.option.refit.domain.notification.service.NotificationSendService;
import com.option.refit.domain.user.repository.UserRepository;
import com.option.refit.global.error.makeupError.exception.AlreadyProcessedMakeupException;
import com.option.refit.global.error.makeupError.exception.MakeupAccessDeniedException;
import com.option.refit.global.error.makeupError.exception.MakeupNotFoundException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MakeupCancelServiceImpl implements MakeupCancelService {

    // 알림 유형: 보강 신청 취소 (0 제안, 1 승인, 2 거절, 3 공지, 4 일정 변경, 5 취소)
    private static final String NOTIFICATION_TYPE_MAKEUP_CANCELLED = "5";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final MakeupRepository makeupRepository;
    private final UserRepository userRepository;
    private final NotificationSendService notificationSendService;

    @Override
    @Transactional
    public MakeupApproveResponse cancelMakeup(JwtPrincipal principal, Long makeupId) {
        // 존재하지 않는 보강 신청 (404)
        Makeup makeup = makeupRepository.findById(makeupId)
                .orElseThrow(MakeupNotFoundException::new);

        // 신청한 본인만 취소 가능 (403)
        if (!makeup.getStudentId().equals(principal.userId())) {
            throw new MakeupAccessDeniedException();
        }

        // 대기 상태만 취소 가능, 이미 승인/거절/취소된 건은 409
        if (!makeup.isPending()) {
            throw new AlreadyProcessedMakeupException();
        }

        // 행은 삭제하지 않고 상태만 변경해 이력을 유지한다.
        makeup.cancel();

        // 담당 선생님에게 알림 (취소와 같은 트랜잭션: 알림 저장 실패 시 취소도 롤백)
        String studentName = userRepository.findByIdAndDeletedAtIsNull(makeup.getStudentId())
                .map(user -> user.getName())
                .orElse("학생");
        notificationSendService.send(
                makeup.getTeacherId(),
                makeup.getStudentId(),
                makeup.getTeacherId(),
                studentName + " 학생이 " + makeup.getMakeupTime().format(TIME_FORMAT) + " 보강 신청을 취소했습니다",
                NOTIFICATION_TYPE_MAKEUP_CANCELLED
        );

        return new MakeupApproveResponse(makeup);
    }
}
