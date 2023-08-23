package com.munsun.card2card_project.application.service;

import com.munsun.card2card_project.application.dto.in.*;
import com.munsun.card2card_project.application.dto.out.CardDtoOut;
import com.munsun.card2card_project.application.exception.CardNotFoundException;
import com.munsun.card2card_project.application.exception.ErrorTransferCurrencyException;
import com.munsun.card2card_project.application.exception.InvalidConfirmException;
import com.munsun.card2card_project.application.exception.NegativeBalanceAfterTransfer;
import com.munsun.card2card_project.application.service.impl.CardServiceImpl;
import com.munsun.card2card_project.application.service.impl.TransferServiceImpl;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class TransferServiceImplIntegrationTests {
    @Autowired
    private TransferServiceImpl transferService;
    @Autowired
    private CardServiceImpl cardService;

    private CardDtoOut testCardFrom;
    private CardDtoOut testCardTo;
    private TransferInfoDtoIn testTransferInfoDtoIn;

    @BeforeEach
    public void setTestCards() {
        testCardFrom = cardService.add(new CardDtoIn("RUR"));
        testCardTo = cardService.add(new CardDtoIn("RUR"));
        testTransferInfoDtoIn = new TransferInfoDtoIn(
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
    }

    @Test
    public void sendMoney_NegativeBalance() throws Exception {
        assertThrowsExactly(NegativeBalanceAfterTransfer.class, ()-> {
            transferService.send(testTransferInfoDtoIn);
        });
    }

    @Test
    public void sendMoneyWithoutConfirm() throws Exception {
        cardService.upBalance(new CardBalanceDtoIn(testCardFrom.cardNumber(), 1000L));
        cardService.upBalance(new CardBalanceDtoIn(testCardTo.cardNumber(), 1000L));

        var idOperation = transferService.send(testTransferInfoDtoIn)
                .operationId();
        var actual = transferService.findByIdOperation(idOperation);

        assertAll(()->{
            assertEquals(testCardFrom.cardNumber(), actual.cardFromNumber());
            assertEquals(testCardTo.cardNumber(), actual.cardToNumber());
            assertEquals("RUR", actual.amountDtoOut().currency());
            assertEquals(10000L, actual.amountDtoOut().value());
            assertFalse(actual.status());

            assertEquals(1000L, cardService.findCardByNumber(testCardFrom.cardNumber()).value());
            assertEquals(1000L, cardService.findCardByNumber(testCardTo.cardNumber()).value());
        });
    }

    @Test
    public void sendMoneyWithoutConfirm_CardFromNotFound() throws Exception {
        cardService.upBalance(new CardBalanceDtoIn(testCardFrom.cardNumber(), 1000L));
        cardService.upBalance(new CardBalanceDtoIn(testCardTo.cardNumber(), 1000L));
        var testTransferUnknownCardFrom = new TransferInfoDtoIn(
                "1111222233334444",
                "10/27",
                "444",
                testCardTo.cardNumber(),
                new AmountDtoIn(100L, "RUR")
        );

        assertThrowsExactly(CardNotFoundException.class, ()-> {
            transferService.send(testTransferUnknownCardFrom);
        });
    }

    @Test
    public void sendMoneyWithoutConfirm_ErrorCurrency() throws Exception {
        cardService.upBalance(new CardBalanceDtoIn(testCardFrom.cardNumber(), 1000L));
        cardService.upBalance(new CardBalanceDtoIn(testCardTo.cardNumber(), 1000L));
        var testTransferUnknownCardFrom = new TransferInfoDtoIn(
                testCardFrom.cardNumber(),
                testCardFrom.cardValidTill(),
                testCardFrom.cardCVV(),
                testCardTo.cardNumber(),
                new AmountDtoIn(100L, "UAU")
        );

        assertThrowsExactly(ErrorTransferCurrencyException.class, ()-> {
            transferService.send(testTransferUnknownCardFrom);
        });
    }

    @Test
    public void sendMoneyWithConfirm() throws Exception {
        cardService.upBalance(new CardBalanceDtoIn(testCardFrom.cardNumber(), 1000L));
        cardService.upBalance(new CardBalanceDtoIn(testCardTo.cardNumber(), 1000L));

        long id = transferService.send(testTransferInfoDtoIn).operationId();
        var testConfirm = new ConfirmTransferDtoIn(String.valueOf(id), "0000");
        var actualSuccess = transferService.confirm(testConfirm);

        var actualTransfer = transferService.findByIdOperation(actualSuccess.operationId());

        assertTrue(actualTransfer.status());
        assertAll(()->{
            assertEquals(900L, cardService.findCardByNumber(testCardFrom.cardNumber()).value());
            assertEquals(1100L, cardService.findCardByNumber(testCardTo.cardNumber()).value());
        });
    }

    @Test
    public void sendMoneyWithConfirm_InvalidCodeConfirm() throws Exception {
        cardService.upBalance(new CardBalanceDtoIn(testCardFrom.cardNumber(), 1000L));
        cardService.upBalance(new CardBalanceDtoIn(testCardTo.cardNumber(), 1000L));

        long id = transferService.send(testTransferInfoDtoIn).operationId();
        var testConfirm = new ConfirmTransferDtoIn(String.valueOf(id), "1");

        assertThrowsExactly(InvalidConfirmException.class, ()-> {
            var actualSuccess = transferService.confirm(testConfirm);
        });
    }
}
