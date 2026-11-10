package com.option.refit.global.error.noticeError;

import com.option.refit.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NoticeErrorCode implements ErrorCode {
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 공지입니다"),
    NOTICE_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "선생님만 공지를 작성할 수 있습니다"),
    NOTICE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "본인이 작성한 공지만 수정/삭제할 수 있습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
