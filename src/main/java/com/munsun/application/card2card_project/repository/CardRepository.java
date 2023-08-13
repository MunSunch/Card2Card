package com.munsun.application.card2card_project.repository;

import com.munsun.application.card2card_project.exception.CardNotFoundException;
import com.munsun.application.card2card_project.model.Card;

import java.util.Optional;

public interface CardRepository extends CrudRepository<Card>{
    Optional<Card> findCardByNumberAndValidTillAndCvv(String number, String validTill, String cvv);
    Optional<Card> findCardByNumber(String number);
    void setValueCard(String cardNumber, long valueFromAfterTransfer) throws CardNotFoundException;
}
