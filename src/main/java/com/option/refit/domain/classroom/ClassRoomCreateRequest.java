package com.option.refit.domain.classroom;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record ClassRoomCreateRequest(

        @NotBlank(message = "수업명을 입력해주세요.")
        @Size(max = 100, message = "수업명은 100자 이하여야 합니다.")
        String name

) {
}