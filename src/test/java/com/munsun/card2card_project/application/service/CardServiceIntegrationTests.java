package com.munsun.card2card_project.application.service;

import com.munsun.card2card_project.application.dto.in.CardBalanceDtoIn;
import com.munsun.card2card_project.application.dto.in.CardDtoIn;
import com.munsun.card2card_project.application.exception.CardNotFoundException;
import com.munsun.card2card_project.application.service.impl.CardServiceImpl;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CardServiceIntegrationTests {
    @Autowired
    private CardServiceImpl cardService;

    @AfterEach
    public void clearRepository() {
        cardService.clear();
    }

    @Test
    public void addNewCard() throws CardNotFoundException {
        var dto = new CardDtoIn("RUR");
        var number = cardService.add(dto).cardNumber();
        var actual = cardService.findCardByNumber(number);

        assertAll(()->{
            assertTrue(actual.cardNumber().matches("^\\d{16}$"));
            assertTrue(actual.cardCVV().matches("^\\d{3}$"));
            assertTrue(actual.cardValidTill().matches("^(0[1-9]|[10-12])/[2-3][3-9]$"));
            assertEquals(dto.currency(), actual.currency());
            assertEquals(0L, actual.value());
            assertTrue(actual.isActive());
        });
    }

    @Test
    public void upBalance() throws CardNotFoundException {
        var cardDtoIn = new CardDtoIn("RUR");
        var number = cardService.add(cardDtoIn).cardNumber();
        var cardBalanceDtoIn = new CardBalanceDtoIn(number, 500L);

        long expectedValue = 500L;
        cardService.upBalance(cardBalanceDtoIn);
        long actualValue = cardService.findCardByNumber(number).value();

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void upBalance_CardNotFoundException() throws CardNotFoundException {
        var number = "1111222233334444";
        var cardBalanceDtoIn = new CardBalanceDtoIn(number, 500L);

        assertThrowsExactly(CardNotFoundException.class, ()-> {
            cardService.upBalance(cardBalanceDtoIn);
        });
    }

    @Test
    public void getCards() {
        var dto = new CardDtoIn("RUR");
        cardService.add(dto);
        cardService.add(dto);
        cardService.add(dto);

        int expectedSize = 3;
        int actualSize = cardService.getCards().size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    public void findCardByNumber_CardNotFound() throws CardNotFoundException {
        assertThrowsExactly(CardNotFoundException.class, ()-> {
            cardService.findCardByNumber("1111222233334444");
        });
    }

    @Test
    public void findCardByNumber() throws CardNotFoundException {
        var dto = new CardDtoIn("RUR");
        var number = cardService.add(dto).cardNumber();
        var actual = cardService.findCardByNumber(number);

        assertAll(()->{
            assertTrue(actual.cardNumber().matches("^\\d{16}$"));
            assertTrue(actual.cardCVV().matches("^\\d{3}$"));
            assertTrue(actual.cardValidTill().matches("^(0[1-9]|[10-12])/[2-3][3-9]$"));
            assertEquals(dto.currency(), actual.currency());
            assertEquals(0L, actual.value());
            assertTrue(actual.isActive());
        });
    }
}
