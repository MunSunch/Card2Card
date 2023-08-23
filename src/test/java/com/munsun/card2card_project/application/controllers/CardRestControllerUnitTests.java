package com.munsun.card2card_project.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.munsun.card2card_project.application.dto.in.CardBalanceDtoIn;
import com.munsun.card2card_project.application.dto.in.CardDtoIn;
import com.munsun.card2card_project.application.dto.out.FailedTransferDtoOut;
import com.munsun.card2card_project.application.service.CardService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CardRestControllerUnitTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CardService service;
    @Autowired
    private ObjectMapper mapper;

    @ParameterizedTest
    @ValueSource(strings = {"", "wqfwefe", "rur", "123"})
    public void saveNewCardWithInvalidCurrency(String currency) throws Exception {
        CardDtoIn dto = new CardDtoIn(currency);
        FailedTransferDtoOut expected = new FailedTransferDtoOut(
                "Валюта должна быть длиной 3 символа и указана строчными буквами",
                0L
        );

        mockMvc
                .perform(post("/cards/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "wsfwf", "12123434"})
    public void upBalanceCardWithInvalidCardNumber(String cardNumber) throws Exception {
        var dto = new CardBalanceDtoIn(
                cardNumber,
                100L
        );
        var expected = new FailedTransferDtoOut(
                "Номер карты должен быть длиной 16 символов и состоять из цифр 0-9",
                0L
        );

        mockMvc
                .perform(post("/cards/balance/up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }

    @ParameterizedTest
    @ValueSource(longs = {-100L, 0L, 5L})
    public void upBalanceCardWithInvalidValue(long value) throws Exception {
        var dto = new CardBalanceDtoIn(
                "1111222233334444",
                value
        );
        var expected = new FailedTransferDtoOut(
                "Минимальное значение для перевода составляет 10",
                0L
        );

        mockMvc
                .perform(post("/cards/balance/up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }
}
