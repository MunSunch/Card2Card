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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Log4j2
@Repository
public class CardRepositoryImpl implements CardRepository {
    private final ConcurrentHashMap<Long, Card> cards;
    private final AtomicLong generatorId;
    private final ConcurrentHashMap<String, Card> cardsIndexByNumberCard;
    private final Gson gson;
    @Value("${cards.file.location}")
    private String pathFile;

    @Autowired
    public CardRepositoryImpl(Gson gson) {
        this.gson = gson;
        this.cardsIndexByNumberCard = new ConcurrentHashMap<>();
        this.cards = new ConcurrentHashMap<>();
        this.generatorId = new AtomicLong();
    }

    @PostConstruct
    public void postConstruct() throws IOException {
        Path path = Path.of(pathFile);
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
        Path path = Path.of(pathFile);
        String out = gson.toJson(cards.values());
        Files.writeString(path, out);
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
    public Optional<Card> get(Long id) {
        return Optional.of(cards.get(id));
    }

    public List<Card> getAll() {
        return new ArrayList<>(cards.values());
    }

    @Override
    public void setValueCard(String cardNumber, long valueFromAfterTransfer) throws CardNotFoundException {
        findCardByNumber(cardNumber).orElseThrow(CardNotFoundException::new)
                .setValue(valueFromAfterTransfer);
    }
}
