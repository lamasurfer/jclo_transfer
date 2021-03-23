package com.example.transfer_test.service;

import com.example.transfer_test.model.transaction.Transaction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VerificationServiceTest {

    private final VerificationService verificationService = new VerificationService();

    @Test
    void setVerificationCode() {
        final Transaction transaction = new Transaction();
        final String expectedCode = "0000";

        assertEquals(expectedCode, verificationService.setVerificationCode(transaction));
    }

    @Test
    void test_verifyTransaction_ifCorrectCode_returnsTrue() {
        final Transaction transaction = new Transaction();
        transaction.setVerificationCode("0000");

        assertTrue(verificationService.verifyTransaction(transaction));
    }

    @Test
    void test_verifyTransaction_ifWrongCode_returnsFalse() {
        final Transaction transaction = new Transaction();
        transaction.setVerificationCode("wrongCode");

        assertFalse(verificationService.verifyTransaction(transaction));
    }
}