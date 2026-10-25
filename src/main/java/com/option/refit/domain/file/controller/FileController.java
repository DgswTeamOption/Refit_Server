package com.option.refit.domain.file.controller;

import com.option.refit.domain.file.dto.FileUploadRequest;
import com.option.refit.domain.file.dto.FileUploadResponse;
import com.option.refit.domain.file.service.FileUploadService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileUploadService fileUploadService;

    public FileController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping(
            value = "/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<FileUploadResponse> uploadImage(
            @ModelAttribute FileUploadRequest request
    ) {
        FileUploadResponse response =
                fileUploadService.uploadImage(request.file());

        return ResponseEntity.ok(response);
    }
}