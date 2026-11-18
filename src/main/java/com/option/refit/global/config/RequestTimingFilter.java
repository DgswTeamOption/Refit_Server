package com.option.refit.global.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 요청별 응답 시간을 로그로 남겨 느린 구간을 찾는다. 개선 전/후 비교용.
// 기본은 DEBUG로 전부 기록하고, SLOW_THRESHOLD_MS 이상 걸린 요청은 WARN으로 남긴다.
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // 보안 필터 시간까지 포함해 측정
public class RequestTimingFilter extends OncePerRequestFilter {

    private static final long SLOW_THRESHOLD_MS = 1000;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs") || uri.startsWith("/uploads/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        long start = System.nanoTime();
        try {
            chain.doFilter(request, response);
        } finally {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            if (elapsedMs >= SLOW_THRESHOLD_MS) {
                log.warn("[slow] {} {} -> {} {}ms", request.getMethod(), request.getRequestURI(), response.getStatus(), elapsedMs);
            } else {
                log.debug("[timing] {} {} -> {} {}ms", request.getMethod(), request.getRequestURI(), response.getStatus(), elapsedMs);
            }
        }
    }
}
