package com.sparta.delivery.domain.user.dto.response;

import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.global.common.Enums;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class UpdateUserResDto {

    private final Long userId;

    private final String username;

    private final String name;

    private final String phone;

    private final Enums.UserRole role;

    private final LocalDateTime updatedAt;

    public static UpdateUserResDto from(User user) {
        return new UpdateUserResDto(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getPhone(),
                user.getRole(),
                user.getUpdatedAt()
        );
    }
}
