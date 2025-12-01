package com.zeromarket.server.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailService customUserDetailService;

    public JwtFilter(JwtUtil jwtUtil, CustomUserDetailService customUserDetailService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailService = customUserDetailService;
    }

    private static final String[] EXCLUDED_PATHS = {
        "/api",
//        "/api/auth",
//        "/api/products",
        "/swagger-ui",
        "/v3/api-docs"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        for (String excluded : EXCLUDED_PATHS) {
            if (path.startsWith(excluded)) {
                return true;
            }
        }
        return false;
//        return path.startsWith("/api/auth"); // '로그인/회원가입/refresh요청'은 필터 건너뛰기
//        return path.startsWith("/api");
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String token = extractToken(request);

//            1. 토큰 없음 -> TOKEN_MISSING
            if(token == null){
                sendError(response, 401, "TOKEN_MISSING", "토큰이 없습니다.");
                return;
            }

//            2. 토큰 검증
            jwtUtil.validateAccessToken(token); // 여기서 만료되면 예외 발생

//            3. 인증 성공
            String loginId = jwtUtil.getLoginId(token);
            String role = jwtUtil.getRole(token);

//            3-1. DB에서 CustomUserDetails 불러오기
            CustomUserDetails userDetails = (CustomUserDetails) customUserDetailService.loadUserByUsername(loginId);

//            3-2. Authentication 객체 만들기
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                        userDetails.getAuthorities()
//                    List.of(new SimpleGrantedAuthority(role))
                );

//            3-3. SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
//            4. 토큰 만료 -> TOKEN_EXPIRED (Refresh Flow 대상)
            sendError(response, 401, "TOKEN_EXPIRED", "토큰이 만료되었습니다.");

        } catch (io.jsonwebtoken.security.SignatureException e) {
//            5. 토큰 변조 -> TOKEN_INVALID
            sendError(response, 401, "TOKEN_INVALID", "토큰 서명이 유효하지 않습니다");

        } catch (MalformedJwtException e) {
//            6. 토큰 형식 오류 -> TOKEN_MALFORMED
            sendError(response, 401, "TOKEN_MALFORMED", "토큰 형식이 잘못되었습니다");

        } catch (Exception e) {
//            7. 기타 오류 -> TOKEN_ERROR
            sendError(response, 401, "TOKEN_ERROR", "토큰 검증 실패");
        }
    }

    //  추가 정보(에러 코드, 메시지)을 넣어서 에러 응답 반환
    private void sendError(
        HttpServletResponse response,
        int status,
        String code,
        String message
    ) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("code", code);  // 🔑 핵심: 에러 코드
        errorResponse.put("message", message);
        errorResponse.put("timestamp", LocalDateTime.now().toString());

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
