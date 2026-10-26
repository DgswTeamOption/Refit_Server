package com.option.refit.domain.file.service;

import com.option.refit.domain.file.dto.FileUploadResponse;
import com.option.refit.domain.file.entity.FileEntity;
import com.option.refit.domain.file.repository.FileRepository;
import com.option.refit.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

// 파일 업로드 및 삭제 구현체
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final FileRepository fileRepository;

    // 사용자 홈 디렉터리 기준 절대 경로
    private final Path uploadDirectory = Paths
            .get(System.getProperty("user.home"), "uploads")
            .toAbsolutePath()
            .normalize();

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

            String imageUrl = "/uploads/" + storedFileName;

            FileEntity fileEntity = FileEntity.create(
                    originalFileName,
                    storedFileName,
                    imageUrl,
                    file.getContentType(),
                    file.getSize(),
                    user
            );

            FileEntity savedFile = fileRepository.save(fileEntity);

            return new FileUploadResponse(
                    savedFile.getId(),
                    savedFile.getImageUrl()
            );
        } catch (IOException e) {
            throw new IllegalStateException("파일 업로드에 실패했습니다.", e);
        }
    }

    // 이미지 파일 검증
    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
        }
    }

    // 원본 파일명 정리
    private String getOriginalFileName(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            return "image";
        }

        // 경로 구분자를 이용한 파일 경로 전달 방지
        originalFileName = originalFileName.replace("\\", "/");
        return originalFileName.substring(originalFileName.lastIndexOf("/") + 1);
    }

    // 파일 확장자 추출
    private String extractExtension(String originalFileName) {
        int dotIndex = originalFileName.lastIndexOf(".");

        if (dotIndex <= 0) {
            return "";
        }

        return originalFileName.substring(dotIndex);
    }

    // 파일 경로 검증
    private void validatePath(Path path) {
        if (!path.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException("잘못된 파일 경로입니다.");
        }
    }

    // 파일 삭제
    @Override
    @Transactional
    public void deleteFile(Long fileId, Long loginUserId) {
        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() ->
                        new IllegalArgumentException("파일을 찾을 수 없습니다.")
                );

        // 업로드한 사용자 본인인지 확인
        if (file.getUser().getId() == null
                || !file.getUser().getId().equals(loginUserId)) {
            throw new IllegalArgumentException("파일을 삭제할 권한이 없습니다.");
        }

        Path filePath = uploadDirectory
                .resolve(file.getStoredFileName())
                .normalize();

        validatePath(filePath);

        try {
            Files.deleteIfExists(filePath);
            fileRepository.delete(file);
        } catch (IOException e) {
            throw new IllegalStateException("파일 삭제에 실패했습니다.", e);
        }
    }
}