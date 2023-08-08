package com.munsun.application.card2card_project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.munsun.application.card2card_project.dto.in.CardDtoIn;
import com.munsun.application.card2card_project.dto.out.CardDtoOut;
import com.munsun.application.card2card_project.dto.out.FailedTransferDtoOut;
import com.munsun.application.card2card_project.service.CardService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private CardService service;

    @Test
    public void deserializationJson2CardDtoIn() throws Exception {
        String json = "{\"currency\": \"RUR\"}";
        CardDtoIn expected = new CardDtoIn("RUR");
        CardDtoOut cardDtoOut = new CardDtoOut();
            cardDtoOut.setCardNumber("test");
            cardDtoOut.setCardValidTill("11/24");
            cardDtoOut.setCardCVV("444");
            cardDtoOut.setCurrency("RUR");
        Mockito.when(service.add(Mockito.any())).thenReturn(cardDtoOut);

        mockMvc
                .perform(post("/cards/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(cardDtoOut)));
    }

    @Test
    public void deserializationJsonEmptyCurrency() throws Exception {
        String json = "{\"currency\": \"\"}";
        FailedTransferDtoOut failedTransferDtoOut = new FailedTransferDtoOut();
            failedTransferDtoOut.setId(0L);
            failedTransferDtoOut.setMessage("Валюта не заполнена");

        mockMvc
                .perform(post("/cards/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(failedTransferDtoOut)));
    }

    @Test
    public void deserializationJson2CardBalanceDtoIn() throws Exception {
        String json = "{\"cardNumber\": \"7596830436697811\",\"money\": 1000}";
        CardDtoOut cardDtoOut = new CardDtoOut();
            cardDtoOut.setCardNumber("7596830436697811");
            cardDtoOut.setValue(1000L);
        Mockito.when(service.upBalance(Mockito.any())).thenReturn(cardDtoOut);

        mockMvc
                .perform(put("/cards/balance/up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(cardDtoOut)));
    }
}
