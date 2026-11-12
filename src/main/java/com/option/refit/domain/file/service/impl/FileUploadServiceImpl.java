package com.option.refit.domain.file.service.impl;

import com.option.refit.domain.file.dto.FileUploadResponse;
import com.option.refit.domain.file.entity.FileEntity;
import com.option.refit.domain.file.repository.FileRepository;
import com.option.refit.domain.file.service.FileUploadService;
import com.option.refit.domain.user.entity.User;
import com.option.refit.domain.user.repository.UserRepository;
import com.option.refit.global.error.fileError.exception.EmptyFileException;
import com.option.refit.global.error.fileError.exception.FileUploadFailedException;
import com.option.refit.global.error.fileError.exception.InvalidFilePathException;
import com.option.refit.global.error.userError.exception.InvalidFileFormatException;
import com.option.refit.global.error.userError.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    // 업로드 폴더 (기본값: 사용자 홈 디렉터리의 uploads)
    private final Path uploadDirectory;
    // 앱에서 바로 이미지를 불러올 수 있도록 응답 URL 앞에 붙이는 서버 주소
    private final String baseUrl;

    public FileUploadServiceImpl(
            FileRepository fileRepository,
            UserRepository userRepository,
            @Value("${file.upload-dir:${user.home}/uploads}") String uploadDir,
            @Value("${file.base-url:https://refit.team}") String baseUrl
    ) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.uploadDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    // 이미지 업로드
    // 파일 쓰기(느린 I/O) 동안 DB 커넥션을 점유하지 않도록 트랜잭션 밖에서 처리한다.
    // 순서: 사용자 조회 -> 파일 쓰기 -> DB 저장 (각 단계는 repository 단위의 짧은 트랜잭션)
    @Override
    public FileUploadResponse uploadImage(MultipartFile file, Long userId) {
        validateImage(file);

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(UserNotFoundException::new);

        String originalFileName = getOriginalFileName(file);
        String extension = extractExtension(originalFileName);
        String storedFileName = UUID.randomUUID() + extension;
        Path targetPath = uploadDirectory.resolve(storedFileName).normalize();

        validatePath(targetPath);

        writeFile(file, targetPath);

        try {
            FileEntity fileEntity = FileEntity.create(
                    originalFileName,
                    storedFileName,
                    "/uploads/" + storedFileName,
                    file.getContentType(),
                    file.getSize(),
                    user
            );

            FileEntity savedFile = fileRepository.save(fileEntity);
            return new FileUploadResponse(savedFile.getId(), baseUrl + savedFile.getImageUrl());
        } catch (RuntimeException e) {
            // DB 저장 실패 시 이미 저장한 파일이 고아 파일로 남지 않도록 삭제
            deleteQuietly(targetPath);
            throw e;
        }
    }

    // 업로드 폴더에 파일 저장
    private void writeFile(MultipartFile file, Path targetPath) {
        try {
            Files.createDirectories(uploadDirectory);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath);
            }
        } catch (IOException e) {
            deleteQuietly(targetPath); // 쓰다 만 파일 정리
            throw new FileUploadFailedException(e);
        }
    }

    // 롤백용 파일 삭제 (실패해도 원래 예외를 가리지 않도록 로그만 남김)
    private void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("업로드 롤백 중 파일 삭제에 실패했습니다: {}", path, e);
        }
    }

    // 빈 파일과 이미지가 아닌 파일의 업로드 예외 처리
    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new EmptyFileException();
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidFileFormatException();
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
            throw new InvalidFilePathException();
        }
    }
}