package com.option.refit.domain.email.service;

import com.option.refit.domain.email.dto.request.EmailSendCodeRequest;
import com.option.refit.domain.email.service.impl.EmailSendCodeServiceImpl;
import com.option.refit.global.error.authError.exception.EmailSendFailedException;
import com.option.refit.global.verification.AsyncVerificationMailSender;
import com.option.refit.global.verification.VerificationCodeGenerator;
import com.option.refit.global.verification.VerificationCodeStore;
import com.option.refit.global.verification.VerificationMailSender;
import org.junit.jupiter.api.Test;
import org.springframework.core.task.TaskRejectedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class EmailSendCodeServiceTest {

    private final VerificationCodeGenerator generator = mock(VerificationCodeGenerator.class);
    private final VerificationCodeStore store = mock(VerificationCodeStore.class);
    private final AsyncVerificationMailSender asyncSender = mock(AsyncVerificationMailSender.class);
    private final EmailSendCodeServiceImpl service = new EmailSendCodeServiceImpl(generator, store, asyncSender);

    @Test
    void 코드를_저장하고_발송을_접수하면_즉시_응답한다() {
        when(generator.generate()).thenReturn("123456");

        assertThat(service.sendCode(new EmailSendCodeRequest("a@b.com")).isVerification()).isTrue();

        verify(store).saveCode("a@b.com", "123456");
        verify(asyncSender).sendAsync("a@b.com", "123456");
    }

    @Test
    void 발송_큐가_가득_차면_저장한_코드만_지우고_예외를_던진다() {
        when(generator.generate()).thenReturn("123456");
        doThrow(new TaskRejectedException("full")).when(asyncSender).sendAsync("a@b.com", "123456");

        assertThatThrownBy(() -> service.sendCode(new EmailSendCodeRequest("a@b.com")))
                .isInstanceOf(EmailSendFailedException.class);

        verify(store).deleteCodeIfMatches("a@b.com", "123456");
    }

    @Test
    void 비동기_발송_실패시_내가_저장한_코드일_때만_무효화한다() {
        VerificationMailSender mailSender = mock(VerificationMailSender.class);
        doThrow(new EmailSendFailedException(new RuntimeException())).when(mailSender).send("a@b.com", "old");
        when(store.deleteCodeIfMatches("a@b.com", "old")).thenReturn(false); // 이미 새 코드로 교체됨

        new AsyncVerificationMailSender(mailSender, store).sendAsync("a@b.com", "old");

        verify(store).deleteCodeIfMatches("a@b.com", "old");
        verify(store, never()).deleteCode(anyString());
    }
}
