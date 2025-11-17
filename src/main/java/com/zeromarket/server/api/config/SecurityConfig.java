package com.zeromarket.server.api.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. HTTP 요청에 대한 접근 권한 설정
            .authorizeHttpRequests(authorize -> authorize
                // ⬇️ 여기를 수정해야 합니다. ⬇️
                // /board/로 시작하는 모든 요청은 인증 없이 접근 허용 (로그인 필요 없음)
                .requestMatchers("/**").permitAll()
                // /api/로 시작하는 요청은 임시로 허용 (개발 중에는 유용)
                .requestMatchers("/api/**").permitAll()
                // 나머지 모든 요청은 인증(로그인)이 필요함
                .anyRequest().authenticated()
            )
            // 2. 폼 로그인 설정 (기본값)
            .formLogin(withDefaults())
            // 3. CSRF 비활성화 (API 서버인 경우 임시로 비활성화 가능)
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    // 비밀번호 암호화를 위한 Bean (필요하다면)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}