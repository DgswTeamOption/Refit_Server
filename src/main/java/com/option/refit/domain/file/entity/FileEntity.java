package com.option.refit.domain.file.entity;

import com.option.refit.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

// 업로드된 이미지 파일의 메타데이터를 저장하는 엔티티
// 실제 이미지 파일은 서버의 uploads 폴더에 저장, DB에는 파일 정보와 접근 URL만 저장
@Entity
@Table(name = "uploaded_files")
public class FileEntity {

    // 업로드 파일의 DB 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자가 업로드할 때의 원본 파일명 (예: profile.png)
    @Column(nullable = false)
    private String originalFileName;

    // 서버에 저장할 파일명 (UUID 사용)
    @Column(nullable = false, unique = true)
    private String storedFileName;

    // 파일 접근 URL
    @Column(nullable = false)
    private String imageUrl;

    // 파일 MIME 타입
    @Column(nullable = false)
    private String contentType;

    // 파일 크기
    @Column(nullable = false)
    private long fileSize;

    // 파일 생성 시간
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 파일을 업로드한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // JPA 기본 생성자
    protected FileEntity() {
    }

    // 파일 엔티티 생성자
    private FileEntity(
            String originalFileName,
            String storedFileName,
            String imageUrl,
            String contentType,
            long fileSize,
            User user
    ) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.imageUrl = imageUrl;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    // 파일 엔티티 생성
    public static FileEntity create(
            String originalFileName,
            String storedFileName,
            String imageUrl,
            String contentType,
            long fileSize,
            User user
    ) {
        return new FileEntity(
                originalFileName,
                storedFileName,
                imageUrl,
                contentType,
                fileSize,
                user
        );
    }

    public Long getId() {
        return id;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getContentType() {
        return contentType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }
}