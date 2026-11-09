package com.option.refit.domain.file.controller;

import com.option.refit.domain.file.dto.FileUploadRequest;
import com.option.refit.domain.file.dto.FileUploadResponse;
import com.option.refit.domain.file.service.FileDeleteService;
import com.option.refit.domain.file.service.FileUploadService;
import com.option.refit.global.common.ApiResponse;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 파일 업로드 및 삭제 API
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final FileUploadService fileUploadService;
    private final FileDeleteService fileDeleteService;

    // 이미지 업로드
    @PostMapping(
            value = "/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<FileUploadResponse> uploadImage(
            @ModelAttribute FileUploadRequest request,
            @AuthenticationPrincipal JwtPrincipal principal
    ) {
        return ApiResponse.ok(fileUploadService.uploadImage(
                request.file(),
                principal.userId()
        ));
    }

    // 파일 삭제
    @DeleteMapping("/{fileId}")
    public ApiResponse<Void> deleteFile(
            @PathVariable Long fileId,
            @AuthenticationPrincipal JwtPrincipal principal
    ) {
        fileDeleteService.deleteFile(
                fileId,
                principal.userId()
        );

        return ApiResponse.ok();
    }
}