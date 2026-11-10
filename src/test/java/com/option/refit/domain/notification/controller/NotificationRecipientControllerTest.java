package com.option.refit.domain.notification.controller;

import com.option.refit.domain.notification.dto.NotificationResponse;
import com.option.refit.domain.notification.entity.NotificationEntity;
import com.option.refit.domain.notification.service.NotificationDeleteService;
import com.option.refit.domain.notification.service.NotificationGetService;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.GlobalExceptionHandler;
import com.option.refit.global.error.notification.exception.NotificationNotFoundException;
import com.option.refit.global.jwt.JwtPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificationRecipientControllerTest {

    @Mock
    private NotificationGetService notificationGetService;

    @Mock
    private NotificationDeleteService notificationDeleteService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new NotificationController(notificationGetService, notificationDeleteService))
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver(),
                        new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getReturnsRecipientIdInsideTheCommonPageEnvelope() throws Exception {
        authenticate(new JwtPrincipal(20L, Role.STUDENT));
        NotificationEntity entity = NotificationEntity.builder()
                .teacherId(10L)
                .studentId(20L)
                .recipientId(20L)
                .message("수업 일정이 변경되었습니다")
                .notificationType("4")
                .build();
        ReflectionTestUtils.setField(entity, "id", 3L);
        ReflectionTestUtils.setField(entity, "createdAt", LocalDateTime.of(2026, 11, 9, 9, 5, 3));
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
        when(notificationGetService.getNotifications(eq(20L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new NotificationResponse(entity)), pageable, 1));

        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].notificationId").value(3))
                .andExpect(jsonPath("$.data.content[0].recipientId").value(20))
                .andExpect(jsonPath("$.data.content[0].isRead").value(false))
                .andExpect(jsonPath("$.data.content[0].read").doesNotExist())
                .andExpect(jsonPath("$.data.content[0].createdAt").value("2026-11-09 09:05:03"))
                .andExpect(jsonPath("$.data.number").value(0))
                .andExpect(jsonPath("$.data.size").value(20));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(notificationGetService).getNotifications(eq(20L), captor.capture());
        assertEquals(pageable, captor.getValue());
    }

    @Test
    void deletePassesTheAuthenticatedUserIdToService() throws Exception {
        authenticate(new JwtPrincipal(99L, Role.STUDENT));

        mockMvc.perform(delete("/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error").doesNotExist());

        verify(notificationDeleteService).deleteNotification(eq(1L), eq(99L));
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("incompletePrincipals")
    void incompleteAuthenticationReturns401BeforeCallingAnyService(JwtPrincipal principal) throws Exception {
        if (principal != null) {
            authenticate(principal);
        }

        mockMvc.perform(get("/notifications"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("로그인이 필요합니다"));
        mockMvc.perform(delete("/notifications/3"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(notificationGetService, notificationDeleteService);
    }

    @Test
    void deleteReturns404WhenTheServiceDoesNotFindTheNotification() throws Exception {
        authenticate(new JwtPrincipal(99L, Role.STUDENT));
        doThrow(new NotificationNotFoundException()).when(notificationDeleteService)
                .deleteNotification(1L, 99L);

        mockMvc.perform(delete("/notifications/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(notificationDeleteService).deleteNotification(eq(1L), eq(99L));
    }

    private static Stream<JwtPrincipal> incompletePrincipals() {
        return Stream.of(new JwtPrincipal(null, Role.TEACHER), new JwtPrincipal(10L, null));
    }

    private void authenticate(JwtPrincipal principal) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, List.of()));
    }
}
