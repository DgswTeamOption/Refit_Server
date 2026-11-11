-- NFC 출석 DB 변경 (MySQL). 적용 전 아래 "사전 점검"을 먼저 실행한다.
-- 이 저장소에는 마이그레이션 도구가 없으므로 수동 적용 후 테이블 명세서·ERD에 같은 내용을 반영한다.

-- ── 사전 점검 1: attendances에 (schedule_id, student_id) 중복이 있는지 (결과가 0행이어야 한다)
SELECT schedule_id, student_id, COUNT(*) AS cnt
FROM attendances
GROUP BY schedule_id, student_id
HAVING COUNT(*) > 1;
-- 중복이 있으면 가장 최근(id가 큰) 기록 하나만 남기고 정리한 뒤 진행한다.
-- DELETE a FROM attendances a JOIN attendances b
--   ON a.schedule_id = b.schedule_id AND a.student_id = b.student_id AND a.id < b.id;

-- ── 사전 점검 2: 기존 유니크 인덱스 이름 확인 (환경마다 다를 수 있다)
SHOW INDEX FROM attendances WHERE Non_unique = 0;

-- ── 1) attendances: 유니크 제약 교체. 아래 uk_... 이름이 사전 점검 2의 Key_name과 다르면 맞춰서 바꾼다.
-- 새 제약을 먼저 만든 뒤 기존 제약을 지운다. 그 사이에도 중복 방지가 유지된다.
ALTER TABLE attendances
    ADD CONSTRAINT uk_attendances_schedule_student UNIQUE (schedule_id, student_id);
-- ALTER TABLE attendances DROP INDEX <기존 schedule_id 단독 유니크 인덱스 이름>;
-- status 컬럼은 VARCHAR(20) 문자열이라 'LATE' 값은 컬럼 변경 없이 저장된다. method, checked_at 컬럼은 이미 존재한다.

-- ── 2) nfc_tags 신규 (기존 nfc_tags가 있다면 아래 3)의 이전 절차를 먼저 본다)
CREATE TABLE nfc_tags (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    teacher_id  BIGINT       NOT NULL,              -- 태그 소유 선생님 (users.id)
    class_id    BIGINT       NULL,                  -- 연결된 수업 (classroom.id), 미연결이면 NULL
    token       VARCHAR(64)  NOT NULL,              -- 서버가 SecureRandom으로 만든 식별 토큰 (Base64URL 43자)
    name        VARCHAR(50)  NOT NULL,
    is_active   TINYINT(1)   NOT NULL DEFAULT 1,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NULL,
    deleted_at  DATETIME(6)  NULL,                  -- 소프트 삭제
    PRIMARY KEY (id),
    CONSTRAINT uk_nfc_tags_token UNIQUE (token),
    -- 수업당 태그 1개: class_id는 NULL을 여러 개 허용하는 유니크라 미연결 태그끼리는 충돌하지 않는다.
    -- 삭제 전 반드시 연결을 해제해야 하므로(서비스에서 강제) 삭제된 행은 class_id가 NULL이다.
    CONSTRAINT uk_nfc_tags_class UNIQUE (class_id),
    CONSTRAINT fk_nfc_tags_teacher FOREIGN KEY (teacher_id) REFERENCES users (id),
    CONSTRAINT fk_nfc_tags_class   FOREIGN KEY (class_id)   REFERENCES classroom (id)
);
CREATE INDEX idx_nfc_tags_teacher ON nfc_tags (teacher_id);

-- ── 3) 이전 UID 방식 nfc_tags(class_id NOT NULL, tag_uid, registered_by)가 이미 운영 DB에 있는 경우
-- 새 방식은 UID를 쓰지 않으므로 이전 데이터는 이전하지 않고 테이블을 교체한다. 이전 방식으로 만든 데이터가 필요 없는지 확인 후 실행.
-- RENAME TABLE nfc_tags TO nfc_tags_legacy_uid;  -- 백업용 이름 변경 후 2)를 실행
-- attendances에는 태그 FK가 없으므로 과거 출석 기록은 영향을 받지 않는다.
