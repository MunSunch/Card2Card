package com.munsun.application.card2card_project;

import com.munsun.application.card2card_project.dto.in.AmountDtoIn;
import com.munsun.application.card2card_project.dto.in.ConfirmTransferDtoIn;
import com.munsun.application.card2card_project.dto.in.TransferInfoDtoIn;
import com.munsun.application.card2card_project.dto.out.AmountDtoOut;
import com.munsun.application.card2card_project.dto.out.CardDtoOut;
import com.munsun.application.card2card_project.dto.out.TransferInfoDtoOut;
import com.munsun.application.card2card_project.model.Card;
import com.munsun.application.card2card_project.model.ConfirmTransfer;
import com.munsun.application.card2card_project.model.TransferInfo;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class ModelMapperTests {
    @Autowired
    private ModelMapper modelMapper;

    @AfterEach
    public void testConfigMapper() {
        assertDoesNotThrow(() -> modelMapper.validate());
    }

    @Test
    public void mapCard2CardDtoOut() {
        Card card = new Card();
            card.setCardNumber("1111222233334444");
            card.setCardValidTill("10/25");
            card.setCardCVV("666");
            card.setCurrency("RUR");
            card.setValue(1000L);
            card.setIsActive(true);

        CardDtoOut expected = new CardDtoOut();
            expected.setCardNumber("1111222233334444");
            expected.setCardValidTill("10/25");
            expected.setCardCVV("666");
            expected.setCurrency("RUR");
            expected.setValue(1000L);
            expected.setIsActive(true);
        CardDtoOut actual = modelMapper.map(card, CardDtoOut.class);

        assertEquals(expected, actual);
    }

    @Test
    public void mapTransferInfoToTransferInfoDtoOut() {
        TransferInfo transferInfo = new TransferInfo();
            transferInfo.setStatus(false);
            transferInfo.setValue(100L);
            transferInfo.setOperationId(2L);
            transferInfo.setCurrency("RUR");
                Card cardFrom = new Card();
                    cardFrom.setCardNumber("1111222233334444");
                    cardFrom.setCardValidTill("11/29");
                    cardFrom.setCurrency("RUR");
                    cardFrom.setValue(1000L);
                    cardFrom.setIsActive(true);
                    cardFrom.setCardCVV("777");
            transferInfo.setCardFrom(cardFrom);
                Card cardTo = new Card();
                    cardTo.setCardNumber("5555666677778888");
                    cardTo.setCardValidTill("11/29");
                    cardTo.setCurrency("RUR");
                    cardTo.setValue(1000L);
                    cardTo.setIsActive(true);
                    cardTo.setCardCVV("888");
            transferInfo.setCardTo(cardTo);

        TransferInfoDtoOut expected = new TransferInfoDtoOut();
            expected.setCardFromNumber("1111222233334444");
            expected.setCardFromValidTill("11/29");
            expected.setCardFromCVV("777");
            expected.setCardToNumber("5555666677778888");
                AmountDtoOut amountDtoOut = new AmountDtoOut();
                    amountDtoOut.setCurrency("RUR");
                    amountDtoOut.setValue(100L);
            expected.setAmountDtoOut(amountDtoOut);

        TransferInfoDtoOut actual = modelMapper.map(transferInfo, TransferInfoDtoOut.class);

        assertEquals(expected, actual);
    }

    @Test
    public void mapConfirmTransferDtoIn2ConfirmTransfer() {
        var confirmTransferDtoIn = new ConfirmTransferDtoIn();
            confirmTransferDtoIn.setOperationId("3");
            confirmTransferDtoIn.setCode("0");

//        var confirmTransfer = new ConfirmTransfer();
//            confirmTransfer.setCodeConfirm(7777L);
//            confirmTransfer.set
        var actual = modelMapper.map(confirmTransferDtoIn, ConfirmTransfer.class);
    }

    @Test
    public void mapTransferInfoDtoIn2TransferInfo() {
        TransferInfoDtoIn transferInfoDtoIn = new TransferInfoDtoIn();
            transferInfoDtoIn.setCardFromNumber("1251580016888995");
            transferInfoDtoIn.setCardFromValidTill("08/28");
            transferInfoDtoIn.setCardFromCVV("318");
            transferInfoDtoIn.setCardToNumber("7596830436697811");
            AmountDtoIn amountDtoIn = new AmountDtoIn();
                amountDtoIn.setCurrency("RUR");
                amountDtoIn.setValue(100L);
            transferInfoDtoIn.setAmountDtoIn(amountDtoIn);

        var transfer = modelMapper.map(transferInfoDtoIn, TransferInfo.class);
    }
}
