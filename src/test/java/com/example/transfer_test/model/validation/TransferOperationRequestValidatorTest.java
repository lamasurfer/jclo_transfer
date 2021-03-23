package com.example.transfer_test.model.validation;


import com.example.transfer_test.model.request.Amount;
import com.example.transfer_test.model.request.TransferRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
class TransferOperationRequestValidatorTest {

    @Autowired
    private Validator validator;

    @Test
    void test_transferRequest_validCard_noViolations() {
        final TransferRequest request = new TransferRequest(
                "4485583965966956",
                YearMonth.now(), // current month works properly
                "333",
                "4485058901151674",
                new Amount(BigDecimal.TEN, "RUR"));

        Set<ConstraintViolation<TransferRequest>> violations = validator.validate(request);
        assertEquals(0, violations.size());
    }

    @Test
    void test_transferRequest_sameInvalidNumbers_expectedTwoViolations() {
        final TransferRequest request = new TransferRequest(
                "1111111111111111", // invalid
                YearMonth.now(),
                "343",
                "1111111111111111", // invalid
                new Amount(BigDecimal.TEN, "RUR"));
        // only checks validity
        Set<ConstraintViolation<TransferRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size());
    }

    @Test
    void test_transferRequest_sameValidNumbers_expectedOneViolation() {
        final TransferRequest request = new TransferRequest(
                "4485583965966956", // same
                YearMonth.now(),
                "343",
                "4485583965966956", // same
                new Amount(BigDecimal.TEN, "RUR"));
        // checks valid cards only
        Set<ConstraintViolation<TransferRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void test_transferRequest_differentInvalidNumbers_expectedTwoViolations() {
        final TransferRequest request = new TransferRequest(
                "1111111111111111", // invalid
                YearMonth.now(),
                "333",
                "2222222222222222", // invalid
                new Amount(BigDecimal.TEN, "RUR"));

        Set<ConstraintViolation<TransferRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size());
    }

    @Test
    void test_transferRequest_expiredCard_expectedOneViolation() {
        final TransferRequest request = new TransferRequest(
                "4485583965966956",
                YearMonth.of(2021, 2), // expired
                "333",
                "4485058901151674",
                new Amount(BigDecimal.TEN, "RUR"));

        Set<ConstraintViolation<TransferRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void test_transferRequest_cvcWithLetters_expectedOneViolation() {
        final TransferRequest request = new TransferRequest(
                "4485583965966956",
                YearMonth.now(),
                "aaaa", // invalid
                "4485058901151674",
                new Amount(BigDecimal.TEN, "RUR"));

        Set<ConstraintViolation<TransferRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void test_transferRequest_longCvvWithDigits_expectedOneViolation() {
        final TransferRequest request = new TransferRequest(
                "4485583965966956",
                YearMonth.now(),
                "1111", // invalid
                "4485058901151674",
                new Amount(BigDecimal.TEN, "RUR"));

        Set<ConstraintViolation<TransferRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void test_transferRequest_negativeAmount_expectedOneViolation() {
        final TransferRequest request = new TransferRequest(
                "4485583965966956",
                YearMonth.now(),
                "333",
                "4485058901151674",
                new Amount(BigDecimal.valueOf(-1000), "RUR"));

        Set<ConstraintViolation<TransferRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void test_transferRequest_zeroAmount_expectedOneViolation() {
        final TransferRequest request = new TransferRequest(
                "4485583965966956",
                YearMonth.now(),
                "333",
                "4485058901151674",
                new Amount(BigDecimal.ZERO, "RUR"));

        Set<ConstraintViolation<TransferRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void test_transferRequest_wrongCurrency_expectedOneViolation() {
        final TransferRequest request = new TransferRequest(
                "4485583965966956",
                YearMonth.now(),
                "333",
                "4485058901151674",
                new Amount(BigDecimal.TEN, "BTC")); // wrong

        Set<ConstraintViolation<TransferRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
    }

    @Test
    void test_allWrong_validation_expectedSixViolations() {
        final TransferRequest request = new TransferRequest(
                "",
                YearMonth.of(1900, 2),
                "",
                "",
                new Amount(BigDecimal.valueOf(-1000), ""));

        Set<ConstraintViolation<TransferRequest>> violations = validator.validate(request);
        assertEquals(6, violations.size());
    }

    @Test
    void test_allNull_validation_expectedFiveViolations() {
        final TransferRequest request = new TransferRequest(
                null,
                null,
                null,
                null,
                null);

        Set<ConstraintViolation<TransferRequest>> violations = validator.validate(request);
        assertEquals(5, violations.size());
    }

    @Test
    void test_allNullPlus_validation_expectedFiveViolations() {
        final TransferRequest request = new TransferRequest(
                null,
                null,
                null,
                null,
                new Amount(null, null));

        Set<ConstraintViolation<TransferRequest>> violations = validator.validate(request);
        assertEquals(6, violations.size());
    }
}