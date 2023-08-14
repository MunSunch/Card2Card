package com.munsun.application.card2card_project.web;

import com.munsun.application.card2card_project.dto.in.*;
import com.munsun.application.card2card_project.dto.out.*;
import com.munsun.application.card2card_project.exception.CardNotFoundException;
import com.munsun.application.card2card_project.exception.TransferNotFoundException;
import com.munsun.application.card2card_project.model.Card;
import com.munsun.application.card2card_project.service.CardService;
import com.munsun.application.card2card_project.service.TransferService;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.nio.file.Path;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransferRestControllerIntegrationTests {
    @Container
    private GenericContainer<?> app = new GenericContainer<>(
                    new ImageFromDockerfile().withDockerfile(Path.of("./Dockerfile")))
                    .withExposedPorts(5500)
                    .waitingFor(Wait.forHttp("/").forStatusCode(404));

    @Autowired
    private CardService cardService;
    @Autowired
    private TransferService transferService;
    @Autowired
    private TestRestTemplate restTemplate;

    private CardDtoOut cardFrom;
    private CardDtoOut cardTo;
    String host;

    @BeforeEach
    public void setApp() throws CardNotFoundException {
        host = "http://"+app.getHost()+":"+app.getFirstMappedPort();
        cardFrom = restTemplate.postForObject(host+"/cards/add", new CardDtoIn("RUR"), CardDtoOut.class);
        cardTo = restTemplate.postForObject(host+"/cards/add", new CardDtoIn("RUR"), CardDtoOut.class);
        cardFrom = restTemplate.postForObject(host+"/cards/balance/up",
                new CardBalanceDtoIn(cardFrom.getCardNumber(), 1000L), CardDtoOut.class);
        cardTo = restTemplate.postForObject(host+"/cards/balance/up",
                new CardBalanceDtoIn(cardTo.getCardNumber(), 1000L), CardDtoOut.class);
    }

    @Test
    public void successSendWithoutConfirm() throws TransferNotFoundException {
        var dto = new TransferInfoDtoIn();
            dto.setCardFromNumber(cardFrom.getCardNumber());
            dto.setCardFromValidTill(cardFrom.getCardValidTill());
            dto.setCardFromCVV(cardFrom.getCardCVV());
            dto.setCardToNumber(cardTo.getCardNumber());
            dto.setAmountDtoIn(new AmountDtoIn(100L, "RUR"));

        var responseTransfer =
                restTemplate.postForEntity(host+"/transfer", dto, SuccessTransferDtoOut.class);
        var valueCardFromAfterTransfer =
                restTemplate.getForObject(host+"/cards/getByNumber/"+cardFrom.getCardNumber(),
                        CardDtoOut.class).getValue();
        var valueCardToAfterTransfer =
                restTemplate.getForObject(host+"/cards/getByNumber/"+cardTo.getCardNumber(),
                        CardDtoOut.class).getValue();

        assertEquals(200, responseTransfer.getStatusCode().value());
        assertNotNull(responseTransfer.getBody().getOperationId());
        assertEquals(cardFrom.getValue(), valueCardFromAfterTransfer);
        assertEquals(cardTo.getValue(), valueCardToAfterTransfer);
    }

    @Test
    public void failedSendWithoutConfirm_ErrorCurrency500() {
        var dto = new TransferInfoDtoIn();
            dto.setCardFromNumber(cardFrom.getCardNumber());
            dto.setCardFromValidTill(cardFrom.getCardValidTill());dto.setCardFromCVV(cardFrom.getCardCVV());
            dto.setCardToNumber(cardTo.getCardNumber());
            dto.setAmountDtoIn(new AmountDtoIn(100L, "USA"));
        var responseTransfer =
                restTemplate.postForEntity(host+"/transfer", dto, FailedTransferDtoOut.class);

        assertEquals(500, responseTransfer.getStatusCode().value());
        assertEquals("Неверная валюта перевода, или карта не поддерживает переводы в данной валюте",
                responseTransfer.getBody().getMessage());
    }

    @Test
    public void failedSendWithoutConfirm_NegativeBalanceAfterTransfer500() {
        var dto = new TransferInfoDtoIn();
        dto.setCardFromNumber(cardFrom.getCardNumber());
        dto.setCardFromValidTill(cardFrom.getCardValidTill());
        dto.setCardFromCVV(cardFrom.getCardCVV());
        dto.setCardToNumber(cardTo.getCardNumber());
        dto.setAmountDtoIn(new AmountDtoIn(10_000_000L, "RUR"));

        var responseTransfer =
                restTemplate.postForEntity(host+"/transfer", dto, FailedTransferDtoOut.class);

        assertEquals(500, responseTransfer.getStatusCode().value());
        assertEquals("Недостаточно средств на карте!",
                responseTransfer.getBody().getMessage());
    }

    @Test
    public void successSendWithConfirm() {
        long value = 10000L;
        var dtoTransfer = new TransferInfoDtoIn();
            dtoTransfer.setCardFromNumber(cardFrom.getCardNumber());
            dtoTransfer.setCardFromValidTill(cardFrom.getCardValidTill());
            dtoTransfer.setCardFromCVV(cardFrom.getCardCVV());
            dtoTransfer.setCardToNumber(cardTo.getCardNumber());
            dtoTransfer.setAmountDtoIn(new AmountDtoIn(value, "RUR"));
        var responseTransfer =
                restTemplate.postForEntity(host+"/transfer", dtoTransfer, SuccessTransferDtoOut.class);

        var dtoConfirm = new ConfirmTransferDtoIn();
            dtoConfirm.setCode("0000");
            dtoConfirm.setOperationId(responseTransfer.getBody().getOperationId().toString());
        var responseConfirm =
                restTemplate.postForEntity(host+"/confirmOperation", dtoConfirm, SuccessTransferDtoOut.class);

        var valueCardFromAfterTransfer =
                restTemplate.getForObject(host+"/cards/getByNumber/"+cardFrom.getCardNumber(),
                        CardDtoOut.class).getValue();
        var valueCardToAfterTransfer =
                restTemplate.getForObject(host+"/cards/getByNumber/"+cardTo.getCardNumber(),
                        CardDtoOut.class).getValue();

        assertEquals(200, responseConfirm.getStatusCode().value());
        assertNotNull(responseConfirm.getBody().getOperationId());
        assertEquals(cardFrom.getValue()-value/100, valueCardFromAfterTransfer);
        assertEquals(cardTo.getValue()+value/100, valueCardToAfterTransfer);
    }

    @Test
    public void failedSendWithConfirm_TransferNotFound500() {
        var dtoConfirm = new ConfirmTransferDtoIn();
            dtoConfirm.setCode("0000");
            dtoConfirm.setOperationId("1000000");
        var responseConfirm =
                restTemplate.postForEntity(host+"/confirmOperation", dtoConfirm, FailedTransferDtoOut.class);

        assertEquals(500, responseConfirm.getStatusCode().value());
        assertEquals("Транзакция не найдена", responseConfirm.getBody().getMessage());
    }

    @Test
    public void failedSendWithConfirm_InvalidConfirm500() {
        var dtoTransfer = new TransferInfoDtoIn();
            dtoTransfer.setCardFromNumber(cardFrom.getCardNumber());
            dtoTransfer.setCardFromValidTill(cardFrom.getCardValidTill());
            dtoTransfer.setCardFromCVV(cardFrom.getCardCVV());
            dtoTransfer.setCardToNumber(cardTo.getCardNumber());
            dtoTransfer.setAmountDtoIn(new AmountDtoIn(100L, "RUR"));
        var responseTransfer =
                restTemplate.postForEntity(host+"/transfer", dtoTransfer, SuccessTransferDtoOut.class);

        var dtoConfirm = new ConfirmTransferDtoIn();
            dtoConfirm.setCode("1111");
            dtoConfirm.setOperationId(responseTransfer.getBody().getOperationId().toString());
        var responseConfirm =
                restTemplate.postForEntity(host+"/confirmOperation", dtoConfirm, FailedTransferDtoOut.class);

        assertEquals(500, responseConfirm.getStatusCode().value());
        assertEquals("Неверный код", responseConfirm.getBody().getMessage());
    }

    @Test
    public void failedSendWithoutConfirm_CardNotFound500() {
        var dtoTransfer = new TransferInfoDtoIn();
            dtoTransfer.setCardFromNumber(cardFrom.getCardNumber());
            dtoTransfer.setCardFromValidTill(cardFrom.getCardValidTill());
            dtoTransfer.setCardFromCVV(cardFrom.getCardCVV());
            dtoTransfer.setCardToNumber("1111222233334444");
            dtoTransfer.setAmountDtoIn(new AmountDtoIn(100L, "RUR"));
        var expected = new FailedTransferDtoOut();
            expected.setId(0L);
            expected.setMessage("Карта или карты не найдены");

        var responseTransfer =
                restTemplate.postForEntity(host+"/transfer", dtoTransfer, FailedTransferDtoOut.class);

        assertEquals(500, responseTransfer.getStatusCode().value());
        assertEquals(expected, responseTransfer.getBody());
    }
}