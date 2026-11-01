package com.option.refit.global.jwt;

import com.option.refit.domain.user.entity.Role;

// 인증된 로그인 유저 정보 (토큰의 userId, role). DB 조회 없이 컨트롤러에서 바로 사용
public record JwtPrincipal(Long userId, Role role) {
}
