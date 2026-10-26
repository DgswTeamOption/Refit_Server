package com.option.refit.domain.file.service;

import com.option.refit.domain.file.dto.FileUploadResponse;
import com.option.refit.domain.file.entity.FileEntity;
import com.option.refit.domain.file.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("jpg", "jpeg", "png");

    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024;

    private static final Path UPLOAD_DIRECTORY =
            Paths.get("uploads").toAbsolutePath().normalize();

    private final FileRepository fileRepository;

    public FileUploadServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    // 이미지 업로드
    @Override
    public FileUploadResponse uploadImage(MultipartFile file) {
        validate(file);

        String extension =
                extractExtension(file.getOriginalFilename());

        String storedFileName =
                UUID.randomUUID() + "." + extension;

        String imageUrl = "/uploads/" + storedFileName;

        Path destination =
                UPLOAD_DIRECTORY.resolve(storedFileName);

        try {
            Files.createDirectories(UPLOAD_DIRECTORY);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destination);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "이미지 저장에 실패했습니다.", e
            );
        }

        try {
            FileEntity savedFile = fileRepository.save(
                    FileEntity.create(
                            file.getOriginalFilename(),
                            storedFileName,
                            imageUrl,
                            file.getContentType(),
                            file.getSize()
                    )
            );

            return new FileUploadResponse(savedFile.getImageUrl());

        } catch (RuntimeException e) {
            // DB 저장 실패 시 실제 파일 정리
            try {
                Files.deleteIfExists(destination);
            } catch (IOException cleanupException) {
                e.addSuppressed(cleanupException);
            }

            throw e;
        }
    }

    // 파일 삭제
    @Override
    public void deleteFile(Long fileId) {

        // DB에서 파일 정보 조회
        FileEntity fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "파일을 찾을 수 없습니다."
                        )
                );

        // 실제 파일 경로 생성
        Path filePath = UPLOAD_DIRECTORY
                .resolve(fileEntity.getStoredFileName())
                .normalize();

        // 업로드 폴더 외부의 파일 삭제 방지
        if (!filePath.startsWith(UPLOAD_DIRECTORY)
                || filePath.equals(UPLOAD_DIRECTORY)) {
            throw new IllegalArgumentException(
                    "잘못된 파일 경로입니다."
            );
        }

        // 실제 파일 삭제
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "파일 삭제에 실패했습니다.", e
            );
        }

        // DB 정보 삭제
        fileRepository.delete(fileEntity);
    }

    // 업로드 파일 검증
    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "업로드할 이미지 파일을 선택해주세요."
            );
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException(
                    "이미지 파일은 10MB 이하만 업로드할 수 있습니다."
            );
        }

        String extension =
                extractExtension(file.getOriginalFilename());

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                    "jpg, jpeg, png 형식의 이미지만 업로드할 수 있습니다."
            );
        }

        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);

            if (image == null) {
                throw new IllegalArgumentException(
                        "실제 이미지 파일만 업로드할 수 있습니다."
                );
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "이미지 파일을 읽을 수 없습니다.", e
            );
        }
    }

    // 파일 확장자 추출
    private String extractExtension(String originalFileName) {
        if (originalFileName == null) {
            throw new IllegalArgumentException(
                    "파일 이름이 없습니다."
            );
        }

        int lastDotIndex = originalFileName.lastIndexOf('.');

        if (lastDotIndex <= 0
                || lastDotIndex == originalFileName.length() - 1) {
            throw new IllegalArgumentException(
                    "파일 확장자가 없습니다."
            );
        }

        return originalFileName
                .substring(lastDotIndex + 1)
                .toLowerCase(Locale.ROOT);
    }
}