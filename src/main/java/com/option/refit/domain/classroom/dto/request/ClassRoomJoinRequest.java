package com.option.refit.domain.classroom.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Locale;

// 수업 참여 요청
public record ClassRoomJoinRequest(

        @NotBlank(message = "수업 코드를 입력해주세요.")
        @Size(min = 8, max = 8, message = "수업 코드는 8자리여야 합니다.")
        @Pattern(regexp = "^[a-zA-Z0-9]{8}$", message = "수업 코드는 영문자와 숫자만 입력할 수 있습니다.")
        String classCode
) {

        // 수업 코드의 앞뒤 공백을 제거하고 영문자를 대문자로 변환
        // DTO 생성 시 정규화하여 Bean Validation이 정리된 값을 검사하도록 함
        public ClassRoomJoinRequest {
                if (classCode != null) {
                classCode = classCode.trim().toUpperCase(Locale.ROOT);}
        }
}