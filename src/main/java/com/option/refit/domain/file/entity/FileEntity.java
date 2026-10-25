package com.option.refit.domain.file.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/*
 업로드된 이미지 파일의 메타데이터를 저장하는 엔티티.
 실제 이미지 파일은 서버의 uploads 폴더에 저장, DB에는 파일 정보와 접근 URL만 저장.
 */

@Entity
@Table(name = "uploaded_files")
public class FileEntity {

    // 업로드 파일의 DB 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자가 업로드할 때의 원본 파일명 (ex, profile.png)
    @Column(nullable = false)
    private String originalFileName;

    /*
      서버에 저장되는 고유 파일명
      UUID를 사용해 같은 이름의 파일이 덮어써지는 것을 방지
     */

    @Column(nullable = false, unique = true)
    private String storedFileName;

    // 클라이언트가 이미지를 조회할 때 사용할 URL
    @Column(nullable = false, unique = true)
    private String imageUrl;

    // 업로드 파일의 MIME 타입 (ex image/png, image/jpeg)
    @Column(nullable = false)
    private String contentType;

    // 파일의 용량으로 단위는 byte
    @Column(nullable = false)
    private long fileSize;

    // 파일이 업로드된 시각. 생성 후 변경되지 않도록 updatable을 false로 설정
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // JPA가 엔티티 객체를 생성할 때 사용하는 기본 생성자
    protected FileEntity() {
    }

    private FileEntity(
            String originalFileName,
            String storedFileName,
            String imageUrl,
            String contentType,
            long fileSize
    ) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.imageUrl = imageUrl;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.createdAt = LocalDateTime.now();
    }

    // 업로드 파일 정보를 바탕으로 엔티티를 생성

    public static FileEntity create(
            String originalFileName,
            String storedFileName,
            String imageUrl,
            String contentType,
            long fileSize
    ) {
        return new FileEntity(
                originalFileName,
                storedFileName,
                imageUrl,
                contentType,
                fileSize
        );
    }

    public String getImageUrl() {
        return imageUrl;
    }
}