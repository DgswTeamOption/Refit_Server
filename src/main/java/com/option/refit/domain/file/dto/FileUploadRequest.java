package com.option.refit.domain.file.dto;

import org.springframework.web.multipart.MultipartFile;

public record FileUploadRequest(MultipartFile file) {
}
