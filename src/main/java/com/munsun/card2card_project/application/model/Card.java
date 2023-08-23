package com.munsun.card2card_project.application.model;

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

    public Card(Card anotherCard) {
        this.cardNumber = anotherCard.getCardNumber();
        this.cardValidTill = anotherCard.getCardValidTill();
        this.cardCVV = anotherCard.getCardCVV();
        this.value = anotherCard.getValue();
        this.currency = anotherCard.getCurrency();
        this.isActive = anotherCard.getIsActive();
    }
}
