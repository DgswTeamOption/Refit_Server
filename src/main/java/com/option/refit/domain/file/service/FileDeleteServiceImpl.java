package com.option.refit.domain.file.service;

import com.option.refit.domain.file.entity.FileEntity;
import com.option.refit.domain.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class FileDeleteServiceImpl implements FileDeleteService {

    private final FileRepository fileRepository;

    private final Path uploadDirectory = Paths.get(
            System.getProperty("user.home"), "uploads"
    ).toAbsolutePath().normalize();

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
            if (Files.exists(filePath)) {
                Files.move(filePath, tempPath);
                moved = true;
            }

            fileRepository.delete(file);

            registerFileCleanup(filePath, moved ? tempPath : null);
        } catch (IOException | RuntimeException e) {
            if (moved) {
                restoreFile(filePath, tempPath);
            }
            throw new IllegalStateException("파일 삭제에 실패했습니다.", e);
        }
    }

    // 파일 경로가 업로드 폴더 안에 있는지 확인
    private void validatePath(Path path) {
        if (!path.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException("잘못된 파일 경로입니다.");
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
                            Files.deleteIfExists(tempPath);
                        } catch (IOException e) {
                            System.err.println("임시 파일 삭제 실패: " + tempPath);
                        }
                    }

                    @Override
                    public void afterCompletion(int status) {
                        if (status == STATUS_ROLLED_BACK) {
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