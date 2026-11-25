package com.option.refit.domain.chatbot.service.impl;

import com.option.refit.domain.chatbot.dto.request.ChatbotQueryRequest;
import com.option.refit.domain.chatbot.dto.response.ChatbotMonthlyStatusResponse;
import com.option.refit.domain.chatbot.dto.response.ChatbotQueryResponse;
import com.option.refit.domain.chatbot.dto.response.ChatbotQueryType;
import com.option.refit.domain.chatbot.dto.response.ChatbotRecommendedTimeResponse;
import com.option.refit.domain.chatbot.dto.response.ChatbotStudentResponse;
import com.option.refit.domain.chatbot.dto.response.ChatbotUnproposedStudentResponse;
import com.option.refit.domain.chatbot.service.ChatbotMonthlyStatusService;
import com.option.refit.domain.chatbot.service.ChatbotQueryService;
import com.option.refit.domain.chatbot.service.ChatbotRecommendTimeService;
import com.option.refit.domain.chatbot.service.ChatbotStudentSearchService;
import com.option.refit.domain.chatbot.service.ChatbotUnproposedStudentService;
import com.option.refit.global.error.chatbotError.exception.NoRecommendationDataException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 1차 버전: LLM 없이 키워드로 라우팅한다. 우선순위는 학생 이름 > 추천 > 미제안 > 이번달/현황 > 이름 일부 > 안내
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatbotQueryServiceImpl implements ChatbotQueryService {

    static final String GUIDE_MESSAGE =
            "학생 이름을 입력하거나 '보강 시간 추천', '미제안 학생', '이번달 현황' 중 하나를 입력해 주세요.";

    private final ChatbotStudentSearchService studentSearchService;
    private final ChatbotRecommendTimeService recommendTimeService;
    private final ChatbotUnproposedStudentService unproposedStudentService;
    private final ChatbotMonthlyStatusService monthlyStatusService;

    @Override
    public ChatbotQueryResponse query(JwtPrincipal principal, ChatbotQueryRequest request) {
        String message = request.message().trim();

        List<ChatbotStudentResponse> students = studentSearchService.findMentionedStudents(principal, message);
        if (!students.isEmpty()) {
            return new ChatbotQueryResponse(ChatbotQueryType.STUDENT,
                    "학생 정보 " + students.size() + "건을 찾았어요.", students);
        }

        if (message.contains("추천")) {
            return recommend(principal, request.classId());
        }

        if (message.contains("미제안")) {
            List<ChatbotUnproposedStudentResponse> unproposed = unproposedStudentService.getUnproposedStudents(principal);
            String text = unproposed.isEmpty()
                    ? "보강 시간이 제안되지 않은 학생이 없어요."
                    : "보강 시간이 아직 제안되지 않은 학생이 " + unproposed.size() + "건 있어요.";
            return new ChatbotQueryResponse(ChatbotQueryType.UNPROPOSED_STUDENTS, text, unproposed);
        }

        if (message.contains("이번달") || message.contains("이번 달") || message.contains("현황")) {
            ChatbotMonthlyStatusResponse status = monthlyStatusService.getMonthlyStatus(principal);
            String text = status.month() + "월 결석 " + status.absenceCount() + "건, 보강 "
                    + status.makeupCount() + "건(대기 " + status.pendingMakeupCount() + "건)이에요.";
            return new ChatbotQueryResponse(ChatbotQueryType.MONTHLY_STATUS, text, status);
        }

        // 키워드도 없으면 이름 일부만 입력한 경우(예: "김")로 보고 부분 일치 학생을 찾는다
        List<ChatbotStudentResponse> partialMatches = studentSearchService.findStudentsByPartialName(principal, message);
        if (!partialMatches.isEmpty()) {
            return new ChatbotQueryResponse(ChatbotQueryType.STUDENT,
                    "학생 정보 " + partialMatches.size() + "건을 찾았어요.", partialMatches);
        }

        return new ChatbotQueryResponse(ChatbotQueryType.GUIDE, GUIDE_MESSAGE, null);
    }

    // 추천은 대화 화면이라 데이터가 없어도 에러 대신 안내 문구로 응답한다 (단독 API는 404)
    private ChatbotQueryResponse recommend(JwtPrincipal principal, Long classId) {
        try {
            List<ChatbotRecommendedTimeResponse> times = recommendTimeService.recommend(principal, classId);
            return new ChatbotQueryResponse(ChatbotQueryType.RECOMMEND_TIME,
                    "신청이 많은 보강 시간 TOP " + times.size() + "를 추천해요.", times);
        } catch (NoRecommendationDataException e) {
            return new ChatbotQueryResponse(ChatbotQueryType.RECOMMEND_TIME,
                    "아직 추천할 보강 신청 데이터가 없어요.", List.of());
        }
    }
}
