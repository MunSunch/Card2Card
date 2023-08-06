package com.munsun.application.card2card_project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Card {
    private String cardNumber;
    private String cardValidTill;
    private String cardCVV;
    private String currency;
    private Long value;
    private Boolean isActive;
}
