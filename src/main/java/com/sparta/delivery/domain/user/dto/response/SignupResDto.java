package com.sparta.delivery.domain.user.dto.response;


import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.global.common.Enums;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignupResDto {

    private final Long userId;

    private final String username;

    private final String name;

    private final Enums.UserRole role;

    private final LocalDateTime createdAt;

    public SignupResDto (User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
    }
}
