package com.munsun.application.card2card_project.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.munsun.application.card2card_project.dto.in.AmountDtoIn;
import com.munsun.application.card2card_project.dto.in.ConfirmTransferDtoIn;
import com.munsun.application.card2card_project.dto.in.TransferInfoDtoIn;
import com.munsun.application.card2card_project.dto.out.FailedTransferDtoOut;
import com.munsun.application.card2card_project.model.TransferInfo;
import com.munsun.application.card2card_project.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

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
        TransferInfoDtoIn dto = new TransferInfoDtoIn();
            dto.setCardFromNumber(numberCardFrom);
            dto.setCardFromValidTill("08/28");
            dto.setCardFromCVV("777");
            dto.setCardToNumber("5555666677778888");
            dto.setAmountDtoIn(new AmountDtoIn(100L, "RUR"));

        FailedTransferDtoOut expected = new FailedTransferDtoOut();
            expected.setId(0L);
            expected.setMessage("Номер карты должен быть длиной 16 символов и состоять из цифр 0-9");

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
        TransferInfoDtoIn dto = new TransferInfoDtoIn();
        dto.setCardFromNumber("1111222233334444");
        dto.setCardFromValidTill(validTill);
        dto.setCardFromCVV("777");
        dto.setCardToNumber("5555666677778888");
        dto.setAmountDtoIn(new AmountDtoIn(100L, "RUR"));

        FailedTransferDtoOut expected = new FailedTransferDtoOut();
        expected.setId(0L);
        expected.setMessage("Срок действия карты должен быть указан форматом: месяц/год");

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
        TransferInfoDtoIn dto = new TransferInfoDtoIn();
        dto.setCardFromNumber("1111222233334444");
        dto.setCardFromValidTill("08/28");
        dto.setCardFromCVV(cvv);
        dto.setCardToNumber("5555666677778888");
        dto.setAmountDtoIn(new AmountDtoIn(100L, "RUR"));

        FailedTransferDtoOut expected = new FailedTransferDtoOut();
        expected.setId(0L);
        expected.setMessage("CVV карты - это трехзначное число");

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
        TransferInfoDtoIn dto = new TransferInfoDtoIn();
        dto.setCardFromNumber("1111222233334444");
        dto.setCardFromValidTill("08/28");
        dto.setCardFromCVV("777");
        dto.setCardToNumber(numberCardTo);
        dto.setAmountDtoIn(new AmountDtoIn(100L, "RUR"));

        FailedTransferDtoOut expected = new FailedTransferDtoOut();
        expected.setId(0L);
        expected.setMessage("Номер карты должен быть длиной 16 символов и состоять из цифр 0-9");

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
        TransferInfoDtoIn dto = new TransferInfoDtoIn();
        dto.setCardFromNumber("1111222233334444");
        dto.setCardFromValidTill("08/28");
        dto.setCardFromCVV("777");
        dto.setCardToNumber("5555666677778888");
        dto.setAmountDtoIn(new AmountDtoIn(100L, currency));

        FailedTransferDtoOut expected = new FailedTransferDtoOut();
        expected.setId(0L);
        expected.setMessage("Валюта должна быть длиной 3 символа и указана строчными буквами");

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
        TransferInfoDtoIn dto = new TransferInfoDtoIn();
        dto.setCardFromNumber("1111222233334444");
        dto.setCardFromValidTill("08/28");
        dto.setCardFromCVV("777");
        dto.setCardToNumber("5555666677778888");
        dto.setAmountDtoIn(new AmountDtoIn(value, "RUR"));

        FailedTransferDtoOut expected = new FailedTransferDtoOut();
        expected.setId(0L);
        expected.setMessage("Минимальное значение для перевода составляет 10");

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
        ConfirmTransferDtoIn dto = new ConfirmTransferDtoIn();
            dto.setOperationId(operationId);
            dto.setCode("0000");

        FailedTransferDtoOut expected = new FailedTransferDtoOut();
        expected.setId(0L);
        expected.setMessage("Id операции должно быть положительным числом");

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
        ConfirmTransferDtoIn dto = new ConfirmTransferDtoIn();
            dto.setOperationId("1");
            dto.setCode(code);

        FailedTransferDtoOut expected = new FailedTransferDtoOut();
            expected.setId(0L);
            expected.setMessage("Код подтверждения не четырехзначное число");

        mockMvc
                .perform(post("/confirmOperation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }
}