package com.option.refit.domain.classroom.dto.request;

import jakarta.validation.constraints.NotNull;

// 수업 배너 교체 요청
public record ClassRoomBannerChangeRequest(

        // POST /api/files/images 로 먼저 업로드한 파일 ID
        @NotNull(message = "배너 이미지 파일 ID를 입력해주세요.")
        Long bannerImageId

) {
}
