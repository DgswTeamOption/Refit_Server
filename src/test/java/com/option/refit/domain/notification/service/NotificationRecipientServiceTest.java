package com.option.refit.domain.notification.service;

import com.option.refit.domain.notification.dto.NotificationResponse;
import com.option.refit.domain.notification.entity.NotificationEntity;
import com.option.refit.domain.notification.repository.NotificationRepository;
import com.option.refit.domain.notification.service.impl.NotificationDeleteServiceImpl;
import com.option.refit.domain.notification.service.impl.NotificationGetServiceImpl;
import com.option.refit.global.error.notification.exception.NotificationNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import({NotificationGetServiceImpl.class, NotificationDeleteServiceImpl.class})
class NotificationRecipientServiceTest {

    private static final Long A_USER_ID = 99L;
    private static final Long B_USER_ID = 100L;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationGetService notificationGetService;

    @Autowired
    private NotificationDeleteService notificationDeleteService;

    @PersistenceContext
    private EntityManager entityManager;

    private NotificationEntity aNotification;
    private NotificationEntity bNotification;

    @BeforeEach
    void setUp() {
        aNotification = saveNotification(A_USER_ID);
        bNotification = saveNotification(B_USER_ID);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void eachUserCanOnlySeeTheirOwnNotifications() {
        NotificationEntity anotherANotification = saveNotification(A_USER_ID);
        entityManager.flush();
        entityManager.clear();
        PageRequest pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "id"));

        Page<NotificationResponse> aPage = notificationGetService.getNotifications(A_USER_ID, pageable);
        Page<NotificationResponse> bPage = notificationGetService.getNotifications(B_USER_ID, pageable);

        assertThat(aPage.getContent()).extracting(NotificationResponse::getNotificationId)
                .containsExactly(anotherANotification.getId());
        assertThat(aPage.getContent()).extracting(NotificationResponse::getRecipientId)
                .containsExactly(A_USER_ID);
        assertThat(aPage.getTotalElements()).isEqualTo(2);
        assertThat(aPage.getTotalPages()).isEqualTo(2);
        assertThat(bPage.getContent()).extracting(NotificationResponse::getNotificationId)
                .containsExactly(bNotification.getId());
        assertThat(bPage.getContent()).extracting(NotificationResponse::getRecipientId)
                .containsExactly(B_USER_ID);
        assertThat(bPage.getTotalElements()).isEqualTo(1);
    }

    @Test
    void deletingAnotherUsersNotificationReturns404AndPreservesIt() {
        Long bNotificationId = bNotification.getId();

        assertThatThrownBy(() -> notificationDeleteService.deleteNotification(bNotificationId, A_USER_ID))
                .isInstanceOf(NotificationNotFoundException.class);
        entityManager.flush();
        entityManager.clear();

        NotificationEntity remaining = notificationRepository
                .findByIdAndRecipientId(bNotificationId, B_USER_ID)
                .orElseThrow();
        assertThat(remaining.getRecipientId()).isEqualTo(B_USER_ID);
        assertThat(remaining.getDeletedAt()).isNull();
        assertThat(notificationRepository.findByIdAndRecipientId(aNotification.getId(), A_USER_ID))
                .isPresent();
    }

    @Test
    void deletingOwnNotificationSoftDeletesOnlyThatNotification() {
        Long aNotificationId = aNotification.getId();

        notificationDeleteService.deleteNotification(aNotificationId, A_USER_ID);
        entityManager.flush();
        entityManager.clear();

        assertThat(notificationRepository.findById(aNotificationId)).isEmpty();
        assertThat(notificationRepository.findByIdAndRecipientId(bNotification.getId(), B_USER_ID))
                .isPresent();
        Object deletedAt = entityManager.createNativeQuery("""
                SELECT deleted_at
                FROM notification
                WHERE id = :id
                """)
                .setParameter("id", aNotificationId)
                .getSingleResult();
        assertThat(deletedAt).isNotNull();
    }

    private NotificationEntity saveNotification(Long recipientId) {
        return notificationRepository.save(NotificationEntity.builder()
                .teacherId(A_USER_ID)
                .studentId(B_USER_ID)
                .recipientId(recipientId)
                .message("공지 알림입니다")
                .notificationType("3")
                .build());
    }
}
