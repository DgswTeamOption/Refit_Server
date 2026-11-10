package com.option.refit.domain.notice.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NoticeRequest {

    @Getter
    @NoArgsConstructor
    public static class Create {
        @NotBlank(message = "공지 제목은 필수입니다.")
        @Size(max = 100, message = "공지 제목은 100자 이하여야 합니다.")
        private String title;

        @NotBlank(message = "공지 내용은 필수입니다.")
        @Size(max = 10000, message = "공지 내용은 10000자 이하여야 합니다.")
        private String content;
    }

    @Getter
    @NoArgsConstructor
    public static class Update {
        @Size(max = 100, message = "공지 제목은 100자 이하여야 합니다.")
        @Pattern(
                regexp = "(?s).*\\S.*",
                message = "공지 제목은 공백만 입력할 수 없습니다."
        )
        private String title;

        @Size(max = 10000, message = "공지 내용은 10000자 이하여야 합니다.")
        @Pattern(
                regexp = "(?s).*\\S.*",
                message = "공지 내용은 공백만 입력할 수 없습니다."
        )
        private String content;

        @AssertTrue(message = "수정할 제목 또는 내용을 입력해주세요.")
        public boolean isAtLeastOneFieldProvided() {
            return title != null || content != null;
        }
    }
}