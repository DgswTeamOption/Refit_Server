package com.option.refit.domain.file.service;

import com.option.refit.domain.file.dto.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    FileUploadResponse uploadImage(MultipartFile file);

    void deleteFile(Long fileId);
}

