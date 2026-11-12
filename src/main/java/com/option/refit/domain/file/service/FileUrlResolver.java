package com.option.refit.domain.file.service;

import com.option.refit.domain.file.entity.FileEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// 저장된 파일의 접근 경로(/uploads/...)를 앱에서 바로 쓸 수 있는 전체 URL로 변환한다.
// 업로드 응답(FileUploadResponse.imageUrl)과 같은 형식을 쓴다.
@Component
public class FileUrlResolver {

    private final String baseUrl;

    public FileUrlResolver(@Value("${file.base-url:https://refit.team}") String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    // 파일이 없으면 null
    public String resolve(FileEntity file) {
        return file == null ? null : baseUrl + file.getImageUrl();
    }
}
