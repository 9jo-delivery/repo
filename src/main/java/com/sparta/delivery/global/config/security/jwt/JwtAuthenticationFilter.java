package com.sparta.delivery.global.config.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.delivery.domain.auth.dto.request.LoginReqDto;
import com.sparta.delivery.domain.auth.dto.response.LoginResDto;
import com.sparta.delivery.global.common.Enums;
import com.sparta.delivery.global.config.security.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response
    ) throws AuthenticationException {
        log.info("로그인 시도");
        try {
            LoginReqDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginReqDto.class);

            if (requestDto.getUsername() == null || requestDto.getUsername().isBlank() ||
                    requestDto.getPassword() == null || requestDto.getPassword().isBlank()) {
                throw new AuthenticationServiceException("닉네임과 비밀번호는 필수 입력 항목입니다.");
            }

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUsername(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult
    ) throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성");
        Long userId = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getId();
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        Enums.UserRole role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();

        String token = jwtUtil.createToken(username, role);
        jwtUtil.addJwtToCookie(token, response);

        LoginResDto resLoginDto = new LoginResDto(userId, username, token, role);

        // HTTP 응답 헤더 설정 (JSON 타입 및 인코딩 명시)
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK); // 200 OK 상태 코드

        // ObjectMapper를 사용하여 DTO 객체를 JSON 문자열로 직렬화 후 바디에 출력
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(resLoginDto);

        // Response 반환
        response.getWriter().write(jsonResponse);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed
    ) throws IOException, ServletException {
        log.info("로그인 실패");
        response.setStatus(401);
    }
}
