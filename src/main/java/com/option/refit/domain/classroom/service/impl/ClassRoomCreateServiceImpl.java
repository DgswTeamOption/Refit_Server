package com.option.refit.domain.classroom.service.impl;

import com.option.refit.domain.classroom.dto.request.ClassRoomCreateRequest;
import com.option.refit.domain.classroom.dto.response.ClassRoomResponse;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.classroom.service.ClassRoomCreateService;
import com.option.refit.domain.classroom.service.ClassRoomCreateAttemptService;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.domain.user.entity.User;
import com.option.refit.global.error.classError.exception.ClassPermissionDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassRoomCreateServiceImpl implements ClassRoomCreateService {

    private final ClassRoomRepository classroomRepository;
    private final ClassRoomCreateAttemptService createAttemptService;

    // 수업 생성 요청을 처리
    @Override
    public ClassRoomResponse createClassroom(
            ClassRoomCreateRequest request,
            User user
    ) {

        // 수업 생성 요청을 보낸 사용자가 교사인지 확인
        if (user.getRole() != Role.TEACHER) {
            throw new ClassPermissionDeniedException();
        }

        // 코드 중복으로 저장에 실패하면 최대 5번까지 재시도
        for (int attempt = 0; attempt < 5; attempt++) {

            String classCode = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8)
                    .toUpperCase();

            // 이미 사용 중인 코드라면 새로운 코드 생성
            if (classroomRepository.existsByClassCode(classCode)) {
                continue;
            }

            try {
                // 별도 트랜잭션에서 수업 저장
                return createAttemptService.create(
                        request, user, classCode
                );

            } catch (DataIntegrityViolationException e) {

                // 수업 코드 중복으로 발생한 오류인지 확인
                if (!isClassCodeDuplicate(e)) {
                    throw e;
                }

                // 마지막 시도에서도 중복되면 예외 발생
                if (attempt == 4) {
                    throw e;
                }
            }
        }

        throw new IllegalStateException("수업 코드 생성에 실패했습니다.");
    }

    // DB에서 수업 코드 UNIQUE 제약 조건 위반이 발생했는지 확인
    private boolean isClassCodeDuplicate(Throwable exception) {

        Throwable cause = exception;

        while (cause != null) {

            // MySQL의 중복 키 오류 코드: 1062
            if (cause instanceof java.sql.SQLException sqlException) {
                if (sqlException.getErrorCode() == 1062) {
                    return true;
                }
            }

            cause = cause.getCause();
        }

        return false;
    }
}