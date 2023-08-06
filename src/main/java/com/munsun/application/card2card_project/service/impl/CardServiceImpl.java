package com.munsun.application.card2card_project.service.impl;

import com.munsun.application.card2card_project.dto.in.CardBalanceDtoIn;
import com.munsun.application.card2card_project.dto.in.CardDtoIn;
import com.munsun.application.card2card_project.dto.out.CardDtoOut;
import com.munsun.application.card2card_project.exception.CardNotFoundException;
import com.munsun.application.card2card_project.model.Card;
import com.munsun.application.card2card_project.repository.impl.CardRepositoryImpl;
import com.munsun.application.card2card_project.service.CardService;
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
    private CardRepositoryImpl cardRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CardServiceImpl(CardRepositoryImpl cardRepository, ModelMapper modelMapper) {
        this.cardRepository = cardRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CardDtoOut add(CardDtoIn cardDtoIn) {
        log.info("create new card");
        Card newCard = new Card();
            newCard.setCurrency(cardDtoIn.getCurrency());
            newCard.setCardValidTill(getCardValidTill());
            newCard.setValue(0L);
            newCard.setCardNumber(getCardNumber());
            newCard.setCardCVV(getCardCvv());
//        return mapper.map(cardRepository.add(newCard));
        return null;
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

    protected long getRandomNum(Long min,Long max) {
        var rand = new Random();
        return rand.nextLong(min, max);
    }

    @Override
    public CardDtoOut upBalance(CardBalanceDtoIn cardBalanceDtoIn) throws CardNotFoundException {
        log.info("up card balance");
        Card card = cardRepository.findCardByNumber(cardBalanceDtoIn.getCardNumber())
                .orElseThrow(() -> {
                    log.error("Card is not found: "+cardBalanceDtoIn.getCardNumber());
                    return new CardNotFoundException();
                });
        card.setValue(card.getValue() + cardBalanceDtoIn.getValue());
//        return mapper.map(card);
        return null;
    }

    @Override
    public List<CardDtoOut> getCards() {
//        return cardRepository.getAll().stream()
//                .map(mapper::map)
//                .toList();
        return null;
    }
}
