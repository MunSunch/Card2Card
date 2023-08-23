package com.munsun.card2card_project.application.service;

import com.munsun.card2card_project.application.dto.in.CardBalanceDtoIn;
import com.munsun.card2card_project.application.dto.in.CardDtoIn;
import com.munsun.card2card_project.application.dto.out.CardDtoOut;
import com.munsun.card2card_project.application.exception.CardNotFoundException;

import java.util.List;

public interface CardService {
    CardDtoOut add(CardDtoIn cardDtoIn);
    List<CardDtoOut> getCards();
    CardDtoOut upBalance(CardBalanceDtoIn cardBalanceDtoIn) throws CardNotFoundException;
    CardDtoOut findCardByNumber(String number) throws CardNotFoundException;
}
