package com.option.refit.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController //이 파일은 화면(HTML)을 보여주는 곳이 아닌 프론트엔드와 순수 데이터(문자열, JSON)를 주고받는 API 창구라고 스프링에게 알림
@RequestMapping("/api/v1/auth") //이 클래스 안에 있는 모든 API 주소 앞에 /api/v1/auth를 기본 접두어로 자동 추가합니다.
@RequiredArgsConstructor //아래에 있는 GoogleService 부품을 스프링이 알아서 가져와 차곡차곡 연결해 주도록 만드는 자동화 도구
public class GoogleController {

    private final GoogleService googleService; //구글와 통신하고 토큰을 만드는 복잡한 일을 처리

    @GetMapping("/google/url") //사용자가 GET 방식으로 /api/v1/auth/google/url 주소에 접속하면 바로 아래 코드를 실행시킴
    public ResponseEntity<String> getGoogleLoginUrl() {
        return ResponseEntity.ok(googleService.getGoogleLoginUrl());
    }

    @GetMapping("/google/callback")
    public ResponseEntity<String> googleCallback(@RequestParam("code") String code) {
        String jwtToken = googleService.processGoogleLogin(code);
        return ResponseEntity.ok(jwtToken);
    }
}
