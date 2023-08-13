package com.munsun.application.card2card_project.service.impl;

import com.munsun.application.card2card_project.dto.in.CardBalanceDtoIn;
import com.munsun.application.card2card_project.dto.in.CardDtoIn;
import com.munsun.application.card2card_project.dto.out.CardDtoOut;
import com.munsun.application.card2card_project.exception.CardNotFoundException;
import com.munsun.application.card2card_project.model.Card;
import com.munsun.application.card2card_project.repository.CardRepository;
import com.munsun.application.card2card_project.repository.impl.CardRepositoryImpl;
import com.munsun.application.card2card_project.service.CardService;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Log4j2
@Service
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final ModelMapper mapper;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository, ModelMapper mapper) {
        this.cardRepository = cardRepository;
        this.mapper = mapper;
    }

    @PostConstruct
    private void postConstruct() throws CardNotFoundException {
        var c1 = add(new CardDtoIn("RUR"));
        c1 = upBalance(new CardBalanceDtoIn(c1.getCardNumber(), 1000L));
        var c2 = add(new CardDtoIn("RUR"));
        c2 = upBalance(new CardBalanceDtoIn(c2.getCardNumber(), 1000L));
        log.info("Card: number={}, validTill={}, cvv={}, value={}, currency={}",
                c1.getCardNumber(), c1.getCardValidTill(), c1.getCardCVV(), c1.getValue(),
                c1.getCurrency());
        log.info("Card: number={}, validTill={}, cvv={}, value={}, currency={}",
                c2.getCardNumber(), c2.getCardValidTill(), c2.getCardCVV(), c2.getValue(),
                c2.getCurrency());
    }

    @Override
    public CardDtoOut add(CardDtoIn cardDtoIn) {
        log.info("create new card: {}", cardDtoIn);
        Card newCard = new Card();
            newCard.setCurrency(cardDtoIn.getCurrency());
            newCard.setCardValidTill(getCardValidTill());
            newCard.setValue(0L);
            newCard.setCardNumber(getCardNumber());
            newCard.setCardCVV(getCardCvv());
            newCard.setIsActive(true);
        return mapper.map(cardRepository.add(newCard), CardDtoOut.class);
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
        Card card = cardRepository.findCardByNumber(cardBalanceDtoIn.getCardNumber())
                .orElseThrow(() -> {
                    log.error("Card is not found: {}", cardBalanceDtoIn.getCardNumber());
                    return new CardNotFoundException();
                });
        card.setValue(card.getValue() + cardBalanceDtoIn.getValue());
        return mapper.map(card, CardDtoOut.class);
    }

    @Override
    public List<CardDtoOut> getCards() {
        log.info("get cards");
        return cardRepository.getAll().stream()
                .map(x -> mapper.map(x, CardDtoOut.class))
                .toList();
    }

    @Override
    public CardDtoOut findCardByNumber(String number) throws CardNotFoundException {
        return mapper.map(cardRepository.findCardByNumber(number)
                .orElseThrow(CardNotFoundException::new), CardDtoOut.class);
    }
}
