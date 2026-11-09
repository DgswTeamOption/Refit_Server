package com.option.refit.domain.file.service;

import com.option.refit.domain.file.dto.FileUploadResponse;
import com.option.refit.domain.file.entity.FileEntity;
import com.option.refit.domain.file.repository.FileRepository;
import com.option.refit.domain.user.entity.User;
import com.option.refit.domain.user.repository.UserRepository;
import com.option.refit.global.error.fileError.exception.EmptyFileException;
import com.option.refit.global.error.fileError.exception.FileUploadFailedException;
import com.option.refit.global.error.fileError.exception.InvalidFilePathException;
import com.option.refit.global.error.userError.exception.InvalidFileFormatException;
import com.option.refit.global.error.userError.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

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
            @Value("${file.base-url:http://localhost:8080}") String baseUrl
    ) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.uploadDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    // 이미지 업로드
    @Override
    @Transactional
    public FileUploadResponse uploadImage(MultipartFile file, Long userId) {
        validateImage(file);

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(UserNotFoundException::new);

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
            return new FileUploadResponse(savedFile.getId(), baseUrl + savedFile.getImageUrl());
        } catch (IOException e) {
            throw new FileUploadFailedException(e);
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