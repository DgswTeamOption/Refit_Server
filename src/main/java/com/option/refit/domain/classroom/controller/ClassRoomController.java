package com.option.refit.domain.classroom.controller;

import com.option.refit.domain.classroom.dto.request.ClassRoomCreateRequest;
import com.option.refit.domain.classroom.dto.request.ClassRoomJoinRequest;
import com.option.refit.domain.classroom.dto.response.ClassRoomHomeResponse;
import com.option.refit.domain.classroom.dto.response.ClassRoomResponse;
import com.option.refit.domain.classroom.service.ClassRoomCreateService;
import com.option.refit.domain.classroom.service.ClassRoomHomeService;
import com.option.refit.domain.classroom.service.ClassRoomJoinService;
import com.option.refit.domain.classroom.service.ClassRoomLeaveService;
import com.option.refit.domain.classroom.service.ClassRoomListService;
import com.option.refit.domain.user.entity.User;
import com.option.refit.global.common.ApiResponse;
import com.option.refit.global.security.MemberDetails;
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
@RequestMapping("/classrooms")
@Validated
public class ClassRoomController {

    private final ClassRoomCreateService createService;
    private final ClassRoomJoinService joinService;
    private final ClassRoomLeaveService leaveService;
    private final ClassRoomListService listService;
    private final ClassRoomHomeService homeService;

    // 수업 생성
    @PostMapping
    public ApiResponse<ClassRoomResponse> createClassroom(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody ClassRoomCreateRequest request
    ) {
        User user = memberDetails.getUser();
        return ApiResponse.ok(createService.createClassroom(request, user));
    }

    // 수업 참여
    @PostMapping("/join")
    public ApiResponse<ClassRoomResponse> joinClassroom(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody ClassRoomJoinRequest request
    ) {
        User user = memberDetails.getUser();
        return ApiResponse.ok(joinService.joinClassroom(request, user));
    }

    // 수업 나가기
    @DeleteMapping("/{classroomId}/leave")
    public ApiResponse<Void> leaveClassroom(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable @Positive Long classroomId
    ) {
        User user = memberDetails.getUser();
        leaveService.leaveClassroom(classroomId, user);
        return ApiResponse.ok();
    }

    // 수업 목록 조회
    @GetMapping
    public ApiResponse<List<ClassRoomResponse>> getClassrooms(
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        User user = memberDetails.getUser();
        return ApiResponse.ok(listService.getClassrooms(user));
    }

    // 수업 홈 조회
    @GetMapping("/{classroomId}")
    public ApiResponse<ClassRoomHomeResponse> getClassroomHome(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable @Positive Long classroomId,
            @RequestParam(required = false) @Min(1) Integer year,
            @RequestParam(required = false) @Min(1) @Max(12) Integer month
    ) {
        User user = memberDetails.getUser();

        YearMonth currentMonth = YearMonth.now();

        int targetYear = year != null
                ? year
                : currentMonth.getYear();

        int targetMonth = month != null
                ? month
                : currentMonth.getMonthValue();

        return ApiResponse.ok(homeService.getClassroomHome(
                classroomId, user, targetYear, targetMonth
        ));
    }
}