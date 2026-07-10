package com.sparta.delivery.domain.auth.service;

import com.sparta.delivery.domain.auth.dto.request.SignupReqDto;
import com.sparta.delivery.domain.auth.dto.response.SignupResDto;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import com.sparta.delivery.global.common.Enums;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResDto signup(SignupReqDto reqDto) {

        // 중복 유저 검증
        Optional<User> checkUsername = userRepository.findByUsername(reqDto.getUsername());
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 유저입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(reqDto.getPassword());

        // 유저 DB에 등록
        User user = userRepository.save(
                User.builder()
                        .username(reqDto.getUsername())
                        .password(encodedPassword)
                        .name(reqDto.getName())
                        .phone(reqDto.getPhone())
                        .role(Enums.UserRole.CUSTOMER)
                        .build());

        return new SignupResDto(user);
    }
}
