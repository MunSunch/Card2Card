package com.munsun.card2card_project.application.repository;

import com.munsun.card2card_project.application.model.Card;
import com.munsun.card2card_project.application.model.ConfirmTransfer;
import com.munsun.card2card_project.application.model.TransferInfo;
import com.munsun.card2card_project.application.repository.impl.ConfirmRepositoryImpl;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ConfirmRepositoryImplUnitTests {
    @Autowired
    private ConfirmRepositoryImpl confirmRepository;
    private ConfirmTransfer testConfirmTransfer;

    @BeforeEach
    public void setConfirmTransfer() {
        var cardFrom = new Card();
            cardFrom.setCardNumber("1111222233334444");
            cardFrom.setCardValidTill("10/30");
            cardFrom.setCardCVV("555");
            cardFrom.setCurrency("RUR");
            cardFrom.setValue(1000L);

        var cardTo = new Card();
            cardTo.setCardNumber("5555666677778888");
            cardTo.setCardValidTill("06/26");
            cardTo.setCardCVV("777");
            cardTo.setCurrency("RUR");
            cardTo.setValue(999L);

        TransferInfo transferInfo = new TransferInfo();
            transferInfo.setOperationId(1L);
            transferInfo.setValue(100L);
            transferInfo.setCurrency("RUR");
            transferInfo.setCardFrom(cardFrom);
            transferInfo.setCardTo(cardTo);
            transferInfo.setStatus(true);

        testConfirmTransfer = new ConfirmTransfer();
            testConfirmTransfer.setCodeConfirm(111L);
            testConfirmTransfer.setTransferInfo(transferInfo);
    }

    @AfterEach
    public void clearRepository() {
        confirmRepository.clear();
    }

    @Test
    public void addNewConfirm() {
        confirmRepository.add(testConfirmTransfer);
        var actual = confirmRepository.findByTransferInfo_operationId(testConfirmTransfer.getTransferInfo().getOperationId());

        assertTrue(actual.isPresent());
        assertAll(()->{
            assertEquals(testConfirmTransfer.getCodeConfirm(), actual.get().getCodeConfirm());
            assertEquals(testConfirmTransfer.getTransferInfo(), actual.get().getTransferInfo());
        });
    }

    @Test
    public void getAll() {
        confirmRepository.add(testConfirmTransfer);
        confirmRepository.add(testConfirmTransfer);
        confirmRepository.add(testConfirmTransfer);
        int expectedSize = 3;
        int actualSize = confirmRepository.getAll().size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    public void findByTransferInfo_operationId_NotFound() {
        var actual = confirmRepository.findByTransferInfo_operationId(1L);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void findByTransferInfo_operationId() {
        confirmRepository.add(testConfirmTransfer);
        var actual = confirmRepository.findByTransferInfo_operationId(testConfirmTransfer.getTransferInfo().getOperationId());
        assertTrue(actual.isPresent());
        assertEquals(testConfirmTransfer, actual.get());
    }
}
