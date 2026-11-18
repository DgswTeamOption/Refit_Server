package com.option.refit.domain.chatbot.service.impl;

import com.option.refit.domain.absence.entity.AbsenceStatus;
import com.option.refit.domain.absence.repository.AbsenceRepository;
import com.option.refit.domain.absence.repository.AbsenceRepository.UnproposedAbsenceSummary;
import com.option.refit.domain.chatbot.dto.response.ChatbotUnproposedStudentResponse;
import com.option.refit.domain.chatbot.service.ChatbotTeacherValidator;
import com.option.refit.domain.chatbot.service.ChatbotUnproposedStudentService;
import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.makeup.entity.MakeupApprovalStatus;
import com.option.refit.domain.user.entity.User;
import com.option.refit.domain.user.repository.UserRepository;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatbotUnproposedStudentServiceImpl implements ChatbotUnproposedStudentService {

    private final ChatbotTeacherValidator teacherValidator;
    private final ClassRoomRepository classRoomRepository;
    private final AbsenceRepository absenceRepository;
    private final UserRepository userRepository;

    @Override
    public List<ChatbotUnproposedStudentResponse> getUnproposedStudents(JwtPrincipal principal) {
        teacherValidator.requireTeacher(principal);

        List<ClassRoomEntity> classes = classRoomRepository.findAllByTeacher_IdOrderByCreatedAtDesc(principal.userId());
        if (classes.isEmpty()) {
            return List.of();
        }

        Map<Long, String> classNames = classes.stream()
                .collect(Collectors.toMap(ClassRoomEntity::getId, ClassRoomEntity::getName));

        List<UnproposedAbsenceSummary> summaries = absenceRepository.summarizeUnproposed(
                classNames.keySet(), AbsenceStatus.APPROVED, MakeupApprovalStatus.INACTIVE);
        if (summaries.isEmpty()) {
            return List.of();
        }

        List<Long> studentIds = summaries.stream().map(UnproposedAbsenceSummary::getStudentId).distinct().toList();
        Map<Long, User> students = userRepository.findAllById(studentIds).stream()
                .filter(user -> user.getDeletedAt() == null) // 탈퇴한 학생 제외
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return summaries.stream()
                .filter(summary -> students.containsKey(summary.getStudentId()))
                .map(summary -> {
                    User student = students.get(summary.getStudentId());
                    return new ChatbotUnproposedStudentResponse(
                            student.getId(),
                            student.getName(),
                            student.getProfileImageUrl(),
                            summary.getClassId(),
                            classNames.get(summary.getClassId()),
                            summary.getAbsenceCount(),
                            summary.getLastAbsenceDate());
                })
                .toList();
    }
}
