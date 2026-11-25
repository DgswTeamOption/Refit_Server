package com.option.refit.domain.chatbot.service;

import com.option.refit.domain.chatbot.dto.response.ChatbotStudentResponse;
import com.option.refit.domain.chatbot.service.impl.ChatbotStudentSearchServiceImpl;
import com.option.refit.domain.classroom.dto.response.ClassRoomStudentResponse;
import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.entity.ClassRoomMemberEntity;
import com.option.refit.domain.classroom.repository.ClassRoomMemberRepository;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.classroom.service.ClassRoomStudentListService;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.domain.user.entity.User;
import com.option.refit.domain.user.repository.UserRepository;
import com.option.refit.global.jwt.JwtPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatbotStudentSearchServiceTest {

    private final JwtPrincipal principal = new JwtPrincipal(1L, Role.TEACHER);

    @Mock ChatbotTeacherValidator teacherValidator;
    @Mock UserRepository userRepository;
    @Mock ClassRoomRepository classRoomRepository;
    @Mock ClassRoomMemberRepository memberRepository;
    @Mock ClassRoomStudentListService studentListService;
    @InjectMocks ChatbotStudentSearchServiceImpl service;

    @Test
    void skipsDetailLookupForClassesWithoutMatchingName() {
        User teacher = teacher();
        ClassRoomEntity mathClass = classroom(10L, "수학반");
        ClassRoomEntity englishClass = classroom(20L, "영어반");
        ClassRoomMemberEntity kim = member("김철수");
        ClassRoomMemberEntity lee = member("이영희");
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(teacher));
        when(classRoomRepository.findAllByTeacher_IdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(mathClass, englishClass));
        when(memberRepository.findAllByClassroom_IdAndLeftAtIsNullAndUser_DeletedAtIsNullOrderByUser_NameAsc(10L))
                .thenReturn(List.of(kim));
        when(memberRepository.findAllByClassroom_IdAndLeftAtIsNullAndUser_DeletedAtIsNullOrderByUser_NameAsc(20L))
                .thenReturn(List.of(lee));
        when(studentListService.getStudents(eq(10L), any())).thenReturn(List.of(
                new ClassRoomStudentResponse(100L, "김철수", null, 1L, 2L, null)));

        List<ChatbotStudentResponse> result = service.findMentionedStudents(principal, "김철수 알려줘");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("김철수");
        verify(studentListService, never()).getStudents(eq(20L), any());
    }

    @Test
    void doesNotLookUpStudentsWhenNoNameIsMentioned() {
        User teacher = teacher();
        ClassRoomEntity mathClass = classroom(10L, "수학반");
        ClassRoomMemberEntity kim = member("김철수");
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(teacher));
        when(classRoomRepository.findAllByTeacher_IdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(mathClass));
        when(memberRepository.findAllByClassroom_IdAndLeftAtIsNullAndUser_DeletedAtIsNullOrderByUser_NameAsc(10L))
                .thenReturn(List.of(kim));

        assertThat(service.findMentionedStudents(principal, "이번달 현황")).isEmpty();
        verify(studentListService, never()).getStudents(any(), any());
    }

    @Test
    void partialNameMatchesStudentWhoseNameContainsTheWord() {
        User teacher = teacher();
        ClassRoomEntity mathClass = classroom(10L, "수학반");
        ClassRoomMemberEntity kim = member("김철수");
        ClassRoomMemberEntity lee = member("이영희");
        when(userRepository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(teacher));
        when(classRoomRepository.findAllByTeacher_IdOrderByCreatedAtDesc(1L)).thenReturn(List.of(mathClass));
        when(memberRepository.findAllByClassroom_IdAndLeftAtIsNullAndUser_DeletedAtIsNullOrderByUser_NameAsc(10L))
                .thenReturn(List.of(kim, lee));
        when(studentListService.getStudents(eq(10L), any())).thenReturn(List.of(
                new ClassRoomStudentResponse(100L, "김철수", null, 1L, 2L, null),
                new ClassRoomStudentResponse(101L, "이영희", null, 0L, 0L, null)));

        List<ChatbotStudentResponse> result = service.findStudentsByPartialName(principal, "김");

        assertThat(result).extracting(ChatbotStudentResponse::name).containsExactly("김철수");
    }

    private static User teacher() {
        User user = org.mockito.Mockito.mock(User.class);
        when(user.getId()).thenReturn(1L);
        return user;
    }

    private static ClassRoomEntity classroom(Long id, String name) {
        ClassRoomEntity classroom = org.mockito.Mockito.mock(ClassRoomEntity.class);
        when(classroom.getId()).thenReturn(id);
        org.mockito.Mockito.lenient().when(classroom.getName()).thenReturn(name);
        return classroom;
    }

    private static ClassRoomMemberEntity member(String name) {
        User user = org.mockito.Mockito.mock(User.class);
        org.mockito.Mockito.lenient().when(user.getName()).thenReturn(name);
        ClassRoomMemberEntity member = org.mockito.Mockito.mock(ClassRoomMemberEntity.class);
        org.mockito.Mockito.lenient().when(member.getUser()).thenReturn(user);
        return member;
    }
}
