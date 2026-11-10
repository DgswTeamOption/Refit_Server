package com.option.refit.global.verification;

import com.option.refit.global.config.AsyncConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

// 인증코드 메일을 별도 스레드에서 발송. 실패 시 "내가 저장한 코드"일 때만 무효화한다
@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncVerificationMailSender {

    private final VerificationMailSender mailSender;
    private final VerificationCodeStore codeStore;

    @Async(AsyncConfig.MAIL_EXECUTOR)
    public void sendAsync(String email, String code) {
        try {
            mailSender.send(email, code);
        } catch (RuntimeException e) {
            // 재발송으로 새 코드가 저장됐다면 삭제하지 않는다(compare-and-delete)
            boolean removed = codeStore.deleteCodeIfMatches(email, code);
            log.warn("[email] 비동기 발송 실패 codeInvalidated={}", removed, e);
        }
    }
}
