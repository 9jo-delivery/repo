package com.sparta.delivery.domain.deliveryaddress.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeliveryRequestDto {

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    private String detailAddress;

    private String zipcode;

    private String alias;

    @NotNull
    private Boolean isDefault;
}
