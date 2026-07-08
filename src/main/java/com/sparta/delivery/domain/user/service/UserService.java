package com.sparta.delivery.domain.user.service;

import com.sparta.delivery.domain.user.dto.response.GetMyInfoResDto;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public GetMyInfoResDto getUserById(Long id) {

        // 조회
        User user = userRepository.findById(id).orElseThrow(()
                -> new UsernameNotFoundException("Not Found User"));

        // 자기 자신 검증
        if (!id.equals(user.getId())) {
            throw new IllegalArgumentException("본인의 프로필만 조회 가능합니다.");
        }

        return new GetMyInfoResDto(user);
    }
}
