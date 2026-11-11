package com.option.refit.domain.classroom.service;

import com.option.refit.domain.file.entity.FileEntity;
import com.option.refit.domain.file.repository.FileRepository;
import com.option.refit.global.error.fileError.exception.FileAccessDeniedException;
import com.option.refit.global.error.fileError.exception.UploadedFileNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// 배너로 지정할 파일이 존재하고, 요청한 교사가 직접 업로드한 파일인지 검사한다.
// 파일 업로드/저장은 file 도메인(POST /api/files/images)을 그대로 사용한다.
@Component
@RequiredArgsConstructor
public class ClassRoomBannerFileValidator {

    private final FileRepository fileRepository;

    // 파일이 없으면 404, 다른 사용자의 파일이면 403
    public FileEntity getOwnedFile(Long fileId, Long userId) {
        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(UploadedFileNotFoundException::new);

        if (file.getUser().getId() == null || !file.getUser().getId().equals(userId)) {
            throw new FileAccessDeniedException();
        }

        return file;
    }
}
