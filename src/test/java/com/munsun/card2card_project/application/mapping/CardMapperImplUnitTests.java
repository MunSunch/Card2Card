package com.munsun.card2card_project.application.mapping;

import com.munsun.card2card_project.application.dto.out.CardDtoOut;
import com.munsun.card2card_project.application.model.Card;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CardMapperImplUnitTests {
    @Autowired
    private CardMapperImpl mapper;
    private Card testCard;

    @BeforeEach
    public void setTestCard() {
        testCard = new Card();
            testCard.setCardNumber("1111222233334444");
            testCard.setCardValidTill("08/29");
            testCard.setCardCVV("555");
            testCard.setCurrency("RUR");
            testCard.setValue(1000L);
            testCard.setIsActive(true);
    }

    @Test
    public void testCardToCardDtoOut() {
        var expected = new CardDtoOut(
                "1111222233334444",
                "08/29",
                "555",
                "RUR",
                1000L,
                true
        );
        var actual = mapper.map(testCard);
        Assertions.assertEquals(expected, actual);
    }
}
