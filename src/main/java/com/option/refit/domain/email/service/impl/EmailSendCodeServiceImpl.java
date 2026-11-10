package com.option.refit.domain.email.service.impl;

import com.option.refit.domain.email.dto.request.EmailSendCodeRequest;
import com.option.refit.domain.email.dto.response.EmailVerificationResponse;
import com.option.refit.domain.email.service.EmailSendCodeService;
import com.option.refit.global.verification.VerificationCodeGenerator;
import com.option.refit.global.verification.VerificationCodeStore;
import com.option.refit.global.verification.VerificationMailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSendCodeServiceImpl implements EmailSendCodeService {
    private final VerificationCodeGenerator codeGenerator;
    private final VerificationCodeStore codeStore;
    private final VerificationMailSender mailSender;

    @Override
    public EmailVerificationResponse sendCode(EmailSendCodeRequest request) {
        String email = request.emailId();
        String code = codeGenerator.generate();

        long start = System.nanoTime();
        codeStore.saveCode(email, code);
        long saved = System.nanoTime();
        try {
            mailSender.send(email, code);
            log.info("[email] send-code 총 {}ms (코드 저장 {}ms, 메일 발송 {}ms)",
                    (System.nanoTime() - start) / 1_000_000, (saved - start) / 1_000_000, (System.nanoTime() - saved) / 1_000_000);
        } catch (RuntimeException e) {
            // 발송 실패 시 받을 수 없는 코드가 남지 않도록 제거
            codeStore.deleteCode(email);
            throw e;
        }

        return EmailVerificationResponse.of(true);
    }
}
