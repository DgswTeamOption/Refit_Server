package com.option.refit.domain.file.service;

import com.option.refit.domain.file.dto.FileUploadResponse;
import com.option.refit.domain.file.entity.FileEntity;
import com.option.refit.domain.file.repository.FileRepository;
import com.option.refit.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final FileRepository fileRepository;

    // 사용자 홈 디렉터리의 uploads 폴더에 파일 저장
    private final Path uploadDirectory = Paths.get(
            System.getProperty("user.home"), "uploads").toAbsolutePath().normalize();

    // 이미지 업로드
    @Override
    @Transactional
    public FileUploadResponse uploadImage(MultipartFile file, User user) {
        validateImage(file);

        String originalFileName = getOriginalFileName(file);
        String extension = extractExtension(originalFileName);
        String storedFileName = UUID.randomUUID() + extension;
        Path targetPath = uploadDirectory.resolve(storedFileName).normalize();

        validatePath(targetPath);

        try {
            Files.createDirectories(uploadDirectory);
            Files.copy(file.getInputStream(), targetPath);

            FileEntity fileEntity = FileEntity.create(
                    originalFileName,
                    storedFileName,
                    "/uploads/" + storedFileName,
                    file.getContentType(),
                    file.getSize(),
                    user
            );

            FileEntity savedFile = fileRepository.save(fileEntity);
            return new FileUploadResponse(savedFile.getId(), savedFile.getImageUrl());
        } catch (IOException e) {
            throw new IllegalStateException("파일 업로드에 실패했습니다.", e);
        }
    }

    // 빈 파일과 이미지가 아닌 파일의 업로드 예외 처리
    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
        }
    }

    // 파일 경로가 포함된 원본 파일명 정리
    private String getOriginalFileName(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isBlank()) {
            return "image";
        }

        fileName = fileName.replace("\\", "/");
        return fileName.substring(fileName.lastIndexOf("/") + 1);
    }

    // 원본 파일의 확장자 추출
    private String extractExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        return dotIndex > 0 ? fileName.substring(dotIndex) : "";
    }

    // 파일이 지정된 업로드 폴더 안에 있는지 확인
    private void validatePath(Path path) {
        if (!path.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException("잘못된 파일 경로입니다.");
        }
    }
}