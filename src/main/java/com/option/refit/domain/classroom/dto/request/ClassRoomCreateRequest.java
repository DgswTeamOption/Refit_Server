package com.option.refit.domain.classroom.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//수업 생성 요청
public record ClassRoomCreateRequest(

        @NotBlank(message = "수업명을 입력해주세요.")
        @Size(max = 50, message = "수업명은 50자 이하여야 합니다.")
        String name,

        // 배너 이미지 파일 ID (POST /api/files/images 로 먼저 업로드한 결과, 선택)
        Long bannerImageId

) {
}