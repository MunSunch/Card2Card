package com.munsun.card2card_project.application.controllers;

import com.munsun.card2card_project.application.dto.in.CardBalanceDtoIn;
import com.munsun.card2card_project.application.dto.in.CardDtoIn;
import com.munsun.card2card_project.application.dto.out.CardDtoOut;
import com.munsun.card2card_project.application.exception.CardNotFoundException;
import com.munsun.card2card_project.application.service.CardService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Hidden
@RequestMapping("/cards")
@Validated
public class CardRestController {
    private final CardService service;

    @Autowired
    public CardRestController(CardService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public CardDtoOut addCard(@RequestBody @Valid CardDtoIn cardDtoIn) {
        return service.add(cardDtoIn);
    }

    @PostMapping("/balance/up")
    public CardDtoOut upBalanceCard(@RequestBody @Valid CardBalanceDtoIn cardBalanceDtoIn) throws CardNotFoundException {
        return service.upBalance(cardBalanceDtoIn);
    }

    @GetMapping("/getByNumber/{number}")
    public CardDtoOut getByNumber(@PathVariable @NotBlank String number) throws CardNotFoundException {
        return service.findCardByNumber(number);
    }

    @GetMapping("/all")
    public List<CardDtoOut> getAllCards() {
        return service.getCards();
    }
}
