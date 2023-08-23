package com.munsun.card2card_project.application.mapping;

import com.munsun.card2card_project.application.dto.out.CardDtoOut;
import com.munsun.card2card_project.application.model.Card;
import org.springframework.stereotype.Component;

@Component
public class CardMapperImpl {
    public CardDtoOut map(Card card) {
        return new CardDtoOut(
                card.getCardNumber(),
                card.getCardValidTill(),
                card.getCardCVV(),
                card.getCurrency(),
                card.getValue(),
                card.getIsActive()
        );
    }
}