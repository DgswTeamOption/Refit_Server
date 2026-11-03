package com.option.refit.domain.classroom.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//수업 참여 요청

public class ClassRoomJoinRequest {

    public record ClassroomJoinRequest(

            @NotBlank(message = "수업 코드를 입력해주세요.")
            @Size(min = 8, max = 8, message = "수업 코드는 8자리여야 합니다.")
            String classCode

    ) { }
}
