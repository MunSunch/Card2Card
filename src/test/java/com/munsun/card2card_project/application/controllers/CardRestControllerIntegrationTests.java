package com.munsun.card2card_project.application.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.munsun.card2card_project.application.dto.in.CardBalanceDtoIn;
import com.munsun.card2card_project.application.dto.in.CardDtoIn;
import com.munsun.card2card_project.application.dto.out.CardDtoOut;
import com.munsun.card2card_project.application.dto.out.FailedTransferDtoOut;
import com.munsun.card2card_project.application.dto.out.SuccessTransferDtoOut;
import com.munsun.card2card_project.application.service.TransferService;
import com.munsun.card2card_project.application.service.impl.CardServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CardRestControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private CardServiceImpl service;
    @Autowired
    private Gson gson;

    private CardDtoOut testCard;

    @AfterEach
    public void clearCardService() {
        service.clear();
    }

    @BeforeEach
    public void addCard() throws Exception {
        var cardDtoIn = new CardDtoIn("RUR");

        var result = mockMvc.perform(post("/cards/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardDtoIn)))
                .andExpect(status().isOk())
                .andReturn();

        testCard = gson.fromJson(result.getResponse().getContentAsString(), CardDtoOut.class);
    }

    @Test
    public void addNewCard200() throws Exception {
        assertAll(()->{
            assertTrue(testCard.cardNumber().matches("^\\d{16}$"));
            assertTrue(testCard.cardCVV().matches("^\\d{3}$"));
            assertTrue(testCard.cardValidTill().matches("^(0[1-9]|[10-12])/[2-3][3-9]$"));
            assertEquals("RUR", testCard.currency());
            assertEquals(0L, testCard.value());
            assertTrue(testCard.isActive());
        });
    }

    @Test
    public void addNewCard_InvalidDto400() throws Exception {
        var cardDtoIn = new CardDtoIn("rur");

        var result = mockMvc.perform(post("/cards/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardDtoIn)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void upBalance200() throws Exception {
        var cardBalanceDtoIn = new CardBalanceDtoIn(testCard.cardNumber(), 1000L);
        var result = mockMvc
                .perform(post("/cards/balance/up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardBalanceDtoIn)))
                .andExpect(status().isOk())
                .andReturn();

        var actualCard = gson.fromJson(result.getResponse().getContentAsString(),
                CardDtoOut.class);

        assertEquals(1000L, actualCard.value());
    }

    @Test
    public void upBalance_CardNotFound500() throws Exception {
        var cardBalanceDtoIn = new CardBalanceDtoIn("1111222233334444", 1000L);
        var expected = new FailedTransferDtoOut("Карта или карты не найдены", 0L);

        var result = mockMvc
                .perform(post("/cards/balance/up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardBalanceDtoIn)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(gson.toJson(expected)));
    }

    @Test
    public void getCards200() throws Exception {
        var result = mockMvc
                .perform(get("/cards/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        var cards = gson.fromJson(result.getResponse().getContentAsString(), new TypeToken<List<CardDtoOut>>() {});

        assertEquals(1, cards.size());
    }

    @Test
    public void getByNumber() throws Exception {
        var result = mockMvc
                .perform(get("/cards/getByNumber/"+testCard.cardNumber())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        var actual = gson.fromJson(result.getResponse().getContentAsString(), CardDtoOut.class);

        assertAll(()->{
            assertTrue(actual.cardNumber().matches("^\\d{16}$"));
            assertTrue(actual.cardCVV().matches("^\\d{3}$"));
            assertTrue(actual.cardValidTill().matches("^(0[1-9]|[10-12])/[2-3][3-9]$"));
            assertEquals("RUR", actual.currency());
            assertEquals(0L, actual.value());
            assertTrue(actual.isActive());
        });
    }

    @Test
    public void getByNumber_CardNotFound500() throws Exception {
        var expected = new FailedTransferDtoOut("Карта или карты не найдены", 0L);

        mockMvc
                .perform(get("/cards/getByNumber/"+"1111222233334444")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }
}
