package com.option.refit.global.jwt;

import com.option.refit.global.error.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Authorization: Bearer {token} 헤더를 읽어 SecurityContext에 인증 정보를 저장
// - 토큰 없음 : 그냥 통과 (인가 여부는 SecurityConfig가 결정)
// - 토큰 오류 : 기존 ErrorResponse 형식으로 즉시 응답
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer ";

    private final JwtProvider jwtProvider;
    private final JwtErrorResponseWriter errorResponseWriter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith(BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            SecurityContextHolder.getContext()
                    .setAuthentication(jwtProvider.getAuthentication(header.substring(BEARER.length()).trim()));
        } catch (CustomException e) {
            SecurityContextHolder.clearContext();
            errorResponseWriter.write(response, e.getErrorCode());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
