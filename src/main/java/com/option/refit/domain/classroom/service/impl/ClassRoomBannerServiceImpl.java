package com.option.refit.domain.classroom.service.impl;

import com.option.refit.domain.classroom.dto.request.ClassRoomBannerChangeRequest;
import com.option.refit.domain.classroom.dto.response.ClassRoomResponse;
import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.classroom.service.ClassRoomBannerFileValidator;
import com.option.refit.domain.classroom.service.ClassRoomBannerService;
import com.option.refit.domain.file.entity.FileEntity;
import com.option.refit.domain.file.service.FileDeleteService;
import com.option.refit.domain.file.service.FileUrlResolver;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.domain.user.entity.User;
import com.option.refit.global.error.classError.exception.ClassAccessDeniedException;
import com.option.refit.global.error.classError.exception.ClassNotFoundException;
import com.option.refit.global.error.classError.exception.ClassPermissionDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClassRoomBannerServiceImpl implements ClassRoomBannerService {

    private final ClassRoomRepository classroomRepository;
    private final ClassRoomBannerFileValidator bannerFileValidator;
    private final FileDeleteService fileDeleteService;
    private final FileUrlResolver fileUrlResolver;

    // 배너를 새 파일로 교체하고 기존 배너 파일을 삭제한다.
    @Override
    @Transactional
    public ClassRoomResponse changeBanner(
            Long classroomId,
            ClassRoomBannerChangeRequest request,
            User user
    ) {
        ClassRoomEntity classroom = getOwnedClassroom(classroomId, user);

        FileEntity newBanner = bannerFileValidator.getOwnedFile(request.bannerImageId(), user.getId());
        FileEntity oldBanner = classroom.getBannerImage();

        // 같은 파일로 다시 지정하면 변경 없음 (삭제하면 현재 배너가 사라지므로)
        if (oldBanner != null && oldBanner.getId().equals(newBanner.getId())) {
            return ClassRoomResponse.from(classroom, fileUrlResolver.resolve(newBanner));
        }

        classroom.changeBannerImage(newBanner);
        deleteOldBanner(oldBanner, user);

        return ClassRoomResponse.from(classroom, fileUrlResolver.resolve(newBanner));
    }

    // 배너를 제거하고 기존 배너 파일을 삭제한다.
    @Override
    @Transactional
    public void deleteBanner(Long classroomId, User user) {
        ClassRoomEntity classroom = getOwnedClassroom(classroomId, user);

        FileEntity oldBanner = classroom.getBannerImage();
        classroom.changeBannerImage(null);
        deleteOldBanner(oldBanner, user);
    }

    // 교사 역할이 아니면 403, 수업이 없으면 404, 담당 교사가 아니면 403
    private ClassRoomEntity getOwnedClassroom(Long classroomId, User user) {
        if (user.getRole() != Role.TEACHER) {
            throw new ClassPermissionDeniedException();
        }

        ClassRoomEntity classroom = classroomRepository.findById(classroomId)
                .orElseThrow(ClassNotFoundException::new);

        if (!classroom.getTeacher().getId().equals(user.getId())) {
            throw new ClassAccessDeniedException();
        }

        return classroom;
    }

    // 수업이 더 이상 참조하지 않는 이전 배너 파일(DB 행 + 업로드 폴더 파일)을 정리한다.
    // FK 위반을 피하려고 수업의 참조를 먼저 DB에 반영(flush)한 뒤 삭제한다.
    private void deleteOldBanner(FileEntity oldBanner, User user) {
        if (oldBanner == null) {
            return;
        }

        classroomRepository.flush();
        fileDeleteService.deleteFile(oldBanner.getId(), user.getId());
    }
}
