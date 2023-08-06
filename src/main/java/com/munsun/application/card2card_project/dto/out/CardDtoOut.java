package com.munsun.application.card2card_project.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDtoOut {
    private String cardNumber;
    private String cardValidTill;
    private String cardCVV;
    private String currency;
    private Long value;
    private Boolean isActive;
}
