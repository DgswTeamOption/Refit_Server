package com.option.refit.global.error.fileError;

import com.option.refit.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FileErrorCode implements ErrorCode {
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 파일입니다"),
    FILE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "파일을 삭제할 권한이 없습니다"),
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "업로드할 파일이 없습니다"),
    INVALID_FILE_PATH(HttpStatus.BAD_REQUEST, "잘못된 파일 경로입니다"),
    FILE_SIZE_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, "파일 용량은 5MB 이하여야 합니다"),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다"),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
