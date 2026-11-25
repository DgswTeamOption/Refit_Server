package com.option.refit.domain.chatbot.service.impl;

import com.option.refit.domain.chatbot.dto.response.ChatbotStudentResponse;
import com.option.refit.domain.chatbot.service.ChatbotStudentSearchService;
import com.option.refit.domain.chatbot.service.ChatbotTeacherValidator;
import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.repository.ClassRoomMemberRepository;
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
    private final ClassRoomMemberRepository memberRepository;
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
                principal, studentName -> studentName.toLowerCase(Locale.ROOT).contains(keyword));

        if (found.isEmpty()) {
            throw new ChatbotStudentNotFoundException();
        }
        return found;
    }

    @Override
    public List<ChatbotStudentResponse> findMentionedStudents(JwtPrincipal principal, String message) {
        teacherValidator.requireTeacher(principal);

        String text = message.toLowerCase(Locale.ROOT);
        return collectStudents(principal, studentName -> text.contains(studentName.toLowerCase(Locale.ROOT)));
    }

    // 수업마다 이름만 먼저 확인하고, 이름이 맞는 학생이 있는 수업만 결석·보강 횟수까지 조회한다
    private List<ChatbotStudentResponse> collectStudents(JwtPrincipal principal, Predicate<String> nameFilter) {
        User teacher = userRepository.findByIdAndDeletedAtIsNull(principal.userId())
                .orElseThrow(UserNotFoundException::new);

        List<ClassRoomEntity> classes = classRoomRepository.findAllByTeacher_IdOrderByCreatedAtDesc(teacher.getId());

        return classes.stream()
                .filter(classroom -> hasMatchingStudent(classroom, nameFilter))
                .flatMap(classroom -> studentListService.getStudents(classroom.getId(), teacher).stream()
                        .filter(student -> nameFilter.test(student.name()))
                        .map(student -> new ChatbotStudentResponse(
                                student.studentId(),
                                student.name(),
                                student.profileImageUrl(),
                                classroom.getId(),
                                classroom.getName(),
                                student.absenceCount(),
                                student.makeupCount(),
                                student.lastAbsenceDate())))
                .toList();
    }

    private boolean hasMatchingStudent(ClassRoomEntity classroom, Predicate<String> nameFilter) {
        return memberRepository
                .findAllByClassroom_IdAndLeftAtIsNullAndUser_DeletedAtIsNullOrderByUser_NameAsc(classroom.getId())
                .stream()
                .anyMatch(member -> nameFilter.test(member.getUser().getName()));
    }
}
