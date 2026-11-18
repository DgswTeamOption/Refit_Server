package com.option.refit.domain.chatbot.service;

import com.option.refit.domain.chatbot.dto.request.ChatbotQueryRequest;
import com.option.refit.domain.chatbot.dto.response.ChatbotMonthlyStatusResponse;
import com.option.refit.domain.chatbot.dto.response.ChatbotQueryResponse;
import com.option.refit.domain.chatbot.dto.response.ChatbotQueryType;
import com.option.refit.domain.chatbot.dto.response.ChatbotStudentResponse;
import com.option.refit.domain.chatbot.service.impl.ChatbotQueryServiceImpl;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.chatbotError.exception.NoRecommendationDataException;
import com.option.refit.global.jwt.JwtPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatbotQueryServiceTest {

    private final JwtPrincipal teacher = new JwtPrincipal(1L, Role.TEACHER);

    @Mock ChatbotStudentSearchService studentSearchService;
    @Mock ChatbotRecommendTimeService recommendTimeService;
    @Mock ChatbotUnproposedStudentService unproposedStudentService;
    @Mock ChatbotMonthlyStatusService monthlyStatusService;
    @InjectMocks ChatbotQueryServiceImpl service;

    private ChatbotQueryResponse query(String message) {
        return service.query(teacher, new ChatbotQueryRequest(message, null));
    }

    @Test
    void studentNameTakesPriorityOverKeywords() {
        when(studentSearchService.findMentionedStudents(eq(teacher), any())).thenReturn(List.of(
                new ChatbotStudentResponse(2L, "김민수", null, 10L, "수학반", 1, 0, null)));

        ChatbotQueryResponse response = query("김민수 추천");

        assertThat(response.type()).isEqualTo(ChatbotQueryType.STUDENT);
        verify(recommendTimeService, never()).recommend(any(), any());
    }

    @Test
    void routesRecommendKeyword() {
        when(studentSearchService.findMentionedStudents(eq(teacher), any())).thenReturn(List.of());
        when(recommendTimeService.recommend(teacher, null)).thenReturn(List.of());

        assertThat(query("보강 시간 추천해줘").type()).isEqualTo(ChatbotQueryType.RECOMMEND_TIME);
    }

    @Test
    void recommendWithoutDataReturnsFriendlyMessageInsteadOfError() {
        when(studentSearchService.findMentionedStudents(eq(teacher), any())).thenReturn(List.of());
        when(recommendTimeService.recommend(teacher, null)).thenThrow(new NoRecommendationDataException());

        ChatbotQueryResponse response = query("추천");

        assertThat(response.type()).isEqualTo(ChatbotQueryType.RECOMMEND_TIME);
        assertThat(response.data()).isEqualTo(List.of());
    }

    @Test
    void routesUnproposedKeyword() {
        when(studentSearchService.findMentionedStudents(eq(teacher), any())).thenReturn(List.of());
        when(unproposedStudentService.getUnproposedStudents(teacher)).thenReturn(List.of());

        assertThat(query("미제안 학생 알려줘").type()).isEqualTo(ChatbotQueryType.UNPROPOSED_STUDENTS);
    }

    @Test
    void routesMonthlyKeywords() {
        when(studentSearchService.findMentionedStudents(eq(teacher), any())).thenReturn(List.of());
        when(monthlyStatusService.getMonthlyStatus(teacher))
                .thenReturn(new ChatbotMonthlyStatusResponse(2026, 11, 3, 2, 1));

        assertThat(query("이번달 어때").type()).isEqualTo(ChatbotQueryType.MONTHLY_STATUS);
        assertThat(query("출결 현황").type()).isEqualTo(ChatbotQueryType.MONTHLY_STATUS);
    }

    @Test
    void unmatchedMessageReturnsGuide() {
        when(studentSearchService.findMentionedStudents(eq(teacher), any())).thenReturn(List.of());

        ChatbotQueryResponse response = query("안녕");

        assertThat(response.type()).isEqualTo(ChatbotQueryType.GUIDE);
        assertThat(response.data()).isNull();
    }
}
