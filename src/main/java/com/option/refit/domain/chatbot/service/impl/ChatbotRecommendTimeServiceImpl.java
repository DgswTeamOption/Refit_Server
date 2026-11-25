package com.option.refit.domain.chatbot.service.impl;

import com.option.refit.domain.chatbot.dto.response.ChatbotRecommendedTimeResponse;
import com.option.refit.domain.chatbot.service.ChatbotRecommendTimeService;
import com.option.refit.domain.chatbot.service.ChatbotTeacherValidator;
import com.option.refit.domain.classroom.service.ClassRoomAccessValidator;
import com.option.refit.domain.makeup.entity.MakeupApprovalStatus;
import com.option.refit.domain.makeup.repository.MakeupRepository;
import com.option.refit.domain.makeup.repository.MakeupRepository.SlotApplicantCount;
import com.option.refit.global.error.chatbotError.exception.NoRecommendationDataException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatbotRecommendTimeServiceImpl implements ChatbotRecommendTimeService {

    private static final int TOP_COUNT = 3;

    private final ChatbotTeacherValidator teacherValidator;
    private final ClassRoomAccessValidator accessValidator;
    private final MakeupRepository makeupRepository;

    // 챗봇 쪽에서 이 예외를 잡아 정상 응답으로 바꾸므로, 같은 트랜잭션이 rollback-only로 표시되지 않게 한다
    @Override
    @Transactional(readOnly = true, noRollbackFor = NoRecommendationDataException.class)
    public List<ChatbotRecommendedTimeResponse> recommend(JwtPrincipal principal, Long classId) {
        teacherValidator.requireTeacher(principal);

        // 수업을 지정하면 없는 수업은 404, 담당 선생님이 아니면 403
        if (classId != null) {
            accessValidator.requireTeacher(classId, principal.userId());
        }

        List<SlotApplicantCount> slots = makeupRepository.findPopularSlots(
                principal.userId(), classId, MakeupApprovalStatus.INACTIVE,
                LocalDateTime.now(), PageRequest.of(0, TOP_COUNT));

        if (slots.isEmpty()) {
            throw new NoRecommendationDataException();
        }

        return IntStream.range(0, slots.size())
                .mapToObj(i -> new ChatbotRecommendedTimeResponse(
                        i + 1,
                        slots.get(i).getMakeupTime(),
                        slots.get(i).getMakeupTime().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN),
                        slots.get(i).getApplicantCount()))
                .toList();
    }
}
