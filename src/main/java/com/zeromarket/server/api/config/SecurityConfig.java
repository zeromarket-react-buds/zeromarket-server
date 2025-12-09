package com.zeromarket.server.api.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.zeromarket.server.api.security.CustomUserDetailService;
import com.zeromarket.server.api.security.JwtFilter;
import com.zeromarket.server.api.security.JwtUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailService customUserDetailService;

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtUtil, customUserDetailService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. HTTP 요청에 대한 접근 권한 설정
            .authorizeHttpRequests(authorize -> authorize
                // 1. GET /api/products/** (목록, 상세 조회)는 인증 없이 접근 허용
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                // 2. POST, PATCH, DELETE 등 나머지 /api/products/** 요청은 인증 필요
                // 상품 등록 (POST), 수정 (PATCH), 삭제 (DELETE)
                .requestMatchers("/api/products/**").authenticated()

                // 3. 로그인, 회원가입 관련은 인증 없이 접근 허용
                .requestMatchers("/api/auth/**").permitAll()

                // 4. 나머지 모든 요청은 인증이 필요함
                .anyRequest().authenticated()
            )
            // 2. 폼 로그인 설정 (기본값)
            .formLogin(withDefaults())
            // 3. CSRF 비활성화 (API 서버인 경우 임시로 비활성화 가능)
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
            .cors(cors -> cors.configurationSource(corsSecurityConfig()));

        return http.build();
    }

    // 비밀번호 암호화를 위한 Bean (필요하다면)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS 설정 추가
    @Bean
    public CorsConfigurationSource corsSecurityConfig() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
            "http://localhost:5173",
            "http://localhost:5174",
            "http://localhost:5175",
            "http://localhost:5176"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Authorization")); // 필요 시 노출

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}