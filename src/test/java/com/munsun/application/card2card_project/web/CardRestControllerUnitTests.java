package com.munsun.application.card2card_project.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.munsun.application.card2card_project.dto.in.CardBalanceDtoIn;
import com.munsun.application.card2card_project.dto.in.CardDtoIn;
import com.munsun.application.card2card_project.dto.out.FailedTransferDtoOut;
import com.munsun.application.card2card_project.service.CardService;
import org.junit.jupiter.api.Test;
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
        CardDtoIn dto = new CardDtoIn();
            dto.setCurrency(currency);

        FailedTransferDtoOut expected = new FailedTransferDtoOut();
            expected.setId(0L);
            expected.setMessage("Валюта должна быть длиной 3 символа и указана строчными буквами");

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
        CardBalanceDtoIn dto = new CardBalanceDtoIn();
            dto.setCardNumber(cardNumber);
            dto.setValue(100L);

        FailedTransferDtoOut expected = new FailedTransferDtoOut();
            expected.setId(0L);
            expected.setMessage("Номер карты должен быть длиной 16 символов и состоять из цифр 0-9");

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
        CardBalanceDtoIn dto = new CardBalanceDtoIn();
            dto.setCardNumber("1111222233334444");
            dto.setValue(value);

        FailedTransferDtoOut expected = new FailedTransferDtoOut();
            expected.setId(0L);
            expected.setMessage("Минимальное значение для перевода составляет 10");

        mockMvc
                .perform(post("/cards/balance/up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }
}
