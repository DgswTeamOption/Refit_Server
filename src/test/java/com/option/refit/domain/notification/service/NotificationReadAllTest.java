package com.option.refit.domain.notification.service;

import com.option.refit.domain.notification.dto.NotificationReadAllResponse;
import com.option.refit.domain.notification.entity.NotificationEntity;
import com.option.refit.domain.notification.repository.NotificationRepository;
import com.option.refit.domain.notification.service.impl.NotificationReadServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(NotificationReadServiceImpl.class)
class NotificationReadAllTest {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationReadService readService;
    @PersistenceContext
    private EntityManager entityManager;

    private NotificationEntity save(Long recipientId) {
        return notificationRepository.save(NotificationEntity.builder()
                .teacherId(1L).studentId(2L).recipientId(recipientId)
                .message("m").notificationType("0").build());
    }

    @Test
    void marksOnlyOwnUnreadAndReturnsCount() {
        NotificationEntity mine1 = save(10L);
        NotificationEntity mine2 = save(10L);
        NotificationEntity alreadyRead = save(10L);
        alreadyRead.markAsRead();
        NotificationEntity others = save(11L);
        entityManager.flush();
        entityManager.clear();

        NotificationReadAllResponse first = readService.readAllNotifications(10L);
        NotificationReadAllResponse second = readService.readAllNotifications(10L);

        assertThat(first.updatedCount()).isEqualTo(2);
        assertThat(second.updatedCount()).isZero();
        assertThat(notificationRepository.findById(mine1.getId()).orElseThrow().isRead()).isTrue();
        assertThat(notificationRepository.findById(mine2.getId()).orElseThrow().isRead()).isTrue();
        assertThat(notificationRepository.findById(others.getId()).orElseThrow().isRead()).isFalse();
    }
}
