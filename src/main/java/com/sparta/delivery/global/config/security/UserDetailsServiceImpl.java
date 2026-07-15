package com.sparta.delivery.global.config.security;

import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import com.sparta.delivery.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // UserName 으로 유저 찾기
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException(username + " 님을 찾을 수 없습니다.")
        );

        return new UserDetailsImpl(user);
    }
}
