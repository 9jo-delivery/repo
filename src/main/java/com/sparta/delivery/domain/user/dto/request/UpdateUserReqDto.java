package com.sparta.delivery.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateUserReqDto {

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 15, message = "비밀번호는 8자 이상 15자리 이하입니다.")
    @Pattern(regexp = ".*\\d.*", message = "비밀번호는 숫자를 포함해야 합니다.")
    @Pattern(regexp = ".*[a-zA-Z].*", message = "비밀번호는 영문자를 포함해야 합니다.")
    @Pattern(regexp = ".*\\p{Punct}.*", message = "비밀번호는 특수문자를 포함해야 합니다.")
    private final String password;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^010-\\d{3,4}-\\d{4}$", message = "유효한 휴대폰 번호 형식이 아닙니다.")
    private final String phone;
}
