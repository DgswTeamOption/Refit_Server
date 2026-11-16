package com.option.refit.global.jwt;

// 요청을 보낸 클라이언트 종류. X-Client: web 헤더가 있으면 WEB, 그 외(모바일 앱 등)는 APP.
// Refresh Token을 클라이언트별로 따로 저장해, 웹/앱 동시 로그인이 서로의 세션을 덮어쓰지 않게 한다.
public enum ClientType {
    WEB, APP;

    public static ClientType from(String header) {
        return "web".equalsIgnoreCase(header) ? WEB : APP;
    }
}
