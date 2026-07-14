package com.sparta.delivery.domain.user.service;

import com.sparta.delivery.domain.user.dto.request.UpdateUserReqDto;
import com.sparta.delivery.domain.user.dto.response.UpdateUserResDto;
import com.sparta.delivery.domain.user.dto.response.UserResDto;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import com.sparta.delivery.global.common.Enums;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Page<UserResDto> getUsers(int page, int size, String sortBy, boolean isAsc, String keyword) {

        // 페이징 처리
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> userPage;
        if (keyword != null && !keyword.isBlank()) {
            userPage = userRepository.searchByUsernameOrRole(keyword, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        return userPage.map(UserResDto::from);
    }

    @Transactional(readOnly = true)
    public UserResDto getUserDetail(Long id) {

        User user = userRepository.findByIdOrElseThrow(id);

        return new UserResDto(user);
    }


    @Transactional
    public UpdateUserResDto UpdateUser(Long id, User loginUser, UpdateUserReqDto userReqDto) {

        // 유저 검증
        User user = userRepository.findById(id).orElseThrow(()
                -> new UsernameNotFoundException("Not Found User"));

        // 관리자 권한 체크
        boolean isAdmin = (loginUser.getRole() == Enums.UserRole.MASTER) || (loginUser.getRole() == Enums.UserRole.MANAGER);

        // 중복 유저 검증
        userRepository.findByUsername(userReqDto.getUsername()).ifPresent(checkUser -> {
            throw new IllegalArgumentException("이미 존재하는 유저입니다.");
        });

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userReqDto.getPassword());

        // 공통 수정 가능 항목
        user.updateUser(
                userReqDto.getUsername(),
                encodedPassword,
                userReqDto.getName(),
                userReqDto.getPhone());

        // 관리자 전용 수정 항목
        if (userReqDto.getRole() != null) {
            if (!isAdmin) {
                throw new AccessDeniedException("관리자만 권한(Role)을 변경할 수 있습니다.");
            }

            // 관리자가 맞다면 권한 변경 허용
            user.updateRole(userReqDto.getRole());
        }

        return UpdateUserResDto.from(user);
    }

    @Transactional
    public void userIsDelete(Long loginId) {

        User user = userRepository.findById(loginId).orElseThrow(()
                -> new UsernameNotFoundException("Not Found User"));

        userRepository.delete(user);
    }
}
