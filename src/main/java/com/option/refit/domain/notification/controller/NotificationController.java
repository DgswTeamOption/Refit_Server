package com.option.refit.domain.notification.controller;

import com.option.refit.domain.notification.dto.NotificationReadAllResponse;
import com.option.refit.domain.notification.dto.NotificationResponse;
import com.option.refit.domain.notification.service.NotificationDeleteService;
import com.option.refit.domain.notification.service.NotificationGetService;
import com.option.refit.domain.notification.service.NotificationReadService;
import com.option.refit.global.common.ApiResponse;
import com.option.refit.global.error.jwtError.exception.MissingTokenException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationGetService notificationGetService;
    private final NotificationDeleteService notificationDeleteService;
    private final NotificationReadService notificationReadService;

    @GetMapping
    public ApiResponse<Page<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PageableDefault(size = 20,
                    sort = {"createdAt", "id"},
                    direction = Sort.Direction.DESC)
            Pageable pageable) {

        validatePrincipal(principal);

        return ApiResponse.ok(notificationGetService.getNotifications(
                principal.userId(), pageable));
    }

    // 본인의 안 읽은 알림을 모두 읽음 처리 (여러 페이지에 걸쳐도 전체 대상). 변경된 개수 반환
    @PatchMapping("/read-all")
    public ApiResponse<NotificationReadAllResponse> readAllNotifications(
            @AuthenticationPrincipal JwtPrincipal principal) {

        validatePrincipal(principal);

        return ApiResponse.ok(notificationReadService.readAllNotifications(principal.userId()));
    }

    // 본인 알림만 읽음 처리 (타인 알림 403, 없는 알림 404)
    @PatchMapping("/{notificationId}/read")
    public ApiResponse<NotificationResponse> readNotification(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable("notificationId") Long notificationId) {

        validatePrincipal(principal);

        return ApiResponse.ok(notificationReadService.readNotification(
                notificationId, principal.userId()));
    }

    @DeleteMapping("/{notificationId}")
    public ApiResponse<Void> deleteNotification(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable("notificationId") Long notificationId) {

        validatePrincipal(principal);

        notificationDeleteService.deleteNotification(
                notificationId, principal.userId());

        return ApiResponse.ok();
    }

    private void validatePrincipal(JwtPrincipal principal) {
        if (principal == null
                || principal.userId() == null
                || principal.role() == null) {
            throw new MissingTokenException();
        }
    }
}