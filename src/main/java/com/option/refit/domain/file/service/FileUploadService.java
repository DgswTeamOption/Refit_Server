package com.option.refit.domain.file.service;

import com.option.refit.domain.file.dto.FileUploadResponse;
import com.option.refit.domain.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

// 파일 업로드 및 삭제
public interface FileUploadService {

    // 이미지 업로드
    FileUploadResponse uploadImage(MultipartFile file, User user);
}