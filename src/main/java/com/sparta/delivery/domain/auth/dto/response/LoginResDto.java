package com.sparta.delivery.domain.auth.dto.response;

import com.sparta.delivery.global.common.Enums;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginResDto {

    private final Long userId;

    private final String username;

    private final String accessToken;

    private final Enums.UserRole role;
}
