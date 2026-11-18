package com.option.refit.domain.chatbot.service.impl;

import com.option.refit.domain.absence.entity.AbsenceStatus;
import com.option.refit.domain.absence.repository.AbsenceRepository;
import com.option.refit.domain.chatbot.dto.response.ChatbotMonthlyStatusResponse;
import com.option.refit.domain.chatbot.service.ChatbotMonthlyStatusService;
import com.option.refit.domain.chatbot.service.ChatbotTeacherValidator;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.makeup.entity.MakeupApprovalStatus;
import com.option.refit.domain.makeup.repository.MakeupRepository;
import com.option.refit.domain.makeup.repository.MakeupRepository.TeacherStatusCount;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatbotMonthlyStatusServiceImpl implements ChatbotMonthlyStatusService {

    private final ChatbotTeacherValidator teacherValidator;
    private final ClassRoomRepository classRoomRepository;
    private final AbsenceRepository absenceRepository;
    private final MakeupRepository makeupRepository;

    @Override
    public ChatbotMonthlyStatusResponse getMonthlyStatus(JwtPrincipal principal) {
        teacherValidator.requireTeacher(principal);

        LocalDate today = LocalDate.now();
        LocalDate firstDay = today.withDayOfMonth(1);
        LocalDate lastDay = today.withDayOfMonth(today.lengthOfMonth());

        List<Long> classIds = classRoomRepository.findIdsByTeacherId(principal.userId());

        // 수업이 없으면 IN 절이 비어 쿼리가 깨질 수 있으므로 결석은 0으로 처리
        long absenceCount = classIds.isEmpty() ? 0L
                : absenceRepository.countByClassIdInAndStatusAndAbsenceDateBetween(
                        classIds, AbsenceStatus.APPROVED, firstDay, lastDay);

        // 보강은 보강 시각이 이번 달에 속하는 건 (승인 / 대기)
        List<TeacherStatusCount> makeupCounts = makeupRepository.countByTeacherAndStatus(
                principal.userId(), firstDay.atStartOfDay(), lastDay.plusDays(1).atStartOfDay());

        return new ChatbotMonthlyStatusResponse(
                today.getYear(),
                today.getMonthValue(),
                absenceCount,
                countOf(makeupCounts, MakeupApprovalStatus.APPROVED),
                countOf(makeupCounts, MakeupApprovalStatus.PENDING)
        );
    }

    private long countOf(List<TeacherStatusCount> counts, MakeupApprovalStatus status) {
        return counts.stream()
                .filter(count -> count.getApprovalStatus() == status)
                .mapToLong(TeacherStatusCount::getCount)
                .sum();
    }
}
