package com.munsun.card2card_project.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.munsun.card2card_project.application.dto.in.AmountDtoIn;
import com.munsun.card2card_project.application.dto.in.ConfirmTransferDtoIn;
import com.munsun.card2card_project.application.dto.in.TransferInfoDtoIn;
import com.munsun.card2card_project.application.dto.out.FailedTransferDtoOut;
import com.munsun.card2card_project.application.service.TransferService;
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
public class TransferRestControllerUnitTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private TransferService service;

    @ParameterizedTest
    @ValueSource(strings = {"", "21632gjvqjd63", "111122223333444"})
    public void validationTransferDtoInvalidCardFrom(String numberCardFrom) throws Exception {
        TransferInfoDtoIn dto = new TransferInfoDtoIn(
                numberCardFrom,
                "08/28",
                "777",
                "5555666677778888",
                new AmountDtoIn(100L, "RUR")
        );
        FailedTransferDtoOut expected = new FailedTransferDtoOut(
                "Номер карты должен быть длиной 16 символов и состоять из цифр 0-9",
            0L);

        mockMvc
                .perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "8/28", "dwfwqfrw2/123d"})
    public void validationTransferDtoInvalidCardFromValidTill(String validTill) throws Exception {
        TransferInfoDtoIn dto = new TransferInfoDtoIn(
        "1111222233334444",
                        validTill,
                "777",
                "5555666677778888",
                new AmountDtoIn(100L, "RUR")
        );

        FailedTransferDtoOut expected = new FailedTransferDtoOut(
                "Срок действия карты должен быть указан форматом: месяц/год",
                0L
        );

        mockMvc
                .perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "5551", "sdks12"})
    public void validationTransferDtoInvalidCardFromCVV(String cvv) throws Exception {
        TransferInfoDtoIn dto = new TransferInfoDtoIn(
                "1111222233334444",
                "08/28",
                cvv,
                "5555666677778888",
                new AmountDtoIn(100L, "RUR")
        );

        FailedTransferDtoOut expected = new FailedTransferDtoOut(
                "CVV карты - это трехзначное число",
                0L
        );

        mockMvc
                .perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "sqwfwf", "111122223333444"})
    public void validationTransferDtoInvalidCardTo(String numberCardTo) throws Exception {
        TransferInfoDtoIn dto = new TransferInfoDtoIn(
                "1111222233334444",
                "08/28",
                "777",
                numberCardTo,
                new AmountDtoIn(100L, "RUR")
        );

        FailedTransferDtoOut expected = new FailedTransferDtoOut(
                "Номер карты должен быть длиной 16 символов и состоять из цифр 0-9",
                0L
        );

        mockMvc
                .perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "RUBLE", "123",})
    public void validationTransferDtoInvalidCurrency(String currency) throws Exception {
        TransferInfoDtoIn dto = new TransferInfoDtoIn(
                "1111222233334444",
                "08/28",
                "777",
                "5555666677778888",
                new AmountDtoIn(100L, currency)
        );

        FailedTransferDtoOut expected = new FailedTransferDtoOut(
                "Валюта должна быть длиной 3 символа и указана строчными буквами",
                0L
        );

        mockMvc
                .perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }

    @ParameterizedTest
    @ValueSource(longs = {-100L, 0L, 9L})
    public void validationTransferDtoInvalidValue(long value) throws Exception {
        TransferInfoDtoIn dto = new TransferInfoDtoIn(
                "1111222233334444",
                "08/28",
                "777",
                "5555666677778888",
                new AmountDtoIn(value, "RUR")
        );

        FailedTransferDtoOut expected = new FailedTransferDtoOut(
                "Минимальное значение для перевода составляет 10",
                      0L
        );

        mockMvc
                .perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "12xf", "-100"})
    public void validationConfirmDtoInvalidOperationId(String operationId) throws Exception {
        ConfirmTransferDtoIn dto = new ConfirmTransferDtoIn(
            operationId,
            "0000"
        );

        FailedTransferDtoOut expected = new FailedTransferDtoOut(
                "Id операции должно быть положительным числом",
                0L
        );

        mockMvc
                .perform(post("/confirmOperation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "12xf", "-100", "123"})
    public void validationConfirmDtoInvalidCode(String code) throws Exception {
        ConfirmTransferDtoIn dto = new ConfirmTransferDtoIn("1", code);
        FailedTransferDtoOut expected = new FailedTransferDtoOut(
                "Код подтверждения не четырехзначное число",
                0L
        );

        mockMvc
                .perform(post("/confirmOperation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }
}