package com.option.refit.domain.schedule.service;

import com.option.refit.domain.classroom.service.ClassRoomAccessValidator;
import com.option.refit.domain.schedule.dto.ScheduleRuleRequest.ScheduleRuleCreateRequest;
import com.option.refit.domain.schedule.dto.ScheduleRuleRequest.ScheduleRuleUpdateRequest;
import com.option.refit.domain.schedule.dto.ScheduleRuleResponse;
import com.option.refit.domain.schedule.entity.ScheduleRuleEntity;
import com.option.refit.domain.schedule.repository.ScheduleRuleRepository;
import com.option.refit.domain.schedule.rule.ScheduleRuleGenerator;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.scheduleError.exception.InvalidDateFormatException;
import com.option.refit.global.error.scheduleError.exception.ScheduleOverlapException;
import com.option.refit.global.error.scheduleError.exception.ScheduleRuleNotFoundException;
import com.option.refit.global.error.scheduleError.exception.StudentNotInClassException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

// 요일 반복 규칙 등록·수정·삭제·조회. 등록/수정/삭제는 담당 선생님만 가능하다.
@Service
@RequiredArgsConstructor
public class ScheduleRuleService {

    private final ScheduleRuleRepository ruleRepository;
    private final ScheduleRuleGenerator generator;
    private final ClassRoomAccessValidator accessValidator;

    @Transactional
    public Long create(JwtPrincipal principal, Long classId, ScheduleRuleCreateRequest request) {
        accessValidator.requireTeacher(classId, principal.userId());
        if (!accessValidator.isActiveMember(classId, request.getStudentId())) {
            throw new StudentNotInClassException();
        }
        validate(classId, request.getDayOfWeek(), request.getStartTime(), request.getEndTime(), null);

        ScheduleRuleEntity rule = ruleRepository.save(ScheduleRuleEntity.builder()
                .classId(classId)
                .studentId(request.getStudentId())
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build());

        generator.generate(rule);
        return rule.getId();
    }

    @Transactional
    public void update(JwtPrincipal principal, Long classId, Long ruleId, ScheduleRuleUpdateRequest request) {
        accessValidator.requireTeacher(classId, principal.userId());
        ScheduleRuleEntity rule = getRule(classId, ruleId);
        validate(classId, request.getDayOfWeek(), request.getStartTime(), request.getEndTime(), ruleId);

        rule.update(request.getDayOfWeek(), request.getStartTime(), request.getEndTime());
        generator.deleteFuture(ruleId); // 앞으로의 회차를 새 요일/시간으로 다시 만든다
        generator.generate(rule);
    }

    @Transactional
    public void delete(JwtPrincipal principal, Long classId, Long ruleId) {
        accessValidator.requireTeacher(classId, principal.userId());
        ScheduleRuleEntity rule = getRule(classId, ruleId);

        generator.deleteFuture(ruleId);
        ruleRepository.delete(rule);
    }

    // 선생님은 수업 전체, 학생은 본인 규칙만 조회한다.
    @Transactional(readOnly = true)
    public List<ScheduleRuleResponse> list(JwtPrincipal principal, Long classId) {
        accessValidator.requireTeacherOrMember(classId, principal.userId());

        List<ScheduleRuleEntity> rules = principal.role() == Role.STUDENT
                ? ruleRepository.findByClassIdAndStudentId(classId, principal.userId())
                : ruleRepository.findByClassId(classId);

        return rules.stream()
                .sorted(Comparator.comparing(ScheduleRuleEntity::getDayOfWeek)
                        .thenComparing(ScheduleRuleEntity::getStartTime))
                .map(ScheduleRuleResponse::new)
                .toList();
    }

    private ScheduleRuleEntity getRule(Long classId, Long ruleId) {
        return ruleRepository.findByIdAndClassId(ruleId, classId)
                .orElseThrow(ScheduleRuleNotFoundException::new);
    }

    // 종료는 시작보다 늦어야 하고(자정을 넘기는 수업 불가), 수업 전체에서 같은 요일 시간이 겹치면 안 된다.
    private void validate(Long classId, DayOfWeek day, LocalTime start, LocalTime end, Long excludeRuleId) {
        if (!end.isAfter(start)) {
            throw new InvalidDateFormatException();
        }
        boolean overlap = ruleRepository.findByClassIdAndDayOfWeek(classId, day).stream()
                .filter(rule -> !rule.getId().equals(excludeRuleId))
                .anyMatch(rule -> rule.getStartTime().isBefore(end) && start.isBefore(rule.getEndTime()));
        if (overlap) {
            throw new ScheduleOverlapException();
        }
    }
}
