package com.munsun.card2card_project.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.munsun.card2card_project.application.dto.in.*;
import com.munsun.card2card_project.application.dto.out.CardDtoOut;
import com.munsun.card2card_project.application.dto.out.FailedTransferDtoOut;
import com.munsun.card2card_project.application.dto.out.SuccessTransferDtoOut;
import com.munsun.card2card_project.application.dto.out.TransferInfoDtoOut;
import com.munsun.card2card_project.application.exception.ErrorTransferCurrencyException;
import com.munsun.card2card_project.application.exception.InvalidConfirmException;
import com.munsun.card2card_project.application.exception.NegativeBalanceAfterTransfer;
import com.munsun.card2card_project.application.service.impl.CardServiceImpl;
import com.munsun.card2card_project.application.service.impl.TransferServiceImpl;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransferRestControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TransferServiceImpl transferService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private Gson gson;
    @Autowired
    private CardServiceImpl cardService;

    private CardDtoOut testCardFrom;
    private CardDtoOut testCardTo;
    private TransferInfoDtoIn testTransferDtoIn;

    @BeforeEach
    public void setTestCards() throws Exception {
        testCardFrom = cardService.add(new CardDtoIn("RUR"));
        cardService.upBalance(new CardBalanceDtoIn(testCardFrom.cardNumber(), 1000L));
        testCardTo = cardService.add(new CardDtoIn("RUR"));
        cardService.upBalance(new CardBalanceDtoIn(testCardTo.cardNumber(), 1000L));

        testTransferDtoIn = new TransferInfoDtoIn(
                testCardFrom.cardNumber(),
                testCardFrom.cardValidTill(),
                testCardFrom.cardCVV(),
                testCardTo.cardNumber(),
                new AmountDtoIn(10000L, "RUR")
        );
    }

    @AfterEach
    public void clearRepository() {
        cardService.clear();
        transferService.clear();
    }

    @Test
    public void sendMoneyWithoutConfirm() throws Exception {
        var result = mockMvc
                .perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testTransferDtoIn)))
                .andExpect(status().isOk())
                .andReturn();
        var idOperation = gson.fromJson(result.getResponse().getContentAsString(), SuccessTransferDtoOut.class)
                .operationId();

        result = mockMvc
                .perform(get("/transfers/get/"+idOperation)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        var transfer = gson.fromJson(result.getResponse().getContentAsString(), TransferInfoDtoOut.class);

        assertAll(()->{
            assertEquals(testCardFrom.cardNumber(), transfer.cardFromNumber());
            assertEquals(testCardTo.cardNumber(), transfer.cardToNumber());
            assertEquals("RUR", transfer.amountDtoOut().currency());
            assertEquals(10000L, transfer.amountDtoOut().value());
            assertFalse(transfer.status());

            assertEquals(1000L, cardService.findCardByNumber(testCardFrom.cardNumber()).value());
            assertEquals(1000L, cardService.findCardByNumber(testCardTo.cardNumber()).value());
        });
    }

    @Test
    public void sendMoneyWithoutConfirm_NegativeBalanceAfterTransfer500() throws Exception {
        testTransferDtoIn = new TransferInfoDtoIn(
                testTransferDtoIn.cardFromNumber(),
                testTransferDtoIn.cardFromValidTill(),
                testTransferDtoIn.cardFromCVV(),
                testTransferDtoIn.cardToNumber(),
                new AmountDtoIn(1000000L, "RUR")
        );

        var expected = new FailedTransferDtoOut("Недостаточно средств на карте!", 1L);
        var result = mockMvc
                .perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testTransferDtoIn)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(gson.toJson(expected)));
    }

    @Test
    public void sendMoneyWithoutConfirm_CardFromNotFound500() throws Exception {
        testTransferDtoIn = new TransferInfoDtoIn(
                "1111222233334444",
                "03/25",
                "555",
                testTransferDtoIn.cardToNumber(),
                new AmountDtoIn(1000L, "RUR")
        );

        var expected = new FailedTransferDtoOut("Карта или карты не найдены", 0L);
        var result = mockMvc
                .perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testTransferDtoIn)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(gson.toJson(expected)));
    }

    @Test
    public void sendMoneyWithoutConfirm_CardToNotFound500() throws Exception {
        testTransferDtoIn = new TransferInfoDtoIn(
                testTransferDtoIn.cardFromNumber(),
                testTransferDtoIn.cardFromValidTill(),
                testTransferDtoIn.cardFromCVV(),
                "1111222233334444",
                new AmountDtoIn(1000L, "RUR")
        );

        var expected = new FailedTransferDtoOut("Карта или карты не найдены", 0L);
        var result = mockMvc
                .perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testTransferDtoIn)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(gson.toJson(expected)));
    }

    @Test
    public void sendMoneyWithoutConfirm_ErrorCurrency() throws Exception {
        testTransferDtoIn = new TransferInfoDtoIn(
                testCardFrom.cardNumber(),
                testCardFrom.cardValidTill(),
                testCardFrom.cardCVV(),
                testCardTo.cardNumber(),
                new AmountDtoIn(100L, "UAU")
        );

        var expected = new FailedTransferDtoOut("Неверная валюта перевода, или карта не поддерживает переводы в данной валюте", 1L);
        var result = mockMvc
                .perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testTransferDtoIn)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(gson.toJson(expected)));
    }

    @Test
    public void sendMoneyWithConfirm() throws Exception {
        var result = mockMvc
                .perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testTransferDtoIn)))
                .andExpect(status().isOk())
                .andReturn();
        String idOperation = String.valueOf(gson.fromJson(result.getResponse().getContentAsString(), TransferInfoDtoOut.class)
                .operationId());

        var confirm = new ConfirmTransferDtoIn(idOperation, "0000");
        result = mockMvc
                .perform(post("/confirmOperation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(confirm)))
                .andExpect(status().isOk())
                .andReturn();
        var expected = new SuccessTransferDtoOut(1L);
        assertEquals(expected, gson.fromJson(result.getResponse().getContentAsString(), SuccessTransferDtoOut.class));

        result = mockMvc
                .perform(get("/transfers/get/"+idOperation)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        var transfer = gson.fromJson(result.getResponse().getContentAsString(), TransferInfoDtoOut.class);

        assertTrue(transfer.status());
        assertAll(()->{
            assertEquals(900L, cardService.findCardByNumber(testCardFrom.cardNumber()).value());
            assertEquals(1100L, cardService.findCardByNumber(testCardTo.cardNumber()).value());
        });
    }

    @Test
    public void sendMoneyWithConfirm_InvalidCodeConfirm500() throws Exception {
        var result = mockMvc
                .perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testTransferDtoIn)))
                .andExpect(status().isOk())
                .andReturn();
        String idOperation = String.valueOf(gson.fromJson(result.getResponse().getContentAsString(), TransferInfoDtoOut.class)
                .operationId());

        var expected = new FailedTransferDtoOut("Неверный код", 1L);
        mockMvc
                .perform(post("/confirmOperation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ConfirmTransferDtoIn(idOperation, "1111"))))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(gson.toJson(expected)));
    }
}