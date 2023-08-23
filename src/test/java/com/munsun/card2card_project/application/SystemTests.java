package com.munsun.card2card_project.application;

import com.munsun.card2card_project.application.dto.in.*;
import com.munsun.card2card_project.application.dto.out.CardDtoOut;
import com.munsun.card2card_project.application.dto.out.FailedTransferDtoOut;
import com.munsun.card2card_project.application.dto.out.SuccessTransferDtoOut;
import com.munsun.card2card_project.application.exception.CardNotFoundException;
import com.munsun.card2card_project.application.exception.TransferNotFoundException;
import com.munsun.card2card_project.application.service.CardService;
import com.munsun.card2card_project.application.service.TransferService;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.file.Path;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SystemTests {
    @Container
    private GenericContainer<?> app = new GenericContainer<>(
                    new ImageFromDockerfile().withDockerfile(Path.of("./Dockerfile")))
                    .withExposedPorts(5500)
                    .waitingFor(Wait.forHttp("/").forStatusCode(404));

    @Autowired
    private CardService cardService;
    @Autowired
    private TestRestTemplate restTemplate;

    private CardDtoOut cardFrom;
    private CardDtoOut cardTo;
    String host;

    @BeforeEach
    public void setApp() throws CardNotFoundException {
        host = "http://"+app.getHost()+":"+app.getFirstMappedPort();
        cardFrom = restTemplate.postForObject(host+"/cards/add", new CardDtoIn("RUR"), CardDtoOut.class);
        cardFrom = restTemplate.postForObject(host+"/cards/balance/up", new CardBalanceDtoIn(cardFrom.cardNumber(), 1000L), CardDtoOut.class);

        cardTo = restTemplate.postForObject(host+"/cards/add", new CardDtoIn("RUR"), CardDtoOut.class);
        cardTo = restTemplate.postForObject(host+"/cards/balance/up", new CardBalanceDtoIn(cardTo.cardNumber(), 1000L), CardDtoOut.class);
    }

    @Test
    public void successSendWithoutConfirm() throws TransferNotFoundException {
        var dto = new TransferInfoDtoIn(
                cardFrom.cardNumber(),
                cardFrom.cardValidTill(),
                cardFrom.cardCVV(),
                cardTo.cardNumber(),
                new AmountDtoIn(100L, "RUR"));

        var responseTransfer = restTemplate.postForEntity(host+"/transfer", dto, SuccessTransferDtoOut.class);
        var valueCardFromAfterTransfer = restTemplate.getForObject(host+"/cards/getByNumber/"+cardFrom.cardNumber(),
                        CardDtoOut.class).value();
        var valueCardToAfterTransfer = restTemplate.getForObject(host+"/cards/getByNumber/"+cardTo.cardNumber(),
                        CardDtoOut.class).value();

        assertEquals(200, responseTransfer.getStatusCode().value());
        assertNotNull(responseTransfer.getBody().operationId());
        assertEquals(cardFrom.value(), valueCardFromAfterTransfer);
        assertEquals(cardTo.value(), valueCardToAfterTransfer);
    }

    @Test
    public void failedSendWithoutConfirm_ErrorCurrency500() {
        var dto = new TransferInfoDtoIn(
                cardFrom.cardNumber(),
                cardFrom.cardValidTill(),
                cardFrom.cardCVV(),
                cardTo.cardNumber(),
                new AmountDtoIn(100L, "USA")
        );
        var responseTransfer =
                restTemplate.postForEntity(host+"/transfer", dto, FailedTransferDtoOut.class);

        assertEquals(500, responseTransfer.getStatusCode().value());
        assertEquals("Неверная валюта перевода, или карта не поддерживает переводы в данной валюте",
                responseTransfer.getBody().message());
    }

    @Test
    public void failedSendWithoutConfirm_NegativeBalanceAfterTransfer500() {
        var dto = new TransferInfoDtoIn(
                cardFrom.cardNumber(),
                cardFrom.cardValidTill(),
                cardFrom.cardCVV(),
                cardTo.cardNumber(),
                new AmountDtoIn(10_000_000L, "RUR")
        );
        var responseTransfer =
                restTemplate.postForEntity(host+"/transfer", dto, FailedTransferDtoOut.class);

        assertEquals(500, responseTransfer.getStatusCode().value());
        assertEquals("Недостаточно средств на карте!",
                responseTransfer.getBody().message());
    }

    @Test
    public void successSendWithConfirm() {
        long value = 10000L;
        var dtoTransfer = new TransferInfoDtoIn(
                cardFrom.cardNumber(),
                cardFrom.cardValidTill(),
                cardFrom.cardCVV(),
                cardTo.cardNumber(),
                new AmountDtoIn(value, "RUR")
        );

        var responseTransfer =
                restTemplate.postForEntity(host+"/transfer", dtoTransfer, SuccessTransferDtoOut.class);

        var dtoConfirm = new ConfirmTransferDtoIn(responseTransfer.getBody().operationId().toString(), "0000");
        var responseConfirm =
                restTemplate.postForEntity(host+"/confirmOperation", dtoConfirm, SuccessTransferDtoOut.class);

        var valueCardFromAfterTransfer =
                restTemplate.getForObject(host+"/cards/getByNumber/"+cardFrom.cardNumber(),
                        CardDtoOut.class).value();
        var valueCardToAfterTransfer =
                restTemplate.getForObject(host+"/cards/getByNumber/"+cardTo.cardNumber(),
                        CardDtoOut.class).value();

        assertEquals(200, responseConfirm.getStatusCode().value());
        assertNotNull(responseConfirm.getBody().operationId());
        assertEquals(cardFrom.value()-value/100, valueCardFromAfterTransfer);
        assertEquals(cardTo.value()+value/100, valueCardToAfterTransfer);
    }

    @Test
    public void failedSendWithConfirm_TransferNotFound500() {
        var dtoConfirm = new ConfirmTransferDtoIn("1000000", "0000");
        var responseConfirm =
                restTemplate.postForEntity(host+"/confirmOperation", dtoConfirm, FailedTransferDtoOut.class);

        assertEquals(500, responseConfirm.getStatusCode().value());
        assertEquals("Транзакция не найдена", responseConfirm.getBody().message());
    }

    @Test
    public void failedSendWithConfirm_InvalidConfirm500() {
        var dtoTransfer = new TransferInfoDtoIn(
                cardFrom.cardNumber(),
                cardFrom.cardValidTill(),
                cardFrom.cardCVV(),
                cardTo.cardNumber(),
                new AmountDtoIn(100L, "RUR")
        );
        var responseTransfer =
                restTemplate.postForEntity(host+"/transfer", dtoTransfer, SuccessTransferDtoOut.class);

        var dtoConfirm = new ConfirmTransferDtoIn(responseTransfer.getBody().operationId().toString(), "1111");
        var responseConfirm =
                restTemplate.postForEntity(host+"/confirmOperation", dtoConfirm, FailedTransferDtoOut.class);

        assertEquals(500, responseConfirm.getStatusCode().value());
        assertEquals("Неверный код", responseConfirm.getBody().message());
    }

    @Test
    public void failedSendWithoutConfirm_CardNotFound500() {
        var dtoTransfer = new TransferInfoDtoIn(
                "1111222233334444",
                "07/27",
                "999",
                cardTo.cardNumber(),
                new AmountDtoIn(100L, "RUR")
        );
        var expected = new FailedTransferDtoOut("Карта или карты не найдены", 0L);
        var responseTransfer =
                restTemplate.postForEntity(host+"/transfer", dtoTransfer, FailedTransferDtoOut.class);

        assertEquals(500, responseTransfer.getStatusCode().value());
        assertEquals(expected, responseTransfer.getBody());
    }
}