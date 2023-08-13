package com.munsun.application.card2card_project.service;

import com.munsun.application.card2card_project.dto.in.CardBalanceDtoIn;
import com.munsun.application.card2card_project.dto.in.CardDtoIn;
import com.munsun.application.card2card_project.dto.out.CardDtoOut;
import com.munsun.application.card2card_project.exception.CardNotFoundException;

import java.util.List;

public interface CardService {
    CardDtoOut add(CardDtoIn cardDtoIn);
    List<CardDtoOut> getCards();
    CardDtoOut upBalance(CardBalanceDtoIn cardBalanceDtoIn) throws CardNotFoundException;
    CardDtoOut findCardByNumber(String number) throws CardNotFoundException;
}
