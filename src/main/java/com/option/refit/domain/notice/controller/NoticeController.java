package com.option.refit.domain.notice.controller;

import com.option.refit.domain.notice.dto.NoticeRequest;
import com.option.refit.domain.notice.dto.NoticeResponse;
import com.option.refit.domain.notice.service.NoticeCreateService;
import com.option.refit.domain.notice.service.NoticeDeleteService;
import com.option.refit.domain.notice.service.NoticeGetService;
import com.option.refit.domain.notice.service.NoticeUpdateService;
import com.option.refit.global.common.ApiResponse;
import com.option.refit.global.jwt.JwtPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeCreateService noticeCreateService;
    private final NoticeGetService noticeGetService;
    private final NoticeUpdateService noticeUpdateService;
    private final NoticeDeleteService noticeDeleteService;

    @GetMapping("/notice/{classId}")
    public ApiResponse<List<NoticeResponse>> getNotices(@PathVariable Long classId) {
        return ApiResponse.ok(noticeGetService.getNotices(classId));
    }

    @GetMapping("/notice/{classId}/{noticeId}")
    public ApiResponse<NoticeResponse> getNotice(
            @PathVariable Long classId,
            @PathVariable Long noticeId) {
        return ApiResponse.ok(noticeGetService.getNotice(classId, noticeId));
    }

    @PostMapping("/notice/{classId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Long> createNotice(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId,
            @Valid @RequestBody NoticeRequest.CreateOrUpdate request) {
        Long noticeId = noticeCreateService.createNotice(classId, principal.userId(), principal.role(), request);
        return ApiResponse.ok(noticeId);
    }

    @PatchMapping("/notice/{classId}/{noticeId}")
    public ApiResponse<Void> updateNotice(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId,
            @PathVariable Long noticeId,
            @Valid @RequestBody NoticeRequest.CreateOrUpdate request) {
        noticeUpdateService.updateNotice(classId, noticeId, principal.userId(), request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/notice/{classId}/{noticeId}")
    public ApiResponse<Void> deleteNotice(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long classId,
            @PathVariable Long noticeId) {
        noticeDeleteService.deleteNotice(classId, noticeId, principal.userId());
        return ApiResponse.ok();
    }
}
