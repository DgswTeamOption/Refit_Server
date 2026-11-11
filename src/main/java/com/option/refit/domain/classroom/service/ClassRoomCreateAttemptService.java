package com.option.refit.domain.classroom.service;

import com.option.refit.domain.classroom.dto.request.ClassRoomCreateRequest;
import com.option.refit.domain.classroom.dto.response.ClassRoomResponse;
import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.file.entity.FileEntity;
import com.option.refit.domain.file.service.FileUrlResolver;
import com.option.refit.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClassRoomCreateAttemptService {

    private final ClassRoomRepository classroomRepository;
    private final FileUrlResolver fileUrlResolver;

    // 독립적인 트랜잭션에서 수업을 저장한다.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ClassRoomResponse create(
            ClassRoomCreateRequest request,
            User user,
            String classCode,
            FileEntity bannerImage
    ) {
        ClassRoomEntity classroom = ClassRoomEntity.create(
                request.name(), user, classCode
        );

        classroom.changeBannerImage(bannerImage);

        ClassRoomEntity savedClassroom =
                classroomRepository.saveAndFlush(classroom);

        return ClassRoomResponse.from(savedClassroom, fileUrlResolver.resolve(bannerImage));
    }
}