package com.option.refit.domain.file.controller;

import com.option.refit.domain.file.dto.FileUploadRequest;
import com.option.refit.domain.file.dto.FileUploadResponse;
import com.option.refit.domain.file.service.FileUploadService;
import com.option.refit.global.security.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    // 이미지 업로드
    @PostMapping(
            value = "/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<FileUploadResponse> uploadImage(
            @ModelAttribute FileUploadRequest request,
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        FileUploadResponse response = fileUploadService.uploadImage(
                request.file(),
                memberDetails.getUser()
        );

        return ResponseEntity.ok(response);
    }

    // 파일 삭제
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable Long fileId,
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        fileUploadService.deleteFile(
                fileId,
                memberDetails.getUser().getId()
        );

        return ResponseEntity.noContent().build();
    }
}