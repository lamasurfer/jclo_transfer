package com.example.transfer.integration;

import com.example.transfer.model.request.Amount;
import com.example.transfer.model.request.ConfirmationRequest;
import com.example.transfer.model.request.TransferRequest;
import com.example.transfer.model.response.ErrorMessage;
import com.example.transfer.model.response.SuccessMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTests {

    private static final int APP_PORT = 8080;
    private static final String HOST = "http://localhost:";

    @Container
    private static final GenericContainer<?> TRANSFER_APP = new GenericContainer<>("transfer:latest")
            .withExposedPorts(APP_PORT);

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void test_bothSteps_expectedBehaviour() {
        final Integer mappedPort = TRANSFER_APP.getMappedPort(APP_PORT);

        final TransferRequest transferRequest = new TransferRequest(
                "4111111111111111",
                YearMonth.now(),
                "343",
                "5500000000000004",
                new Amount(new BigDecimal("1000.00"), "RUR"));

        final HttpEntity<TransferRequest> transferRequestEntity = new HttpEntity<>(transferRequest);

        final ResponseEntity<SuccessMessage> transferResponse = restTemplate.
                postForEntity(HOST + mappedPort + "/transfer", transferRequestEntity, SuccessMessage.class);

        assertNotNull(transferResponse);
        assertEquals(HttpStatus.OK, transferResponse.getStatusCode());

        final Optional<SuccessMessage> optionalTransferMessage = Optional.ofNullable(transferResponse.getBody());

        assertTrue(optionalTransferMessage.isPresent());

        final String operationId = optionalTransferMessage.get().getOperationId();

        final ConfirmationRequest confirmationRequest = new ConfirmationRequest(operationId, "0000");

        final HttpEntity<ConfirmationRequest> confirmationRequestEntity = new HttpEntity<>(confirmationRequest);

        final ResponseEntity<SuccessMessage> confirmResponse = restTemplate.
                postForEntity(HOST + mappedPort + "/confirmOperation", confirmationRequestEntity, SuccessMessage.class);

        assertNotNull(confirmResponse);
        assertEquals(HttpStatus.OK, confirmResponse.getStatusCode());

        final Optional<SuccessMessage> optionalConfirmMessage = Optional.ofNullable(transferResponse.getBody());
        assertTrue(optionalConfirmMessage.isPresent());

        final SuccessMessage expected = new SuccessMessage(operationId);
        assertEquals(expected, optionalConfirmMessage.get());
    }

    @Test
    void test_transfer_wrongInput_badRequest() {
        final Integer mappedPort = TRANSFER_APP.getMappedPort(APP_PORT);

        final TransferRequest transferRequest = new TransferRequest(
                "1111111111111111",
                YearMonth.now(),
                "asd",
                "22222222222222222",
                new Amount(new BigDecimal("0"), "BTC"));

        final HttpEntity<TransferRequest> transferRequestEntity = new HttpEntity<>(transferRequest);

        final ResponseEntity<ErrorMessage> errorResponse = restTemplate.
                postForEntity(HOST + mappedPort + "/transfer", transferRequestEntity, ErrorMessage.class);

        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());

        final Optional<ErrorMessage> optionalErrorMessage = Optional.ofNullable(errorResponse.getBody());

        assertTrue(optionalErrorMessage.isPresent());

        final ErrorMessage expected = new ErrorMessage("CVC/CVV код не заполнен, " +
                "Валюта перевода не указана или не поддерживается, " +
                "Некорректная сумма перевода, Некорректный номер кредитной карты отправителя", 0);

        assertEquals(expected, optionalErrorMessage.get());
    }

    @Test
    void test_transfer_wrongAccount_internalServerError() {
        final Integer mappedPort = TRANSFER_APP.getMappedPort(APP_PORT);

        final TransferRequest transferRequest = new TransferRequest(
                "4024007188349954",
                YearMonth.now(),
                "555",
                "4111111111111111",
                new Amount(new BigDecimal("1000.00"), "RUR"));

        final HttpEntity<TransferRequest> transferRequestEntity = new HttpEntity<>(transferRequest);

        final ResponseEntity<ErrorMessage> errorResponse = restTemplate.
                postForEntity(HOST + mappedPort + "/transfer", transferRequestEntity, ErrorMessage.class);

        assertNotNull(errorResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse.getStatusCode());

        final Optional<ErrorMessage> optionalErrorMessage = Optional.ofNullable(errorResponse.getBody());

        assertTrue(optionalErrorMessage.isPresent());

        final ErrorMessage expected = new ErrorMessage("Карта отправителя не зарегистрирована " +
                "или ее данные заполнены неверно", 0);

        assertEquals(expected, optionalErrorMessage.get());
    }

    @Test
    void test_confirmOperation_noTransaction_internalServerError() {
        final Integer mappedPort = TRANSFER_APP.getMappedPort(APP_PORT);

        final ConfirmationRequest confirmationRequest = new ConfirmationRequest("1", "0000");

        final HttpEntity<ConfirmationRequest> confirmationRequestEntity = new HttpEntity<>(confirmationRequest);

        final ResponseEntity<ErrorMessage> errorResponse = restTemplate.
                postForEntity(HOST + mappedPort + "/confirmOperation", confirmationRequestEntity, ErrorMessage.class);

        assertNotNull(errorResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse.getStatusCode());

        final Optional<ErrorMessage> optionalErrorMessage = Optional.ofNullable(errorResponse.getBody());

        assertTrue(optionalErrorMessage.isPresent());

        final ErrorMessage expected = new ErrorMessage("Операция не найдена", 0);

        assertEquals(expected, optionalErrorMessage.get());
    }

    @Test
    void test_confirmOperation_wrongCode_internalServerError() {
        final Integer mappedPort = TRANSFER_APP.getMappedPort(APP_PORT);

        final TransferRequest transferRequest = new TransferRequest(
                "4111111111111111",
                YearMonth.now(),
                "343",
                "5500000000000004",
                new Amount(new BigDecimal("1000.00"), "RUR"));

        final HttpEntity<TransferRequest> transferRequestEntity = new HttpEntity<>(transferRequest);

        final ResponseEntity<SuccessMessage> transferResponse = restTemplate.
                postForEntity(HOST + mappedPort + "/transfer", transferRequestEntity, SuccessMessage.class);

        assertNotNull(transferResponse);

        final Optional<SuccessMessage> optionalTransferMessage = Optional.ofNullable(transferResponse.getBody());

        assertTrue(optionalTransferMessage.isPresent());

        final String operationId = optionalTransferMessage.get().getOperationId();
        final String wrongCode = "1111";

        final ConfirmationRequest confirmationRequest = new ConfirmationRequest(operationId, wrongCode);

        final HttpEntity<ConfirmationRequest> confirmationRequestEntity = new HttpEntity<>(confirmationRequest);

        final ResponseEntity<ErrorMessage> errorResponse = restTemplate.
                postForEntity(HOST + mappedPort + "/confirmOperation", confirmationRequestEntity, ErrorMessage.class);

        final Optional<ErrorMessage> optionalErrorMessage = Optional.ofNullable(errorResponse.getBody());

        assertTrue(optionalErrorMessage.isPresent());

        final ErrorMessage expected = new ErrorMessage("Не удалось подтвердить операцию", 0);
        assertEquals(expected, optionalErrorMessage.get());
    }
}
