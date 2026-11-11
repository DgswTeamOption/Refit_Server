-- 수업 배너 이미지 (MySQL). 마이그레이션 도구가 없으므로 수동 적용 후 테이블 명세서·ERD에 반영한다.
ALTER TABLE classroom
    ADD COLUMN banner_image_id BIGINT NULL,                 -- 배너 이미지 (uploaded_files.id), 없으면 NULL
    ADD CONSTRAINT fk_classroom_banner_image
        FOREIGN KEY (banner_image_id) REFERENCES uploaded_files (id);
