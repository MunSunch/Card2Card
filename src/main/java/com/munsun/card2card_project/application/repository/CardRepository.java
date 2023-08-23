package com.munsun.card2card_project.application.repository;

import com.munsun.card2card_project.application.exception.CardNotFoundException;
import com.munsun.card2card_project.application.model.Card;

import java.util.Optional;

public interface CardRepository extends CrudRepository<Card>{
    Optional<Card> findCardByNumberAndValidTillAndCvv(String number, String validTill, String cvv);
    Optional<Card> findCardByNumber(String number);
    void setValueCard(String cardNumber, long valueFromAfterTransfer) throws CardNotFoundException;
}
