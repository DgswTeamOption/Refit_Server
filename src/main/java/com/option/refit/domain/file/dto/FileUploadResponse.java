package com.option.refit.domain.file.dto;

public record FileUploadResponse(
        Long fileId,
        String imageUrl
) {}