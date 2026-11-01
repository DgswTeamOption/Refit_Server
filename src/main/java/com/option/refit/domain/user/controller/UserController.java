package com.option.refit.domain.user.controller;

import com.option.refit.domain.user.dto.request.UserChangeProfileImageRequest;
import com.option.refit.domain.user.dto.request.UserUpdateProfileRequest;
import com.option.refit.domain.user.dto.response.UserProfileResponse;
import com.option.refit.domain.user.entity.User;
import com.option.refit.domain.user.service.UserChangeProfileImageService;
import com.option.refit.domain.user.service.UserDeleteService;
import com.option.refit.domain.user.service.UserGetProfileService;
import com.option.refit.domain.user.service.UserUpdateProfileService;
import com.option.refit.global.error.userError.exception.UserAccessDeniedException;
import com.option.refit.global.common.ApiResponse;
import com.option.refit.global.security.MemberDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserChangeProfileImageService userChangeProfileImageService;
    private final UserDeleteService userDeleteService;
    private final UserGetProfileService userGetProfileService;
    private final UserUpdateProfileService userUpdateProfileService;

    @GetMapping("/{userId}")
    public ApiResponse<UserProfileResponse> getProfile(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long userId
    ) {

        User user = memberDetails.getUser();

        if (!user.getId().equals(userId)) {
            throw new UserAccessDeniedException();
        }

        UserProfileResponse response = userGetProfileService.getUserProfile(user);

        return ApiResponse.ok(response);
    }

    @PatchMapping("/me/username")
    public ApiResponse<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody UserUpdateProfileRequest request
    ) {

        User user = memberDetails.getUser();

        UserProfileResponse response = userUpdateProfileService.updateProfile(request, user);

        return ApiResponse.ok(response);
    }

    @PatchMapping("/me/profile-image")
    public ApiResponse<UserProfileResponse> changeProfileImage(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody UserChangeProfileImageRequest request
    ) {

        User user = memberDetails.getUser();

        UserProfileResponse response = userChangeProfileImageService.changeProfileImage(request, user.getId());

        return ApiResponse.ok(response);
    }

    @DeleteMapping("/me")
    public ApiResponse<Void> deleteAccount(@AuthenticationPrincipal MemberDetails memberDetails) {

        User user = memberDetails.getUser();

        userDeleteService.deleteUser(user);

        return ApiResponse.ok();
    }
}
