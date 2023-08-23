package com.munsun.card2card_project.application.service.impl;

import com.munsun.card2card_project.application.dto.in.CardBalanceDtoIn;
import com.munsun.card2card_project.application.dto.in.CardDtoIn;
import com.munsun.card2card_project.application.dto.out.CardDtoOut;
import com.munsun.card2card_project.application.exception.CardNotFoundException;
import com.munsun.card2card_project.application.mapping.CardMapperImpl;
import com.munsun.card2card_project.application.model.Card;
import com.munsun.card2card_project.application.repository.CardRepository;
import com.munsun.card2card_project.application.service.CardService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Log4j2
@Service
@AllArgsConstructor
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final CardMapperImpl mapper;

    @Override
    public CardDtoOut add(CardDtoIn cardDtoIn) {
        log.info("create new card: {}", cardDtoIn);
        Card newCard = new Card();
            newCard.setCurrency(cardDtoIn.currency());
            newCard.setCardValidTill(getCardValidTill());
            newCard.setValue(0L);
            newCard.setCardNumber(getCardNumber());
            newCard.setCardCVV(getCardCvv());
            newCard.setIsActive(true);
        return mapper.map(cardRepository.add(newCard));
    }

    private String getCardValidTill() {
        log.info("generate card valid till");
        var date = LocalDate.now();
        String month = date.getMonthValue() < 10
                ? "0"+date.getMonthValue()
                : "" + date.getMonthValue();
        return month + "/" + (date.getYear()+5)%100;
    }

    private String getCardNumber() {
        log.info("generate card number");
        return String.valueOf(getRandomNum(1000_0000_0000_0000L, 9999_9999_9999_9999L));
    }

    private String getCardCvv() {
        log.info("generate card cvv");
        return String.valueOf(getRandomNum(100L, 999L));
    }

    private long getRandomNum(Long min,Long max) {
        var rand = new Random();
        return rand.nextLong(min, max);
    }

    @Override
    public CardDtoOut upBalance(CardBalanceDtoIn cardBalanceDtoIn) throws CardNotFoundException {
        log.info("up card balance: {}", cardBalanceDtoIn);
        Card card = cardRepository.findCardByNumber(cardBalanceDtoIn.cardNumber())
                .orElseThrow(() -> {
                    log.error("Card is not found: {}", cardBalanceDtoIn.cardNumber());
                    return new CardNotFoundException();
                });
        card.setValue(card.getValue() + cardBalanceDtoIn.value());
        return mapper.map(card);
    }

    @Override
    public List<CardDtoOut> getCards() {
        log.info("get cards");
        return cardRepository.getAll().stream()
                .map(mapper::map)
                .toList();
    }

    @Override
    public CardDtoOut findCardByNumber(String number) throws CardNotFoundException {
        return mapper.map(cardRepository.findCardByNumber(number)
                .orElseThrow(CardNotFoundException::new));
    }

    public void clear() {
        cardRepository.clear();
    }
}
