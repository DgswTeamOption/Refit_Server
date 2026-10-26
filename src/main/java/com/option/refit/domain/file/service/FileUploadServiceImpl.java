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

    // 업로드 파일 저장 경로
    private final Path uploadDirectory =
            Paths.get("uploads").toAbsolutePath().normalize();

    // 이미지 업로드
    @Override
    @Transactional
    public FileUploadResponse uploadImage(MultipartFile file, User user) {

        // 파일이 비어 있는지 확인
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        // 이미지 파일인지 확인
        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
        }

        // 원본 파일명 확인
        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            originalFileName = "image";
        }

        // 파일 확장자 추출
        String extension = "";

        if (originalFileName.contains(".")) {
            extension = originalFileName.substring(
                    originalFileName.lastIndexOf(".")
            );
        }

        // UUID를 이용해 저장 파일명 생성
        String storedFileName = UUID.randomUUID() + extension;

        Path targetPath = uploadDirectory
                .resolve(storedFileName)
                .normalize();

        // 잘못된 파일 경로 방지
        if (!targetPath.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException("잘못된 파일 경로입니다.");
        }

        try {
            // uploads 폴더가 없으면 생성
            Files.createDirectories(uploadDirectory);

            // 실제 파일 저장
            Files.copy(file.getInputStream(), targetPath);

            // 파일 접근 URL
            String imageUrl = "/uploads/" + storedFileName;

            // DB에 저장할 파일 정보 생성
            FileEntity fileEntity = FileEntity.create(
                    originalFileName,
                    storedFileName,
                    imageUrl,
                    contentType,
                    file.getSize(),
                    user
            );

            // 파일 정보 DB 저장
            FileEntity savedFile = fileRepository.save(fileEntity);

            // 업로드 결과 반환
            return new FileUploadResponse(
                    savedFile.getId(),
                    savedFile.getImageUrl()
            );

        } catch (IOException e) {
            throw new IllegalStateException("파일 업로드에 실패했습니다.", e);
        }
    }

    // 파일 삭제
    @Override
    @Transactional
    public void deleteFile(Long fileId, Long loginUserId) {

        // 파일 정보 조회
        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() ->
                        new IllegalArgumentException("파일을 찾을 수 없습니다.")
                );

        // 본인이 업로드한 파일인지 확인
        if (file.getUser().getId() == null
                || !file.getUser().getId().equals(loginUserId)) {
            throw new IllegalArgumentException("파일을 삭제할 권한이 없습니다.");
        }

        // 실제 파일 경로 생성
        Path filePath = uploadDirectory
                .resolve(file.getStoredFileName())
                .normalize();

        // 잘못된 파일 경로 방지
        if (!filePath.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException("잘못된 파일 경로입니다.");
        }

        try {
            // 서버에 저장된 실제 파일 삭제
            Files.deleteIfExists(filePath);

            // DB에 저장된 파일 정보 삭제
            fileRepository.delete(file);

        } catch (IOException e) {
            throw new IllegalStateException("파일 삭제에 실패했습니다.", e);
        }
    }
}