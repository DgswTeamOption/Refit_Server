package com.option.refit.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

// 프로필 이미지 변경 요청. 임의 URL 대신 업로드한 파일 ID를 받아 서버가 URL을 만든다.
public record UserChangeProfileImageRequest(

        @NotNull(message = "프로필 이미지 파일 ID를 입력해주세요.")
        @Schema(description = "POST /api/files/images 로 먼저 업로드한 본인 파일 ID")
        Long profileImageId
) {}
