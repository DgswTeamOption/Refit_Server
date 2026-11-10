package com.option.refit.domain.notice.repository;

import com.option.refit.domain.notice.entity.NoticeEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:notice-test;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver"
})
class NoticeRepositorySoftDeleteTest {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void deleteSoftDeletesNoticeAndHidesItFromRepository() {
        NoticeEntity notice = noticeRepository.saveAndFlush(
                NoticeEntity.builder()
                        .classId(1L)
                        .writerId(10L)
                        .title("제목")
                        .content("내용")
                        .build()
        );

        Long noticeId = notice.getId();

        noticeRepository.delete(notice);
        noticeRepository.flush();

        assertTrue(noticeRepository.findById(noticeId).isEmpty());

        Integer rowCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notices WHERE id = ? AND deleted_at IS NOT NULL",
                Integer.class,
                noticeId
        );

        assertEquals(1, rowCount);
    }
}