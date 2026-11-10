package com.option.refit.domain.notice.service.impl;

import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.repository.ClassRoomMemberRepository;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.notice.dto.NoticeRequest;
import com.option.refit.domain.notice.dto.NoticeResponse;
import com.option.refit.domain.notice.entity.NoticeEntity;
import com.option.refit.domain.notice.repository.NoticeRepository;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.domain.user.entity.User;
import com.option.refit.global.error.classError.exception.ClassAccessDeniedException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private ClassRoomRepository classRoomRepository;

    @Mock
    private ClassRoomMemberRepository classRoomMemberRepository;

    private NoticeCreateServiceImpl createService;
    private NoticeGetServiceImpl getService;
    private NoticeUpdateServiceImpl updateService;
    private NoticeDeleteServiceImpl deleteService;

    @BeforeEach
    void setUp() {
        createService = new NoticeCreateServiceImpl(
                noticeRepository,
                classRoomRepository
        );

        getService = new NoticeGetServiceImpl(
                noticeRepository,
                classRoomRepository,
                classRoomMemberRepository
        );

        updateService = new NoticeUpdateServiceImpl(noticeRepository);
        deleteService = new NoticeDeleteServiceImpl(noticeRepository);
    }

    @Test
    void 담당교사는공지를생성할수있다() {
        NoticeRequest.Create request =
                createRequest("공지 제목", "공지 내용");
        NoticeEntity saved =
                notice(1L, 10L, "공지 제목", "공지 내용");
        ReflectionTestUtils.setField(saved, "id", 7L);

        stubClassTeacher(1L, 10L);
        when(noticeRepository.save(any(NoticeEntity.class)))
                .thenReturn(saved);

        Long noticeId = createService.createNotice(
                1L,
                10L,
                Role.TEACHER,
                request
        );

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
    void 학생은공지를생성할수없다() {
        NoticeRequest.Create request =
                createRequest("공지 제목", "공지 내용");
        stubClassExists(1L);

        assertThrows(
                NoticePermissionDeniedException.class,
                () -> createService.createNotice(
                        1L,
                        10L,
                        Role.STUDENT,
                        request
                )
        );

        verify(noticeRepository, never()).save(any());
    }

    @Test
    void 다른반의교사는공지를생성할수없다() {
        NoticeRequest.Create request =
                createRequest("공지 제목", "공지 내용");
        stubClassTeacher(1L, 20L);

        assertThrows(
                NoticePermissionDeniedException.class,
                () -> createService.createNotice(
                        1L,
                        10L,
                        Role.TEACHER,
                        request
                )
        );

        verify(noticeRepository, never()).save(any());
    }

    @Test
    void 현재참여중인학생은반의공지목록을조회할수있다() {
        NoticeEntity entity =
                notice(1L, 10L, "공지 제목", "공지 내용");
        stubStudentAccess(1L, 20L);

        when(noticeRepository.findByClassIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(entity));

        List<NoticeResponse> result =
                getService.getNotices(1L, 20L, Role.STUDENT);

        assertEquals(1, result.size());
        assertEquals("공지 제목", result.get(0).getTitle());

        verify(classRoomMemberRepository)
                .existsByClassroom_IdAndUser_IdAndLeftAtIsNull(1L, 20L);
        verify(noticeRepository)
                .findByClassIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void 참여하지않는학생은공지목록을조회할수없다() {
        stubClassExists(1L);
        when(classRoomMemberRepository
                .existsByClassroom_IdAndUser_IdAndLeftAtIsNull(1L, 20L))
                .thenReturn(false);

        assertThrows(
                ClassAccessDeniedException.class,
                () -> getService.getNotices(1L, 20L, Role.STUDENT)
        );

        verify(noticeRepository, never())
                .findByClassIdOrderByCreatedAtDesc(any());
    }

    @Test
    void 현재참여중인학생은공지상세를조회할수있다() {
        NoticeEntity entity =
                notice(1L, 10L, "공지 제목", "공지 내용");
        stubStudentAccess(1L, 20L);

        when(noticeRepository.findByIdAndClassId(5L, 1L))
                .thenReturn(Optional.of(entity));

        NoticeResponse result =
                getService.getNotice(1L, 5L, 20L, Role.STUDENT);

        assertEquals("공지 제목", result.getTitle());
        verify(noticeRepository).findByIdAndClassId(5L, 1L);
    }

    @Test
    void 존재하지않는반은공지조회시예외가발생한다() {
        when(classRoomRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                com.option.refit.global.error.classError.exception
                        .ClassNotFoundException.class,
                () -> getService.getNotices(1L, 20L, Role.STUDENT)
        );

        verify(noticeRepository, never())
                .findByClassIdOrderByCreatedAtDesc(any());
    }

    @Test
    void 반에속하지않는공지는찾을수없다() {
        NoticeEntity entity =
                notice(1L, 10L, "공지 제목", "공지 내용");
        stubStudentAccess(1L, 20L);

        when(noticeRepository.findByIdAndClassId(5L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                NoticeNotFoundException.class,
                () -> getService.getNotice(1L, 5L, 20L, Role.STUDENT)
        );
    }

    @Test
    void 수정요청에서전달한필드만변경한다() {
        NoticeEntity entity =
                notice(1L, 10L, "기존 제목", "기존 내용");
        when(noticeRepository.findByIdAndClassId(5L, 1L))
                .thenReturn(Optional.of(entity));

        NoticeRequest.Update request =
                updateRequest("새 제목", null);

        updateService.updateNotice(1L, 5L, 10L, request);

        assertEquals("새 제목", entity.getTitle());
        assertEquals("기존 내용", entity.getContent());
    }

    @Test
    void 작성자가아닌사용자는공지를수정할수없다() {
        NoticeEntity entity =
                notice(1L, 10L, "기존 제목", "기존 내용");
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
    void 작성자는공지를삭제할수있다() {
        NoticeEntity entity =
                notice(1L, 10L, "제목", "내용");
        when(noticeRepository.findByIdAndClassId(5L, 1L))
                .thenReturn(Optional.of(entity));

        deleteService.deleteNotice(1L, 5L, 10L);

        verify(noticeRepository).delete(entity);
    }

    @Test
    void 작성자가아닌사용자는공지를삭제할수없다() {
        NoticeEntity entity =
                notice(1L, 10L, "제목", "내용");
        when(noticeRepository.findByIdAndClassId(5L, 1L))
                .thenReturn(Optional.of(entity));

        assertThrows(
                NoticeAccessDeniedException.class,
                () -> deleteService.deleteNotice(1L, 5L, 99L)
        );

        verify(noticeRepository, never()).delete(any());
    }

    private void stubClassExists(Long classId) {
        when(classRoomRepository.findById(classId))
                .thenReturn(Optional.of(mock(ClassRoomEntity.class)));
    }

    private void stubClassTeacher(Long classId, Long teacherId) {
        ClassRoomEntity classroom = mock(ClassRoomEntity.class);
        User teacher = mock(User.class);

        when(classRoomRepository.findById(classId))
                .thenReturn(Optional.of(classroom));
        when(classroom.getTeacher()).thenReturn(teacher);
        when(teacher.getId()).thenReturn(teacherId);
    }

    private void stubStudentAccess(Long classId, Long userId) {
        stubClassExists(classId);

        when(classRoomMemberRepository
                .existsByClassroom_IdAndUser_IdAndLeftAtIsNull(
                        classId,
                        userId
                ))
                .thenReturn(true);
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

    private static NoticeRequest.Create createRequest(
            String title,
            String content
    ) {
        NoticeRequest.Create request =
                new NoticeRequest.Create();
        ReflectionTestUtils.setField(request, "title", title);
        ReflectionTestUtils.setField(request, "content", content);
        return request;
    }

    private static NoticeRequest.Update updateRequest(
            String title,
            String content
    ) {
        NoticeRequest.Update request =
                new NoticeRequest.Update();
        ReflectionTestUtils.setField(request, "title", title);
        ReflectionTestUtils.setField(request, "content", content);
        return request;
    }
}