package com.option.refit.domain.notice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NoticeRequest {

    @Getter
    @NoArgsConstructor
    public static class CreateOrUpdate {
        @NotBlank(message = "공지 제목은 필수입니다.")
        @Size(max = 100, message = "공지 제목은 100자 이하여야 합니다.")
        private String title;

        @NotBlank(message = "공지 내용은 필수입니다.")
        private String content;
    }
}
