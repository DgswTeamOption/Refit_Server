package com.option.refit.domain.chatbot.service;

import com.option.refit.domain.chatbot.dto.request.ChatbotQueryRequest;
import com.option.refit.domain.chatbot.dto.response.ChatbotQueryResponse;
import com.option.refit.domain.chatbot.dto.response.ChatbotQueryType;
import com.option.refit.domain.chatbot.service.impl.ChatbotQueryServiceImpl;
import com.option.refit.domain.chatbot.service.impl.ChatbotRecommendTimeServiceImpl;
import com.option.refit.domain.classroom.service.ClassRoomAccessValidator;
import com.option.refit.domain.makeup.repository.MakeupRepository;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.jwt.JwtPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// 추천 데이터가 없을 때 두 서비스가 같은 트랜잭션에서 실제로 200 응답을 만드는지 확인
// (서비스를 mock하면 rollback-only 문제를 잡지 못한다)
@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import({ChatbotQueryServiceImpl.class, ChatbotRecommendTimeServiceImpl.class, ChatbotTeacherValidator.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class ChatbotRecommendTransactionTest {

    @Autowired ChatbotQueryServiceImpl queryService;

    @MockitoBean ChatbotStudentSearchService studentSearchService;
    @MockitoBean ChatbotUnproposedStudentService unproposedStudentService;
    @MockitoBean ChatbotMonthlyStatusService monthlyStatusService;
    @MockitoBean ClassRoomAccessValidator accessValidator;
    @MockitoBean MakeupRepository makeupRepository;

    @Test
    void recommendWithoutDataReturnsEmptyListWithoutRollbackError() {
        when(studentSearchService.findMentionedStudents(any(), any())).thenReturn(List.of());
        when(makeupRepository.findPopularSlots(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(List.of());

        ChatbotQueryResponse response = queryService.query(
                new JwtPrincipal(1L, Role.TEACHER), new ChatbotQueryRequest("보강 시간 추천", null));

        assertThat(response.type()).isEqualTo(ChatbotQueryType.RECOMMEND_TIME);
        assertThat((List<?>) response.data()).isEmpty();
    }
}
