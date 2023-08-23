package com.munsun.card2card_project.application.repository.impl;

import com.munsun.card2card_project.application.exception.CardNotFoundException;
import com.munsun.card2card_project.application.model.Card;
import com.munsun.card2card_project.application.repository.CardRepository;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Log4j2
@Repository
@AllArgsConstructor
@Setter
public class CardRepositoryImpl implements CardRepository {
    private final ConcurrentHashMap<Long, Card> cards;
    private final AtomicLong generatorId;
    private final ConcurrentHashMap<String, Card> cardsIndexByNumberCard;

    @Autowired
    public CardRepositoryImpl() {
        this.cardsIndexByNumberCard = new ConcurrentHashMap<>();
        this.cards = new ConcurrentHashMap<>();
        this.generatorId = new AtomicLong();
    }

    @Override
    public Optional<Card> findCardByNumberAndValidTillAndCvv(String number, String validTill, String cvv) {
        var card = findCardByNumber(number);
        if(card.isPresent()) {
            var temp = card.get();
            if(!temp.getCardValidTill().equals(validTill) || !temp.getCardCVV().equals(cvv)) {
                return Optional.empty();
            }
            return card;
        }
        return card;
    }

    @Override
    public Optional<Card> findCardByNumber(String number) {
        return Optional.ofNullable(cardsIndexByNumberCard.get(number));
    }

    @Override
    public Card add(Card newObj) {
        Card temp = new Card(newObj);
            temp.setIsActive(true);
        cardsIndexByNumberCard.put(temp.getCardNumber(), temp);
        cards.put(generatorId.incrementAndGet(), temp);
        return temp;
    }

    public List<Card> getAll() {
        return new ArrayList<>(cards.values());
    }

    @Override
    public void setValueCard(String cardNumber, long valueFromAfterTransfer) throws CardNotFoundException {
        findCardByNumber(cardNumber).orElseThrow(CardNotFoundException::new)
                .setValue(valueFromAfterTransfer);
    }

    @Override
    public void clear() {
        cards.clear();
        cardsIndexByNumberCard.clear();
        generatorId.set(0L);
    }
}
