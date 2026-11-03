package com.option.refit.global.verification;

import com.option.refit.global.error.authError.exception.EmailSendFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

// 인증코드 메일 발송. 실패 시 EmailSendFailedException
@Component
@RequiredArgsConstructor
public class VerificationMailSender {

    private static final String SUBJECT = "[Refit] 이메일 인증코드";

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${jwt.email-code-expiration-ms}")
    private long codeExpirationMs;

    public void send(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(SUBJECT);
        message.setText("인증코드: " + code + "\n\n" + (codeExpirationMs / 60_000) + "분 이내에 입력해주세요.");
        try {
            mailSender.send(message);
        } catch (MailException e) {
            throw new EmailSendFailedException(e);
        }
    }
}
