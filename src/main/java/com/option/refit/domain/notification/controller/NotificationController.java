package com.option.refit.domain.notification.controller;

import com.option.refit.domain.notification.dto.NotificationResponse;
import com.option.refit.domain.notification.service.NotificationDeleteService;
import com.option.refit.domain.notification.service.NotificationGetService;
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

    @GetMapping
    public ApiResponse<Page<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PageableDefault(size = 20,
                    sort = {"createdAt", "id"},
                    direction = Sort.Direction.DESC)
            Pageable pageable) {

        validatePrincipal(principal);

        return ApiResponse.ok(notificationGetService.getNotifications(
                principal.userId(), principal.role(), pageable));
    }

    @DeleteMapping("/{notificationId}")
    public ApiResponse<Void> deleteNotification(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable("notificationId") Long notificationId) {

        validatePrincipal(principal);

        notificationDeleteService.deleteNotification(
                notificationId, principal.userId(), principal.role());

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