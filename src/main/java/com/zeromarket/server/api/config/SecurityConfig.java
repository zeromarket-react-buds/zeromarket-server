package com.zeromarket.server.api.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.zeromarket.server.api.security.JwtFilter;
import com.zeromarket.server.api.security.JwtUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. HTTP 요청에 대한 접근 권한 설정
            .authorizeHttpRequests(authorize -> authorize
                // ⬇️ 여기를 수정해야 합니다. ⬇️
                // /board/로 시작하는 모든 요청은 인증 없이 접근 허용 (로그인 필요 없음)
                .requestMatchers("/**").permitAll()
                // /api/로 시작하는 요청은 임시로 허용 (개발 중에는 유용)
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()
                // 나머지 모든 요청은 인증(로그인)이 필요함
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