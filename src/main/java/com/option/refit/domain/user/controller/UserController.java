package com.option.refit.domain.user.controller;

import com.option.refit.domain.user.dto.request.UserChangeProfileImageRequest;
import com.option.refit.domain.user.dto.request.UserUpdateProfileRequest;
import com.option.refit.domain.user.dto.response.UserProfileResponse;
import com.option.refit.domain.user.service.UserChangeProfileImageService;
import com.option.refit.domain.user.service.UserDeleteService;
import com.option.refit.domain.user.service.UserGetProfileService;
import com.option.refit.domain.user.service.UserUpdateProfileService;
import com.option.refit.global.error.userError.exception.UserAccessDeniedException;
import com.option.refit.global.common.ApiResponse;
import com.option.refit.global.jwt.JwtPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserChangeProfileImageService userChangeProfileImageService;
    private final UserDeleteService userDeleteService;
    private final UserGetProfileService userGetProfileService;
    private final UserUpdateProfileService userUpdateProfileService;

    @Operation(description = "본인 프로필만 조회할 수 있습니다. 토큰의 사용자와 userId가 다르면 403을 반환합니다.")
    @GetMapping("/users/{userId}")
    public ApiResponse<UserProfileResponse> getProfile(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long userId
    ) {

        if (!principal.userId().equals(userId)) {
            throw new UserAccessDeniedException();
        }

        UserProfileResponse response = userGetProfileService.getUserProfile(principal.userId());

        return ApiResponse.ok(response);
    }

    @PatchMapping("/setting/username")
    public ApiResponse<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal JwtPrincipal principal,
            @Valid @RequestBody UserUpdateProfileRequest request
    ) {

        UserProfileResponse response = userUpdateProfileService.updateProfile(request, principal.userId());

        return ApiResponse.ok(response);
    }

    @Operation(description = "먼저 POST /api/files/images 로 업로드한 본인 파일의 ID를 보냅니다. 없는 파일이면 404, 다른 사용자의 파일이면 403입니다.")
    @PatchMapping("/setting/profile-image")
    public ApiResponse<UserProfileResponse> changeProfileImage(
            @AuthenticationPrincipal JwtPrincipal principal,
            @Valid @RequestBody UserChangeProfileImageRequest request
    ) {

        UserProfileResponse response = userChangeProfileImageService.changeProfileImage(request, principal.userId());

        return ApiResponse.ok(response);
    }

    @Operation(description = "본인만 탈퇴할 수 있습니다. 토큰의 사용자와 userId가 다르면 403을 반환합니다.")
    @DeleteMapping("/users/{userId}")
    public ApiResponse<Void> deleteAccount(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long userId
    ) {

        if (!principal.userId().equals(userId)) {
            throw new UserAccessDeniedException();
        }

        userDeleteService.deleteUser(principal.userId());

        return ApiResponse.ok();
    }
}
