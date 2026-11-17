package com.option.refit.domain.user.service.impl;

import com.option.refit.domain.file.entity.FileEntity;
import com.option.refit.domain.file.repository.FileRepository;
import com.option.refit.domain.file.service.FileUrlResolver;
import com.option.refit.domain.user.dto.request.UserChangeProfileImageRequest;
import com.option.refit.domain.user.dto.response.UserProfileResponse;
import com.option.refit.domain.user.entity.User;
import com.option.refit.domain.user.repository.UserRepository;
import com.option.refit.domain.user.service.UserChangeProfileImageService;
import com.option.refit.global.error.fileError.exception.FileAccessDeniedException;
import com.option.refit.global.error.fileError.exception.UploadedFileNotFoundException;
import com.option.refit.global.error.userError.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserChangeProfileImageServiceImpl implements UserChangeProfileImageService {
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final FileUrlResolver fileUrlResolver;

    // 파일이 없으면 404, 다른 사용자가 올린 파일이면 403
    @Override
    @Transactional
    public UserProfileResponse changeProfileImage(UserChangeProfileImageRequest request, Long userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(UserNotFoundException::new);

        FileEntity file = fileRepository.findById(request.profileImageId())
                .orElseThrow(UploadedFileNotFoundException::new);
        if (file.getUser().getId() == null || !file.getUser().getId().equals(userId)) {
            throw new FileAccessDeniedException();
        }

        user.changeProfileImage(fileUrlResolver.resolve(file));
        return UserProfileResponse.from(user);
    }
}
