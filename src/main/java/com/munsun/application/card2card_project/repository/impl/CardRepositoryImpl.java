package com.munsun.application.card2card_project.repository.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.munsun.application.card2card_project.exception.CardNotFoundException;
import com.munsun.application.card2card_project.model.Card;
import com.munsun.application.card2card_project.repository.CardRepository;
import com.munsun.application.card2card_project.repository.CrudRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Log4j2
@Repository
public class CardRepositoryImpl implements CrudRepository<Card>, CardRepository {
    private final ConcurrentHashMap<Long, Card> cards;
    private final AtomicLong generatorId;
    private final ConcurrentHashMap<String, Card> cardsIndexByNumberCard;
    private final Gson gson;

    @Autowired
    public CardRepositoryImpl(Gson gson) {
        this.gson = gson;
        this.cardsIndexByNumberCard = new ConcurrentHashMap<>();
        this.cards = new ConcurrentHashMap<>();
        this.generatorId = new AtomicLong();
    }

    @PostConstruct
    public void postConstruct() throws IOException {
        Path path = Path.of("cards.json");
        String in = "";
        try {
            in = Files.readString(path);
        } catch (NoSuchFileException e) {
            Files.createFile(path);
            return;
        }
        TypeToken<List<Card>> collectionType = new TypeToken<>(){};
        var temp = gson.fromJson(in, collectionType);
        if(!ObjectUtils.isEmpty(temp)) {
            temp.forEach(x -> {
                cards.put(generatorId.incrementAndGet(), x);
                cardsIndexByNumberCard.put(x.getCardNumber(), x);
            });
        }
    }

    @PreDestroy
    public void preDestroy() throws IOException {
        Path path = Path.of("cards.json");
        String out = gson.toJson(cards.values());
        Files.writeString(Path.of("cards.json"), out);
    }

    @Override
    public boolean isActive(String number, String validTill, String cvv) {
        var temp = cardsIndexByNumberCard.get(number);
        return temp.getIsActive();
    }

    @Override
    public Optional<Card> findCardByNumberAndValidTillAndCvv(String number, String validTill, String cvv) {
        var card = findCardByNumber(number);
        if(card.isPresent()) {
            var temp = card.get();
            if(!temp.getCardValidTill().equals(validTill) || !temp.getCardCVV().equals(cvv)) {
                return Optional.empty();
            }
        }
        return card;
    }

    @Override
    public Optional<Card> findCardByNumber(String number) {
        return Optional.ofNullable(cardsIndexByNumberCard.get(number));
    }

    @Override
    public boolean isExists(String number) {
        return findCardByNumber(number).isPresent();
    }

    @Override
    public Card add(Card newObj) {
        newObj.setIsActive(true);
        cards.put(generatorId.incrementAndGet(), newObj);
        cardsIndexByNumberCard.put(newObj.getCardNumber(), newObj);
        return findCardByNumber(newObj.getCardNumber()).get();
    }

    @Override
    public Optional<Card> remove(Long id) {
        return Optional.ofNullable(cardsIndexByNumberCard.remove(cards.remove(id).getCardNumber()));
    }

    @Override
    public Optional<Card> update(Long id, Card newObj) {
        cardsIndexByNumberCard.replace(cards.get(id).getCardNumber(), newObj);
        return Optional.ofNullable(cards.replace(id, newObj));
    }

    @Override
    public void setValueCard(String number, Long value) throws CardNotFoundException {
        var card = findCardByNumber(number).orElseThrow(CardNotFoundException::new);
        card.setValue(value);
    }

    @Override
    public Optional<Card> get(Long id) {
        return Optional.of(cards.get(id));
    }

    public List<Card> getAll() {
        return new ArrayList<>(cards.values());
    }
}
