package com.munsun.card2card_project.application.repository;

import com.munsun.card2card_project.application.model.Card;
import com.munsun.card2card_project.application.model.TransferInfo;
import com.munsun.card2card_project.application.repository.impl.TransferRepositoryImpl;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TransferRepositoryImplUnitTests {
    @Autowired
    private TransferRepositoryImpl transferRepository;
    private TransferInfo testTransferInfo;

    @BeforeEach
    public void setTestTransferInfo() {
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

        testTransferInfo = new TransferInfo();
            testTransferInfo.setOperationId(1111L);
            testTransferInfo.setValue(100L);
            testTransferInfo.setCurrency("RUR");
            testTransferInfo.setCardFrom(cardFrom);
            testTransferInfo.setCardTo(cardTo);
            testTransferInfo.setStatus(true);
    }

    @AfterEach
    public void clearRepository() {
        transferRepository.clear();
    }

    @Test
    public void addNewTransferInfo() {
        long id = transferRepository.add(testTransferInfo).getOperationId();
        var actual = transferRepository.findByIdOperation(id);

        assertTrue(actual.isPresent());
        assertAll(()->{
            assertNotEquals(testTransferInfo.getOperationId(), actual.get().getOperationId());
            assertEquals(testTransferInfo.getCardFrom(), actual.get().getCardFrom());
            assertEquals(testTransferInfo.getCardTo(), actual.get().getCardTo());
            assertEquals(testTransferInfo.getValue(), actual.get().getValue());
            assertEquals(testTransferInfo.getCurrency(), actual.get().getCurrency());
        });
    }

    @Test
    public void findByIdOperation_TransferNotFound() {
        var actual = transferRepository.findByIdOperation(1L);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void findByIdOperation() {
        long id = transferRepository.add(testTransferInfo).getOperationId();
        var actual = transferRepository.findByIdOperation(id);
        assertTrue(actual.isPresent());
        assertAll(()->{
            assertEquals(id, actual.get().getOperationId());
            assertEquals(testTransferInfo.getCardFrom(), actual.get().getCardFrom());
            assertEquals(testTransferInfo.getCardTo(), actual.get().getCardTo());
            assertEquals(testTransferInfo.getValue(), actual.get().getValue());
            assertEquals(testTransferInfo.getCurrency(), actual.get().getCurrency());
        });
    }

    @Test
    public void getAll() {
        transferRepository.add(testTransferInfo);
        transferRepository.add(testTransferInfo);
        transferRepository.add(testTransferInfo);
        int expectedSize = 3;
        int actualSize = transferRepository.getAll().size();
        assertEquals(expectedSize, actualSize);
    }
}
