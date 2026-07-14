package com.sparta.delivery.domain.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConfirmPaymentRequestDto {

    @NotBlank(message = "카드사 정보는 필수입니다.")
    private String cardCompany;

    @NotBlank(message = "마스킹된 카드번호는 필수입니다.")
    private String cardNumberMasked;

    @NotBlank(message = "거래 ID는 필수입니다.")
    private String transactionId;
}
