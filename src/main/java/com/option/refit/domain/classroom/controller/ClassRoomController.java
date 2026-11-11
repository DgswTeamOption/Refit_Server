package com.option.refit.domain.classroom.controller;

import com.option.refit.domain.classroom.dto.request.ClassRoomCreateRequest;
import com.option.refit.domain.classroom.dto.request.ClassRoomJoinRequest;
import com.option.refit.domain.classroom.dto.response.ClassRoomHomeResponse;
import com.option.refit.domain.classroom.dto.response.ClassRoomResponse;
import com.option.refit.domain.classroom.dto.response.ClassRoomStudentResponse;
import com.option.refit.domain.classroom.service.ClassRoomCreateService;
import com.option.refit.domain.classroom.service.ClassRoomDeleteService;
import com.option.refit.domain.classroom.service.ClassRoomHomeService;
import com.option.refit.domain.classroom.service.ClassRoomJoinService;
import com.option.refit.domain.classroom.service.ClassRoomLeaveService;
import com.option.refit.domain.classroom.service.ClassRoomListService;
import com.option.refit.domain.classroom.service.ClassRoomStudentListService;
import com.option.refit.domain.user.entity.User;
import com.option.refit.domain.user.repository.UserRepository;
import com.option.refit.global.jwt.JwtPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/class")
@Validated
public class ClassRoomController {

    private final ClassRoomCreateService createService;
    private final ClassRoomJoinService joinService;
    private final ClassRoomLeaveService leaveService;
    private final ClassRoomDeleteService deleteService;
    private final ClassRoomListService listService;
    private final ClassRoomHomeService homeService;
    private final ClassRoomStudentListService studentListService;
    private final UserRepository userRepository;

    // 교사 권한을 확인한 뒤 수업을 생성한다.
    @PostMapping
    public ClassRoomResponse createClassroom(
            @AuthenticationPrincipal JwtPrincipal principal,
            @Valid @RequestBody ClassRoomCreateRequest request
    ) {
        User user = getUser(principal);
        return createService.createClassroom(request, user);
    }

    // 학생이 수업 참여 코드를 입력하여 수업에 참여한다.
    @PostMapping("/join")
    public ClassRoomResponse joinClassroom(
            @AuthenticationPrincipal JwtPrincipal principal,
            @Valid @RequestBody ClassRoomJoinRequest request
    ) {
        User user = getUser(principal);
        return joinService.joinClassroom(request, user);
    }

    // 현재 참여 중인 학생이 수업에서 나간다.
    @DeleteMapping("/{classroomId}/leave")
    public void leaveClassroom(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable @Positive Long classroomId
    ) {
        User user = getUser(principal);
        leaveService.leaveClassroom(classroomId, user);
    }

    // 담당 교사만 수업과 연관 데이터(회차·가입 학생·결석·보강·NFC 연결 등)를 삭제한다.
    @DeleteMapping("/{classroomId}")
    public void deleteClassroom(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable @Positive Long classroomId
    ) {
        User user = getUser(principal);
        deleteService.deleteClassroom(classroomId, user);
    }

    // 교사는 자신이 생성한 수업을, 학생은 참여 중인 수업을 조회한다.
    @GetMapping
    public List<ClassRoomResponse> getClassrooms(
            @AuthenticationPrincipal JwtPrincipal principal
    ) {
        User user = getUser(principal);
        return listService.getClassrooms(user);
    }

    // 해당 수업의 교사이거나 현재 참여 중인 학생만 수업 홈을 조회할 수 있다.
    @GetMapping("/{classroomId}")
    public ClassRoomHomeResponse getClassroomHome(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable @Positive Long classroomId,
            @RequestParam(required = false) @Min(1) Integer year,
            @RequestParam(required = false) @Min(1) @Max(12) Integer month
    ) {
        User user = getUser(principal);

        YearMonth currentMonth = YearMonth.now();

        int targetYear = year != null
                ? year
                : currentMonth.getYear();

        int targetMonth = month != null
                ? month
                : currentMonth.getMonthValue();

        return homeService.getClassroomHome(
                classroomId, user, targetYear, targetMonth
        );
    }

    // 담당 교사만 수업에 참여 중인 학생 목록과 결석·보강 현황을 조회할 수 있다.
    @GetMapping("/{classroomId}/students")
    public List<ClassRoomStudentResponse> getClassroomStudents(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable @Positive Long classroomId
    ) {
        User user = getUser(principal);
        return studentListService.getStudents(classroomId, user);
    }

    // JWT 인증 정보에서 사용자 ID를 가져와 실제 User 엔티티를 조회한다.
    private User getUser(JwtPrincipal principal) {
        return userRepository.findByIdAndDeletedAtIsNull(principal.userId())
                .orElseThrow();
    }
}
