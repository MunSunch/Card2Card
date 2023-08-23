package com.munsun.card2card_project.application.mapping;

import com.munsun.card2card_project.application.dto.in.AmountDtoIn;
import com.munsun.card2card_project.application.dto.in.ConfirmTransferDtoIn;
import com.munsun.card2card_project.application.dto.in.TransferInfoDtoIn;
import com.munsun.card2card_project.application.dto.out.AmountDtoOut;
import com.munsun.card2card_project.application.dto.out.TransferInfoDtoOut;
import com.munsun.card2card_project.application.exception.CardNotFoundException;
import com.munsun.card2card_project.application.exception.TransferNotFoundException;
import com.munsun.card2card_project.application.model.Card;
import com.munsun.card2card_project.application.model.TransferInfo;
import com.munsun.card2card_project.application.repository.CardRepository;
import com.munsun.card2card_project.application.repository.TransferRepository;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

@SpringBootTest
public class TransferMapperImplUnitTests {
    @MockBean
    private CardRepository cardRepository;
    @MockBean
    private TransferRepository transferRepository;
    @Autowired
    @InjectMocks
    private TransferMapperImpl mapper;

    private Card testCardFrom;
    private Card testCardTo;
    private TransferInfo testTransfer;
    private static final String CODE_CONFIRM = "0";

    @BeforeEach
    public void setTestCards() {
        testCardFrom = new Card();
            testCardFrom.setCardNumber("1111222233334444");
            testCardFrom.setCardValidTill("08/30");
            testCardFrom.setCardCVV("999");
            testCardFrom.setCurrency("RUR");
            testCardFrom.setValue(1000L);

        testCardTo = new Card();
            testCardTo.setCardNumber("5555666677778888");
            testCardTo.setCardValidTill("08/31");
            testCardTo.setCardCVV("777");
            testCardTo.setCurrency("RUR");
            testCardTo.setValue(1000L);

        testTransfer = new TransferInfo();
            testTransfer.setCardTo(testCardTo);
            testTransfer.setCardFrom(testCardFrom);
            testTransfer.setCurrency(testCardFrom.getCurrency());
            testTransfer.setValue(500L);
            testTransfer.setOperationId(1L);
    }

    @Test
    public void transferDtoIn2TransferInfo() throws CardNotFoundException {
        var transferDtoIn = new TransferInfoDtoIn(
                testCardFrom.getCardNumber(),
                testCardFrom.getCardValidTill(),
                testCardFrom.getCardCVV(),
                testCardTo.getCardNumber(),
                new AmountDtoIn(500L, "RUR")
        );

        Mockito.doReturn(Optional.of(testCardFrom)).when(cardRepository)
                        .findCardByNumberAndValidTillAndCvv(Mockito.anyString(), Mockito.anyString(),Mockito.anyString());
        Mockito.doReturn(Optional.of(testCardTo)).when(cardRepository)
                        .findCardByNumber(Mockito.anyString());
        var actual = mapper.map(transferDtoIn);

        assertAll(()->{
            assertNull(actual.getOperationId());
            assertEquals(testTransfer.getCardFrom(), actual.getCardFrom());
            assertEquals(testTransfer.getCardTo(), actual.getCardTo());
            assertEquals(testTransfer.getCurrency(), actual.getCurrency());
            assertEquals(testTransfer.getValue(), actual.getValue());
        });
    }

    @Test
    public void transferDtoIn2TransferInfo_NotExistsCardFrom() throws CardNotFoundException {
        var transferDtoIn = new TransferInfoDtoIn(
                "0000000000000000",
                "10/30",
                "777",
                testCardTo.getCardNumber(),
                new AmountDtoIn(500L, "RUR")
        );

        Mockito.doReturn(Optional.empty()).when(cardRepository)
                .findCardByNumberAndValidTillAndCvv(Mockito.anyString(), Mockito.anyString(),Mockito.anyString());

        assertThrowsExactly(CardNotFoundException.class, ()-> {
            var actual = mapper.map(transferDtoIn);
        });
    }

    @Test
    public void transferDtoIn2TransferInfo_NotExistsCardTo() throws CardNotFoundException {
        var transferDtoIn = new TransferInfoDtoIn(
                testCardFrom.getCardNumber(),
                testCardFrom.getCardValidTill(),
                testCardFrom.getCardCVV(),
                "0000000000000000",
                new AmountDtoIn(500L, "RUR")
        );

        Mockito.doReturn(Optional.empty()).when(cardRepository)
                .findCardByNumber(Mockito.anyString());

        assertThrowsExactly(CardNotFoundException.class, ()-> {
            var actual = mapper.map(transferDtoIn);
        });
    }

    @Test
    public void confirmTransferDtoIn2ConfirmTransfer() throws TransferNotFoundException {
        var confirmTransferDtoIn = new ConfirmTransferDtoIn(
                testTransfer.getOperationId().toString(),
                CODE_CONFIRM
        );

        Mockito.doReturn(Optional.of(testTransfer)).when(transferRepository)
                .findByIdOperation(Mockito.anyLong());
        var actual = mapper.map(confirmTransferDtoIn);

        assertAll(()->{
            assertEquals(CODE_CONFIRM, actual.getCodeConfirm().toString());
            assertEquals(testTransfer.getCardFrom(), actual.getTransferInfo().getCardFrom());
            assertEquals(testTransfer.getCardTo(), actual.getTransferInfo().getCardTo());
            assertEquals(testTransfer.getCurrency(), actual.getTransferInfo().getCurrency());
            assertEquals(testTransfer.getValue(), actual.getTransferInfo().getValue());
            assertEquals(testTransfer.getOperationId(), actual.getTransferInfo().getOperationId());
        });
    }

    @Test
    public void confirmTransferDtoIn2ConfirmTransfer_NotExistsTransfer() throws TransferNotFoundException {
        var confirmTransferDtoIn = new ConfirmTransferDtoIn(
                "1000000",
                CODE_CONFIRM
        );

        Mockito.doReturn(Optional.empty()).when(transferRepository)
                .findByIdOperation(Mockito.anyLong());

        assertThrowsExactly(TransferNotFoundException.class, ()-> {
            var actual = mapper.map(confirmTransferDtoIn);
        });
    }

    @Test
    public void transferInfo2TransferInfoDtoOut() {
        var expected = new TransferInfoDtoOut(
                testTransfer.getOperationId(),
                testTransfer.getCardFrom().getCardNumber(),
                testTransfer.getCardTo().getCardNumber(),
                new AmountDtoOut(testTransfer.getCurrency(), testTransfer.getValue()),
                testTransfer.isStatus()
        );

        var actual = mapper.map(testTransfer);

        assertEquals(expected, actual);
    }
}
