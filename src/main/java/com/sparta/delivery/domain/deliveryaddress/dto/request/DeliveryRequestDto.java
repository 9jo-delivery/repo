package com.sparta.delivery.domain.deliveryaddress.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeliveryRequestDto {
    private Long userId;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    private String detailAddress;
    private String zipcode;
    private String alias;
    private Boolean isDefault;
}
