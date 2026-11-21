-- 요일 반복 일정 DB 변경 (MySQL). 이 저장소에는 마이그레이션 도구가 없으므로 수동 적용한다.
-- 적용 후 테이블 명세서·ERD에 같은 내용을 반영한다.

-- 1) 요일 규칙 (예: 월요일 16:00-17:00). 회차(schedules)는 이 규칙에서 자동 생성된다.
CREATE TABLE schedule_rules (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id    BIGINT      NOT NULL,        -- classroom.id
    student_id  BIGINT      NOT NULL,        -- users.id
    day_of_week VARCHAR(10) NOT NULL,        -- MONDAY ~ SUNDAY
    start_time  TIME        NOT NULL,
    end_time    TIME        NOT NULL,
    created_at  DATETIME(6) NOT NULL,
    updated_at  DATETIME(6),
    deleted_at  DATETIME(6),                 -- 소프트 삭제
    INDEX idx_schedule_rules_class_day (class_id, day_of_week)
);

-- 2) 회차가 어떤 규칙에서 만들어졌는지 표시 (직접 만든 날짜형 일정은 NULL)
ALTER TABLE schedules
    ADD COLUMN rule_id BIGINT NULL,
    ADD INDEX idx_schedules_rule_start (rule_id, start_time);
