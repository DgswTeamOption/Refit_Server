package com.option.refit.domain.notice.service.impl;

import com.option.refit.domain.notice.dto.NoticeRequest;
import com.option.refit.domain.notice.dto.NoticeResponse;
import com.option.refit.domain.notice.entity.NoticeEntity;
import com.option.refit.domain.notice.repository.NoticeRepository;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.noticeError.exception.NoticeAccessDeniedException;
import com.option.refit.global.error.noticeError.exception.NoticeNotFoundException;
import com.option.refit.global.error.noticeError.exception.NoticePermissionDeniedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @Mock
    private NoticeRepository noticeRepository;

    private NoticeCreateServiceImpl createService;
    private NoticeGetServiceImpl getService;
    private NoticeUpdateServiceImpl updateService;
    private NoticeDeleteServiceImpl deleteService;

    @BeforeEach
    void setUp() {
        createService = new NoticeCreateServiceImpl(noticeRepository);
        getService = new NoticeGetServiceImpl(noticeRepository);
        updateService = new NoticeUpdateServiceImpl(noticeRepository);
        deleteService = new NoticeDeleteServiceImpl(noticeRepository);
    }

    @Test
    void teacherCanCreateNotice() {
        NoticeRequest.Create request = createRequest("공지 제목", "공지 내용");
        NoticeEntity saved = notice(1L, 10L, "공지 제목", "공지 내용");
        ReflectionTestUtils.setField(saved, "id", 7L);

        when(noticeRepository.save(any(NoticeEntity.class))).thenReturn(saved);

        Long noticeId = createService.createNotice(1L, 10L, Role.TEACHER, request);

        assertEquals(7L, noticeId);

        ArgumentCaptor<NoticeEntity> captor =
                ArgumentCaptor.forClass(NoticeEntity.class);
        verify(noticeRepository).save(captor.capture());

        assertEquals(1L, captor.getValue().getClassId());
        assertEquals(10L, captor.getValue().getWriterId());
        assertEquals("공지 제목", captor.getValue().getTitle());
        assertEquals("공지 내용", captor.getValue().getContent());
    }

    @Test
    void studentCannotCreateNotice() {
        NoticeRequest.Create request = createRequest("공지 제목", "공지 내용");

        assertThrows(
                NoticePermissionDeniedException.class,
                () -> createService.createNotice(1L, 10L, Role.STUDENT, request)
        );

        verify(noticeRepository, never()).save(any());
    }

    @Test
    void getNoticesReturnsNoticesForClass() {
        NoticeEntity entity = notice(1L, 10L, "공지 제목", "공지 내용");
        when(noticeRepository.findByClassIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(entity));

        List<NoticeResponse> result =
                getService.getNotices(1L, 10L, Role.STUDENT);

        assertEquals(1, result.size());
        assertEquals("공지 제목", result.get(0).getTitle());
        verify(noticeRepository).findByClassIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void getNoticeReturnsNoticeMatchingClassAndId() {
        NoticeEntity entity = notice(1L, 10L, "공지 제목", "공지 내용");
        when(noticeRepository.findByIdAndClassId(5L, 1L))
                .thenReturn(Optional.of(entity));

        NoticeResponse result =
                getService.getNotice(1L, 5L, 10L, Role.STUDENT);

        assertEquals("공지 제목", result.getTitle());
        verify(noticeRepository).findByIdAndClassId(5L, 1L);
    }

    @Test
    void getNoticeThrowsWhenNoticeDoesNotExistInClass() {
        when(noticeRepository.findByIdAndClassId(5L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                NoticeNotFoundException.class,
                () -> getService.getNotice(1L, 5L, 10L, Role.STUDENT)
        );
    }

    @Test
    void updateChangesOnlyProvidedField() {
        NoticeEntity entity = notice(1L, 10L, "기존 제목", "기존 내용");
        when(noticeRepository.findByIdAndClassId(5L, 1L))
                .thenReturn(Optional.of(entity));

        NoticeRequest.Update request = updateRequest("새 제목", null);

        updateService.updateNotice(1L, 5L, 10L, request);

        assertEquals("새 제목", entity.getTitle());
        assertEquals("기존 내용", entity.getContent());
    }

    @Test
    void nonWriterCannotUpdateNotice() {
        NoticeEntity entity = notice(1L, 10L, "기존 제목", "기존 내용");
        when(noticeRepository.findByIdAndClassId(5L, 1L))
                .thenReturn(Optional.of(entity));

        assertThrows(
                NoticeAccessDeniedException.class,
                () -> updateService.updateNotice(
                        1L,
                        5L,
                        99L,
                        updateRequest("새 제목", null)
                )
        );
    }

    @Test
    void writerCanDeleteNotice() {
        NoticeEntity entity = notice(1L, 10L, "제목", "내용");
        when(noticeRepository.findByIdAndClassId(5L, 1L))
                .thenReturn(Optional.of(entity));

        deleteService.deleteNotice(1L, 5L, 10L);

        verify(noticeRepository).delete(entity);
    }

    @Test
    void nonWriterCannotDeleteNotice() {
        NoticeEntity entity = notice(1L, 10L, "제목", "내용");
        when(noticeRepository.findByIdAndClassId(5L, 1L))
                .thenReturn(Optional.of(entity));

        assertThrows(
                NoticeAccessDeniedException.class,
                () -> deleteService.deleteNotice(1L, 5L, 99L)
        );

        verify(noticeRepository, never()).delete(any());
    }

    private static NoticeEntity notice(
            Long classId,
            Long writerId,
            String title,
            String content
    ) {
        return NoticeEntity.builder()
                .classId(classId)
                .writerId(writerId)
                .title(title)
                .content(content)
                .build();
    }

    private static NoticeRequest.Create createRequest(String title, String content) {
        NoticeRequest.Create request = new NoticeRequest.Create();
        ReflectionTestUtils.setField(request, "title", title);
        ReflectionTestUtils.setField(request, "content", content);
        return request;
    }

    private static NoticeRequest.Update updateRequest(String title, String content) {
        NoticeRequest.Update request = new NoticeRequest.Update();
        ReflectionTestUtils.setField(request, "title", title);
        ReflectionTestUtils.setField(request, "content", content);
        return request;
    }
}