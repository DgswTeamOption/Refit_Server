-- 조회 성능용 인덱스 (MySQL). 이 저장소에는 마이그레이션 도구가 없으므로 수동 적용한다.
-- 적용 전에 SHOW INDEX FROM <테이블>; 로 같은 컬럼의 인덱스가 이미 있는지 확인하고, 있으면 해당 줄은 건너뛴다.
-- 컬럼명은 엔티티(snake_case) 기준이며, 환경에 따라 다르면 SHOW CREATE TABLE 결과에 맞춘다.

-- 월별/기간별 일정 조회 (class_id + start_time 범위)
CREATE INDEX idx_schedules_class_start ON schedules (class_id, start_time);

-- 수업별 공지 목록 (최신순)
CREATE INDEX idx_notices_class_created ON notices (class_id, created_at);

-- 수업별 / 학생별 결석 목록
CREATE INDEX idx_absences_class_date ON absences (class_id, absence_date);
CREATE INDEX idx_absences_student_date ON absences (student_id, absence_date);

-- 수업별 보강 신청
CREATE INDEX idx_makeups_class ON makeups (class_id);

-- 교사가 만든 수업 목록 (최신순)
CREATE INDEX idx_classroom_teacher_created ON classroom (teacher_id, created_at);

-- 출석 일자별 조회 (attendances는 (schedule_id, student_id) 유니크 인덱스가 이미 있다)
CREATE INDEX idx_attendances_class_date ON attendances (class_id, attendance_date);

-- 학생이 참여 중인 수업 목록 (classroom_members는 (classroom_id, user_id) 유니크만 있어 user_id 선두 조회에는 별도 인덱스가 필요하다)
CREATE INDEX idx_classroom_members_user ON classroom_members (user_id, left_at);

-- 챗봇 보강 시간 추천·월말 현황 (선생님별 보강 시각 범위/집계)
CREATE INDEX idx_makeups_teacher_time ON makeups (teacher_id, makeup_time);
