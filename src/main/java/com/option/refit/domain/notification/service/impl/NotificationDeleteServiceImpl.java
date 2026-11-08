package com.option.refit.domain.notification.service.impl;

import com.option.refit.domain.notification.entity.NotificationEntity;
import com.option.refit.domain.notification.repository.NotificationRepository;
import com.option.refit.domain.notification.service.NotificationDeleteService;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.jwtError.exception.MissingTokenException;
import com.option.refit.global.error.notification.exception.NotificationAccessDeniedException;
import com.option.refit.global.error.notification.exception.NotificationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationDeleteServiceImpl implements NotificationDeleteService {

    private final NotificationRepository notificationRepository;

    @Override
    public void deleteNotification(Long notificationId, Long userId, Role role) {
        if (userId == null || role == null) {
            throw new MissingTokenException();
        }

        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);

        boolean isRecipient = userId != null && (
                (Role.TEACHER.equals(role) && userId.equals(notification.getTeacherId()))
                        || (Role.STUDENT.equals(role) && userId.equals(notification.getStudentId())));

        if (!isRecipient) {
            throw new NotificationAccessDeniedException();
        }

        notificationRepository.delete(notification);
    }
}