package com.option.refit.domain.classroom.service.impl;

import com.option.refit.domain.absence.entity.AbsenceStatus;
import com.option.refit.domain.absence.repository.AbsenceRepository;
import com.option.refit.domain.absence.repository.AbsenceRepository.StudentAbsenceSummary;
import com.option.refit.domain.classroom.dto.response.ClassRoomStudentResponse;
import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.entity.ClassRoomMemberEntity;
import com.option.refit.domain.classroom.repository.ClassRoomMemberRepository;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.classroom.service.ClassRoomStudentListService;
import com.option.refit.domain.makeup.entity.MakeupApprovalStatus;
import com.option.refit.domain.makeup.repository.MakeupRepository;
import com.option.refit.domain.makeup.repository.MakeupRepository.StudentMakeupSummary;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.domain.user.entity.User;
import com.option.refit.global.error.classError.exception.ClassAccessDeniedException;
import com.option.refit.global.error.classError.exception.ClassNotFoundException;
import com.option.refit.global.error.classError.exception.ClassPermissionDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassRoomStudentListServiceImpl implements ClassRoomStudentListService {

    private final ClassRoomRepository classroomRepository;
    private final ClassRoomMemberRepository memberRepository;
    private final AbsenceRepository absenceRepository;
    private final MakeupRepository makeupRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ClassRoomStudentResponse> getStudents(Long classroomId, User user) {

        // 교사만 학생 목록을 조회할 수 있다.
        if (user.getRole() != Role.TEACHER) {
            throw new ClassPermissionDeniedException();
        }

        // 수업이 존재하는지 확인
        ClassRoomEntity classroom = classroomRepository
                .findById(classroomId)
                .orElseThrow(ClassNotFoundException::new);

        // 담당 교사(수업 생성자)가 아니면 접근 불가
        if (!classroom.getTeacher().getId().equals(user.getId())) {
            throw new ClassAccessDeniedException();
        }

        // 현재 참여 중인 학생 목록 (이름순)
        List<ClassRoomMemberEntity> members = memberRepository
                .findAllByClassroom_IdAndLeftAtIsNullAndUser_DeletedAtIsNullOrderByUser_NameAsc(classroomId);

        if (members.isEmpty()) {
            return List.of();
        }

        List<Long> studentIds = members.stream()
                .map(member -> member.getUser().getId())
                .toList();

        // 학생별 결석 횟수·최근 결석일 (승인된 결석만 집계)
        Map<Long, StudentAbsenceSummary> absenceByStudent = absenceRepository
                .summarizeByStudent(classroomId, AbsenceStatus.APPROVED, studentIds)
                .stream()
                .collect(Collectors.toMap(StudentAbsenceSummary::getStudentId, Function.identity()));

        // 학생별 보강 횟수 (승인된 보강만 집계)
        Map<Long, Long> makeupCountByStudent = makeupRepository
                .summarizeByStudent(classroomId, MakeupApprovalStatus.APPROVED, studentIds)
                .stream()
                .collect(Collectors.toMap(
                        StudentMakeupSummary::getStudentId,
                        StudentMakeupSummary::getMakeupCount
                ));

        return members.stream()
                .map(member -> {
                    User student = member.getUser();
                    StudentAbsenceSummary absence = absenceByStudent.get(student.getId());

                    return new ClassRoomStudentResponse(
                            student.getId(),
                            student.getName(),
                            student.getProfileImageUrl(),
                            makeupCountByStudent.getOrDefault(student.getId(), 0L),
                            absence != null ? absence.getAbsenceCount() : 0L,
                            absence != null ? absence.getLastAbsenceDate() : null
                    );
                })
                .toList();
    }
}
