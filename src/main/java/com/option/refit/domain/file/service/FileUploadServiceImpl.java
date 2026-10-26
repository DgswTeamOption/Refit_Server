package com.option.refit.domain.file.service;

import com.option.refit.domain.file.dto.FileUploadResponse;
import com.option.refit.domain.file.entity.FileEntity;
import com.option.refit.domain.file.repository.FileRepository;
import com.option.refit.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    // 파일 삭제
    @Override
    @Transactional
    public void deleteFile(Long fileId, Long loginUserId) {
        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        // 파일을 업로드한 사용자만 삭제 가능
        if (file.getUser().getId() == null
                || !file.getUser().getId().equals(loginUserId)) {
            throw new IllegalArgumentException("파일을 삭제할 권한이 없습니다.");
        }

        Path filePath = uploadDirectory.resolve(file.getStoredFileName()).normalize();
        Path tempPath = uploadDirectory.resolve(file.getStoredFileName() + ".deleting").normalize();

        validatePath(filePath);
        validatePath(tempPath);

        boolean moved = false;

        try {
            // 파일을 임시 경로로 옮겨 DB 작업이 끝날 때까지 보관
            if (Files.exists(filePath)) {
                Files.move(filePath, tempPath);
                moved = true;
            }

            fileRepository.delete(file);

            // 트랜잭션 완료 후 임시 파일 삭제 또는 복구
            registerFileCleanup(filePath, moved ? tempPath : null);
        } catch (IOException | RuntimeException e) {
            if (moved) {
                restoreFile(filePath, tempPath);
            }
            throw new IllegalStateException("파일 삭제에 실패했습니다.", e);
        }
    }

    // DB 트랜잭션 결과에 따라 임시 파일 처리
    private void registerFileCleanup(Path filePath, Path tempPath) {
        if (tempPath == null) {
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            // 커밋 성공 후 임시 파일 삭제
                            Files.deleteIfExists(tempPath);
                        } catch (IOException e) {
                            System.err.println("임시 파일 삭제 실패: " + tempPath);
                        }
                    }

                    @Override
                    public void afterCompletion(int status) {
                        if (status == STATUS_ROLLED_BACK) {
                            // 롤백 시 원래 위치로 복구
                            restoreFile(filePath, tempPath);
                        }
                    }
                }
        );
    }

    // 임시 파일을 원래 경로로 복구
    private void restoreFile(Path filePath, Path tempPath) {
        if (tempPath == null || !Files.exists(tempPath)) {
            return;
        }

        try {
            Files.move(tempPath, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("파일 복구 실패: " + filePath);
        }
    }
}