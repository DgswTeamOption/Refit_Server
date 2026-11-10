package com.option.refit.domain.classroom.service.impl;

import com.option.refit.domain.absence.entity.Absence;
import com.option.refit.domain.absence.entity.AbsenceStatus;
import com.option.refit.domain.absence.repository.AbsenceRepository;
import com.option.refit.domain.classroom.dto.response.ClassRoomStudentResponse;
import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.entity.ClassRoomMemberEntity;
import com.option.refit.domain.classroom.repository.ClassRoomMemberRepository;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.classroom.service.ClassRoomStudentService;
import com.option.refit.domain.makeup.entity.Makeup;
import com.option.refit.domain.makeup.entity.MakeupApprovalStatus;
import com.option.refit.domain.makeup.repository.MakeupRepository;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.domain.user.entity.User;
import com.option.refit.global.error.classError.exception.ClassAccessDeniedException;
import com.option.refit.global.error.classError.exception.ClassNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassRoomStudentServiceImpl implements ClassRoomStudentService {

    private final ClassRoomRepository classRoomRepository;
    private final ClassRoomMemberRepository classRoomMemberRepository;
    private final AbsenceRepository absenceRepository;
    private final MakeupRepository makeupRepository;

    @Override
    public List<ClassRoomStudentResponse> getStudents(Long classroomId, User user) {

        // 삭제되지 않은 수업인지 확인
        ClassRoomEntity classRoom = classRoomRepository.findById(classroomId)
                .orElseThrow(ClassNotFoundException::new);

        // 수업을 생성한 교사만 학생별 통계를 조회
        if (user.getRole() != Role.TEACHER
                || !Objects.equals(classRoom.getTeacher().getId(), user.getId())) {
            throw new ClassAccessDeniedException();
        }

        // 현재 수업에 참여 중인 학생과 User를 한 번에 조회
        List<ClassRoomMemberEntity> members =
                classRoomMemberRepository.findActiveMembersWithUser(classroomId);

        // 참여 중인 학생이 없으면 빈 목록을 반환
        if (members.isEmpty()) {
            return List.of();
        }

        // 승인된 결석 내역을 수업 단위로 한 번에 조회
        List<Absence> absences = absenceRepository
                .findByClassIdAndStatusOrderByAbsenceDateDesc(
                        classroomId, AbsenceStatus.APPROVED
                );

        // 승인된 보강 내역을 수업 단위로 한 번에 조회
        List<Makeup> makeups = makeupRepository.findList(
                classroomId, MakeupApprovalStatus.APPROVED, null
        );

        // 학생별 결석 횟수와 최근 결석일을 메모리에서 집계
        Map<Long, Long> absenceCounts = new HashMap<>();
        Map<Long, LocalDate> recentAbsenceDates = new HashMap<>();

        for (Absence absence : absences) {
            Long studentId = absence.getStudentId();
            absenceCounts.merge(studentId, 1L, Long::sum);
            recentAbsenceDates.merge(
                    studentId,
                    absence.getAbsenceDate(),
                    (current, candidate) -> current.isAfter(candidate) ? current : candidate
            );
        }

        // 학생별 승인된 보강 횟수를 집계
        Map<Long, Long> makeupCounts = new HashMap<>();
        for (Makeup makeup : makeups) {
            makeupCounts.merge(makeup.getStudentId(), 1L, Long::sum);
        }

        // 현재 참여 중인 학생만 응답 DTO로 변환
        return members.stream()
                .map(member -> toResponse(
                        member,
                        absenceCounts,
                        recentAbsenceDates,
                        makeupCounts
                ))
                .toList();
    }

    private ClassRoomStudentResponse toResponse(
            ClassRoomMemberEntity member,
            Map<Long, Long> absenceCounts,
            Map<Long, LocalDate> recentAbsenceDates,
            Map<Long, Long> makeupCounts
    ) {
        Long studentId = member.getUser().getId();
        long absenceCount = absenceCounts.getOrDefault(studentId, 0L);
        long makeupCount = makeupCounts.getOrDefault(studentId, 0L);

        // 현재 프로젝트에는 보강 완료 상태가 없어 승인된 보강 건수를 처리된 보강으로 간주
        long makeupRequiredDays = Math.max(0L, absenceCount - makeupCount);

        return new ClassRoomStudentResponse(
                studentId,
                member.getUser().getName(),
                makeupCount,
                absenceCount,
                recentAbsenceDates.get(studentId),
                makeupRequiredDays > 0,
                makeupRequiredDays
        );
    }
}