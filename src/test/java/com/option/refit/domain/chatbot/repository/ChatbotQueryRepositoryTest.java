package com.option.refit.domain.chatbot.repository;

import com.option.refit.domain.absence.entity.Absence;
import com.option.refit.domain.absence.entity.AbsenceStatus;
import com.option.refit.domain.absence.repository.AbsenceRepository;
import com.option.refit.domain.absence.repository.AbsenceRepository.UnproposedAbsenceSummary;
import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.entity.ClassRoomMemberEntity;
import com.option.refit.domain.classroom.repository.ClassRoomMemberRepository;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.makeup.entity.Makeup;
import com.option.refit.domain.makeup.entity.MakeupApprovalStatus;
import com.option.refit.domain.makeup.repository.MakeupRepository;
import com.option.refit.domain.makeup.repository.MakeupRepository.SlotApplicantCount;
import com.option.refit.domain.makeup.repository.MakeupRepository.TeacherStatusCount;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.domain.user.entity.User;
import com.option.refit.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// 챗봇이 쓰는 집계 쿼리(JPQL)가 실제로 동작하고 조건대로 걸러지는지 확인
@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ChatbotQueryRepositoryTest {

    @Autowired UserRepository userRepository;
    @Autowired ClassRoomRepository classRoomRepository;
    @Autowired ClassRoomMemberRepository memberRepository;
    @Autowired AbsenceRepository absenceRepository;
    @Autowired MakeupRepository makeupRepository;

    private User teacher;
    private ClassRoomEntity classroom;

    @BeforeEach
    void setUp() {
        teacher = user("teacher@test.com", "선생님", Role.TEACHER);
        classroom = classRoomRepository.save(ClassRoomEntity.create("수학반", teacher, "CODE0001"));
    }

    @Test
    void unproposedKeepsOnlyApprovedAbsenceStudentsWithoutActiveMakeup() {
        User noMakeup = joinedStudent("a@test.com", "무신청");
        User pending = joinedStudent("b@test.com", "대기중");
        User cancelled = joinedStudent("c@test.com", "취소함");
        User left = joinedStudent("d@test.com", "퇴장");
        User pendingAbsence = joinedStudent("e@test.com", "결석대기");
        memberRepository.findByClassroom_IdAndUser_Id(classroom.getId(), left.getId()).orElseThrow().leave();

        approvedAbsence(noMakeup, LocalDate.of(2026, 11, 3));
        approvedAbsence(noMakeup, LocalDate.of(2026, 11, 10));
        approvedAbsence(pending, LocalDate.of(2026, 11, 3));
        approvedAbsence(cancelled, LocalDate.of(2026, 11, 3));
        approvedAbsence(left, LocalDate.of(2026, 11, 3));
        absenceRepository.save(Absence.builder().studentId(pendingAbsence.getId()).classId(classroom.getId())
                .absenceDate(LocalDate.of(2026, 11, 3)).reason("r").build());

        makeup(pending, LocalDateTime.now().plusDays(1));
        makeupRepository.save(makeupEntity(cancelled, LocalDateTime.now().plusDays(1)));
        Makeup cancelledMakeup = makeupRepository.findAll().get(1);
        cancelledMakeup.cancel();
        makeupRepository.flush();

        List<UnproposedAbsenceSummary> result = absenceRepository.summarizeUnproposed(
                List.of(classroom.getId()), AbsenceStatus.APPROVED, MakeupApprovalStatus.INACTIVE);

        assertThat(result).extracting(UnproposedAbsenceSummary::getStudentId)
                .containsExactlyInAnyOrder(noMakeup.getId(), cancelled.getId());
        UnproposedAbsenceSummary first = result.stream()
                .filter(s -> s.getStudentId().equals(noMakeup.getId())).findFirst().orElseThrow();
        assertThat(first.getAbsenceCount()).isEqualTo(2);
        assertThat(first.getLastAbsenceDate()).isEqualTo(LocalDate.of(2026, 11, 10));
    }

    @Test
    void popularSlotsRankByApplicantCountAndSkipInactiveAndPast() {
        User s1 = joinedStudent("a@test.com", "가");
        User s2 = joinedStudent("b@test.com", "나");
        User s3 = joinedStudent("c@test.com", "다");
        LocalDateTime popular = LocalDateTime.now().plusDays(2).withNano(0);
        LocalDateTime second = popular.plusDays(1);
        LocalDateTime third = popular.plusDays(2);
        LocalDateTime fourth = popular.plusDays(3);

        makeup(s1, popular); makeup(s2, popular); makeup(s3, popular);
        makeup(s1, second); makeup(s2, second);
        makeup(s1, third);
        makeup(s1, fourth);
        makeup(s2, LocalDateTime.now().minusDays(1)); // 지난 시간
        makeupRepository.save(makeupEntity(s3, fourth)); // 아래에서 취소 처리
        makeupRepository.flush();
        List<Makeup> all = makeupRepository.findAll();
        all.get(all.size() - 1).cancel();
        makeupRepository.flush();

        List<SlotApplicantCount> top = makeupRepository.findPopularSlots(
                teacher.getId(), null, MakeupApprovalStatus.INACTIVE, LocalDateTime.now(), PageRequest.of(0, 3));

        assertThat(top).extracting(SlotApplicantCount::getMakeupTime).containsExactly(popular, second, third);
        assertThat(top).extracting(SlotApplicantCount::getApplicantCount).containsExactly(3L, 2L, 1L);

        assertThat(makeupRepository.findPopularSlots(
                teacher.getId(), classroom.getId() + 99, MakeupApprovalStatus.INACTIVE,
                LocalDateTime.now(), PageRequest.of(0, 3))).isEmpty();
    }

    @Test
    void teacherStatusCountsGroupByApprovalStatusWithinRange() {
        User s1 = joinedStudent("a@test.com", "가");
        LocalDateTime inRange = LocalDateTime.of(2026, 11, 20, 15, 0);
        makeupRepository.save(makeupEntity(s1, inRange));
        Makeup approved = makeupRepository.save(makeupEntity(s1, inRange.plusDays(1)));
        approved.approve();
        makeupRepository.save(makeupEntity(s1, LocalDateTime.of(2026, 12, 1, 9, 0))); // 범위 밖
        makeupRepository.flush();

        List<TeacherStatusCount> counts = makeupRepository.countByTeacherAndStatus(
                teacher.getId(), LocalDateTime.of(2026, 11, 1, 0, 0), LocalDateTime.of(2026, 12, 1, 0, 0));

        assertThat(counts).extracting(TeacherStatusCount::getApprovalStatus)
                .containsExactlyInAnyOrder(MakeupApprovalStatus.PENDING, MakeupApprovalStatus.APPROVED);
        assertThat(counts).allSatisfy(c -> assertThat(c.getCount()).isEqualTo(1));
    }

    private User user(String email, String name, Role role) {
        return userRepository.save(User.builder().emailId(email).name(name).role(role).build());
    }

    private User joinedStudent(String email, String name) {
        User student = user(email, name, Role.STUDENT);
        memberRepository.save(ClassRoomMemberEntity.create(classroom, student));
        return student;
    }

    private void approvedAbsence(User student, LocalDate date) {
        Absence absence = Absence.builder().studentId(student.getId()).classId(classroom.getId())
                .absenceDate(date).reason("사유").build();
        absence.approve();
        absenceRepository.save(absence);
    }

    private Makeup makeupEntity(User student, LocalDateTime time) {
        return Makeup.builder().studentId(student.getId()).teacherId(teacher.getId())
                .classId(classroom.getId()).scheduleId(1L).makeupTime(time).build();
    }

    private void makeup(User student, LocalDateTime time) {
        makeupRepository.save(makeupEntity(student, time));
    }
}
