package com.munsun.card2card_project.application.repository;

import com.munsun.card2card_project.application.exception.CardNotFoundException;
import com.munsun.card2card_project.application.model.Card;
import static org.junit.jupiter.api.Assertions.*;

import com.munsun.card2card_project.application.repository.impl.CardRepositoryImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.TestPropertySource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class CardRepositoryImplUnitTests {
    @Autowired
    private CardRepositoryImpl cardRepository;
    private Card card;

    @BeforeAll
    public void setCard() {
        card = new Card();
            card.setCardNumber("1111222233334444");
            card.setCardValidTill("10/30");
            card.setCardCVV("555");
            card.setCurrency("RUR");
            card.setValue(1000L);
    }

    @AfterEach
    public void clearCardRepository() {
        cardRepository.clear();
    }

    @Test
    public void addNewCard() {
        cardRepository.add(card);
        var actual = cardRepository.findCardByNumber("1111222233334444");

        assertFalse(actual.isEmpty());
        assertAll(()->{
            assertEquals("1111222233334444", actual.get().getCardNumber());
            assertEquals("10/30", actual.get().getCardValidTill());
            assertEquals("555", actual.get().getCardCVV());
            assertEquals("RUR", actual.get().getCurrency());
            assertEquals(1000L, actual.get().getValue());
        });
    }

    @Test
    public void findCardByNumberAndValidTillAndCvv_NotFound() {
        var actual = cardRepository.findCardByNumberAndValidTillAndCvv(card.getCardNumber(),
                card.getCardValidTill(), card.getCardCVV());
        assertTrue(actual.isEmpty());
    }

    @Test
    public void findCardByNumberAndValidTillAndCvv() {
        cardRepository.add(card);

        var actual = cardRepository.findCardByNumberAndValidTillAndCvv(card.getCardNumber(),
                card.getCardValidTill(), card.getCardCVV());
        assertTrue(actual.isPresent());
        assertAll(()->{
            assertEquals("1111222233334444", actual.get().getCardNumber());
            assertEquals("10/30", actual.get().getCardValidTill());
            assertEquals("555", actual.get().getCardCVV());
            assertEquals("RUR", actual.get().getCurrency());
            assertEquals(1000L, actual.get().getValue());
        });
    }

    @Test
    public void findCardByNumber_NotFound() {
        var actual = cardRepository.findCardByNumber(card.getCardNumber());
        assertTrue(actual.isEmpty());
    }

    @Test
    public void findCardByNumber() {
        cardRepository.add(card);
        var actual = cardRepository.findCardByNumber(card.getCardNumber());

        assertTrue(actual.isPresent());
        assertAll(()->{
            assertEquals("1111222233334444", actual.get().getCardNumber());
            assertEquals("10/30", actual.get().getCardValidTill());
            assertEquals("555", actual.get().getCardCVV());
            assertEquals("RUR", actual.get().getCurrency());
            assertEquals(1000L, actual.get().getValue());
        });
    }

    @Test
    public void firstTestGetAll() {
        cardRepository.add(card);
        cardRepository.add(card);
        cardRepository.add(card);
        cardRepository.add(card);
        int expectedSize = 4;

        int actualSize = cardRepository.getAll().size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    public void secondTestGetAll() {
        int expectedSize = 0;
        int actualSize = cardRepository.getAll().size();
        assertEquals(expectedSize, actualSize);
    }

    @Test
    public void setValueCard() throws CardNotFoundException {
        cardRepository.add(card);
        cardRepository.setValueCard(card.getCardNumber(), 0L);
        long expectedValue = 0L;
        long actualValue = cardRepository.findCardByNumber(card.getCardNumber())
                .get().getValue();

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void setValueCard_CardNotFound() throws CardNotFoundException {
        assertThrowsExactly(CardNotFoundException.class, ()-> {
            cardRepository.setValueCard(card.getCardNumber(), 0L);
        });
    }
}
