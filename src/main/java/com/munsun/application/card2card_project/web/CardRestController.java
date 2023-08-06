package com.munsun.application.card2card_project.web;

import com.munsun.application.card2card_project.dto.in.CardBalanceDtoIn;
import com.munsun.application.card2card_project.dto.in.CardDtoIn;
import com.munsun.application.card2card_project.dto.out.CardDtoOut;
import com.munsun.application.card2card_project.exception.CardNotFoundException;
import com.munsun.application.card2card_project.service.CardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardRestController {
    private final CardService service;

    @Autowired
    public CardRestController(CardService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public CardDtoOut addCard(@RequestBody CardDtoIn cardDtoIn) {
        return service.add(cardDtoIn);
    }

    @PutMapping("/balance/up")
    public CardDtoOut upBalanceCard(@RequestBody @Valid CardBalanceDtoIn cardBalanceDtoIn) throws CardNotFoundException {
        return service.upBalance(cardBalanceDtoIn);
    }

    @GetMapping("/all")
    public List<CardDtoOut> getAllCards() {
        return service.getCards();
    }
}
