package com.sparta.delivery.domain.user.dto.response;

import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.global.common.Enums;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class UserResDto {

    private final Long userId;

    private final String username;

    private final String name;

    private final String phone;

    private final Enums.UserRole role;

    private final LocalDateTime createdAt;

    public UserResDto(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
    }

    public static UserResDto from(User user) {
        return new UserResDto(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getPhone(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
