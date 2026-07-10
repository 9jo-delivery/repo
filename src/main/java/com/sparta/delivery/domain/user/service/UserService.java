package com.sparta.delivery.domain.user.service;

import com.sparta.delivery.domain.user.dto.request.AdminUpdateUserReqDto;
import com.sparta.delivery.domain.user.dto.request.UpdateUserReqDto;
import com.sparta.delivery.domain.user.dto.response.UpdateUserResDto;
import com.sparta.delivery.domain.user.dto.response.UserResDto;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResDto getUserById(Long id) {

        // 조회
        User user = userRepository.findById(id).orElseThrow(()
                -> new UsernameNotFoundException("Not Found User"));

        return new UserResDto(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResDto> getUsers(int page, int size, String sortBy, boolean isAsc) {

        // 페이징 처리
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return userRepository.findAll(pageable).map(UserResDto::from);
    }

    @Transactional(readOnly = true)
    public UserResDto getUserDetail(Long id) {

        User user = userRepository.findByIdOrElseThrow(id);

        return new UserResDto(user);
    }

    @Transactional
    public UpdateUserResDto updateUser(Long loginId, UpdateUserReqDto userReqDto) {

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userReqDto.getPassword());

       User user = userRepository.findById(loginId).orElseThrow(()
                -> new UsernameNotFoundException("Not Found User"));

        user.updateUser(
                encodedPassword,
                userReqDto.getPhone()
        );

        return UpdateUserResDto.from(user);
    }

    @Transactional
    public UpdateUserResDto AdminUpdateUser(Long id, AdminUpdateUserReqDto userReqDto) {

        // 유저 검증
        User user = userRepository.findById(id).orElseThrow(()
                -> new UsernameNotFoundException("Not Found User"));

        // 중복 유저 검증
        Optional<User> checkUsername = userRepository.findByUsername(userReqDto.getUsername());
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 유저입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userReqDto.getPassword());

        user.adminUpdateUser(userReqDto, encodedPassword);

        return UpdateUserResDto.from(user);
    }

    @Transactional
    public void userIsDelete(Long loginId) {

        User user = userRepository.findById(loginId).orElseThrow(()
                -> new UsernameNotFoundException("Not Found User"));

        userRepository.delete(user);
    }
}
