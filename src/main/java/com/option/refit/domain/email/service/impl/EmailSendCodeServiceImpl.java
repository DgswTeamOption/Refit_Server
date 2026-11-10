package com.option.refit.domain.email.service.impl;

import com.option.refit.domain.email.dto.request.EmailSendCodeRequest;
import com.option.refit.domain.email.dto.response.EmailVerificationResponse;
import com.option.refit.domain.email.service.EmailSendCodeService;
import com.option.refit.global.error.authError.exception.EmailSendFailedException;
import com.option.refit.global.verification.AsyncVerificationMailSender;
import com.option.refit.global.verification.VerificationCodeGenerator;
import com.option.refit.global.verification.VerificationCodeStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Service;

// 코드 저장 후 메일 발송을 비동기로 "접수"만 하고 즉시 응답한다. 실제 발송 성공/실패는 응답에 포함되지 않는다
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSendCodeServiceImpl implements EmailSendCodeService {
    private final VerificationCodeGenerator codeGenerator;
    private final VerificationCodeStore codeStore;
    private final AsyncVerificationMailSender asyncMailSender;

    @Override
    public EmailVerificationResponse sendCode(EmailSendCodeRequest request) {
        String email = request.emailId();
        String code = codeGenerator.generate();

        long start = System.nanoTime();
        codeStore.saveCode(email, code);
        try {
            asyncMailSender.sendAsync(email, code);
        } catch (TaskRejectedException e) {
            // 발송 큐가 가득 찬 경우: 접수 자체가 실패했으므로 받을 수 없는 코드를 제거하고 에러 응답
            codeStore.deleteCodeIfMatches(email, code);
            throw new EmailSendFailedException(e);
        }
        log.info("[email] send-code 접수 완료 {}ms", (System.nanoTime() - start) / 1_000_000);

        return EmailVerificationResponse.of(true);
    }
}
