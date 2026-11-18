package com.option.refit.domain.chatbot.service.impl;

import com.option.refit.domain.chatbot.dto.response.ChatbotStudentResponse;
import com.option.refit.domain.chatbot.service.ChatbotStudentSearchService;
import com.option.refit.domain.chatbot.service.ChatbotTeacherValidator;
import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.classroom.service.ClassRoomStudentListService;
import com.option.refit.domain.user.entity.User;
import com.option.refit.domain.user.repository.UserRepository;
import com.option.refit.global.error.chatbotError.exception.ChatbotStudentNotFoundException;
import com.option.refit.global.error.chatbotError.exception.InvalidSearchNameException;
import com.option.refit.global.error.userError.exception.UserNotFoundException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatbotStudentSearchServiceImpl implements ChatbotStudentSearchService {

    private final ChatbotTeacherValidator teacherValidator;
    private final UserRepository userRepository;
    private final ClassRoomRepository classRoomRepository;
    private final ClassRoomStudentListService studentListService;

    @Override
    public List<ChatbotStudentResponse> searchStudents(JwtPrincipal principal, String name) {
        teacherValidator.requireTeacher(principal);

        // 이름이 비어 있으면 모든 학생이 매칭되므로 400
        if (name == null || name.isBlank()) {
            throw new InvalidSearchNameException();
        }

        String keyword = name.trim().toLowerCase(Locale.ROOT);
        List<ChatbotStudentResponse> found = collectStudents(
                principal, student -> student.name().toLowerCase(Locale.ROOT).contains(keyword));

        if (found.isEmpty()) {
            throw new ChatbotStudentNotFoundException();
        }
        return found;
    }

    @Override
    public List<ChatbotStudentResponse> findMentionedStudents(JwtPrincipal principal, String message) {
        teacherValidator.requireTeacher(principal);

        String text = message.toLowerCase(Locale.ROOT);
        return collectStudents(principal, student -> text.contains(student.name().toLowerCase(Locale.ROOT)));
    }

    // 선생님의 모든 수업에서 학생 목록(결석·보강 횟수 포함)을 기존 수업 학생 조회 서비스로 가져와 조건에 맞는 학생만 남긴다
    private List<ChatbotStudentResponse> collectStudents(
            JwtPrincipal principal, Predicate<ChatbotStudentResponse> filter) {
        User teacher = userRepository.findByIdAndDeletedAtIsNull(principal.userId())
                .orElseThrow(UserNotFoundException::new);

        List<ClassRoomEntity> classes = classRoomRepository.findAllByTeacher_IdOrderByCreatedAtDesc(teacher.getId());

        return classes.stream()
                .flatMap(classroom -> studentListService.getStudents(classroom.getId(), teacher).stream()
                        .map(student -> new ChatbotStudentResponse(
                                student.studentId(),
                                student.name(),
                                student.profileImageUrl(),
                                classroom.getId(),
                                classroom.getName(),
                                student.absenceCount(),
                                student.makeupCount(),
                                student.lastAbsenceDate())))
                .filter(filter)
                .toList();
    }
}
