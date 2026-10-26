package com.option.refit.domain.file.service;

public interface FileDeleteService {

    // 파일 삭제
    void deleteFile(Long fileId, Long loginUserId);
}
