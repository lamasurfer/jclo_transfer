package com.example.transfer.service;

import com.example.transfer.model.account.Account;
import com.example.transfer.model.account.AccountType;
import com.example.transfer.model.transaction.Transaction;
import com.example.transfer.model.transaction.TransactionStatus;
import com.example.transfer.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository repository;
    @InjectMocks
    private TransactionService transactionService;

    @Test
    void test_getTransaction_ifEnoughFunds_returnsOptionalTransaction() {
        final Account accountFrom = new Account();
        accountFrom.setBalance(new BigDecimal("5000.00"));
        final Account accountTo = new Account();
        final BigDecimal amount = new BigDecimal("1000.00");

        assertTrue(transactionService.getTransaction(accountFrom, accountTo, amount).isPresent());
    }

    @Test
    void test_getTransaction_ifEvenFunds_returnsOptionalTransaction() {
        final Account accountFrom = new Account();
        accountFrom.setBalance(new BigDecimal("1010.00")); // 10.00 - 1% fee
        final Account accountTo = new Account();
        final BigDecimal amount = new BigDecimal("1000.00");

        assertTrue(transactionService.getTransaction(accountFrom, accountTo, amount).isPresent());
    }

    @Test
    void test_getTransaction_ifNotEnoughFunds_returnsEmptyOptional() {
        final Account accountFrom = new Account();
        accountFrom.setBalance(new BigDecimal("1009.99"));
        final Account accountTo = new Account();
        final BigDecimal amount = new BigDecimal("1000.00");

        assertFalse(transactionService.getTransaction(accountFrom, accountTo, amount).isPresent());
    }

    @Test
    void test_createTransaction_createsSavesAndReturnsTransaction() {
        final Account accountFrom = new Account();
        final Account accountTo = new Account();
        final BigDecimal amount = BigDecimal.ONE;
        final BigDecimal totalAmount = BigDecimal.ONE;

        final Transaction actual = transactionService.createTransaction(accountFrom, accountTo, amount, totalAmount);
        final TransactionStatus expectedStatus = TransactionStatus.REQUESTED;
        final LocalDateTime expectedConfirmedTime = LocalDateTime.MIN;

        assertAll(
                () -> assertNotNull(actual),
                () -> assertNotNull(actual.getOperationId()),
                () -> assertNotNull(actual.getRequestedTime()),
                () -> assertEquals(expectedStatus, actual.getTransactionStatus()),
                () -> assertEquals(expectedConfirmedTime, actual.getProcessedTime()),
                () -> assertNull(actual.getVerificationCode())
        );
        verify(repository).save(actual);
    }

    @Test
    void test_processTransaction_toInternalAccount_changesTransactionStatus_changesBothBalances() {
        final Transaction transaction = new Transaction(
                "1",
                LocalDateTime.now(),
                new Account("4111111111111111", AccountType.INTERNAL, "343", YearMonth.now(), new BigDecimal("10000.00")),
                new Account("5500000000000004", AccountType.INTERNAL, "543", YearMonth.now(), BigDecimal.ZERO),
                new BigDecimal("4000.00"),
                new BigDecimal("0.01"),
                new BigDecimal("4040.00"),
                TransactionStatus.REQUESTED,
                LocalDateTime.MIN,
                "0000"
        );

        final Transaction actual = transactionService.processTransaction(transaction);

        final TransactionStatus expectedStatus = TransactionStatus.PROCESSED;
        assertEquals(expectedStatus, actual.getTransactionStatus());

        final LocalDateTime defaultMinimalTime = LocalDateTime.MIN;
        assertNotEquals(defaultMinimalTime, actual.getProcessedTime());

        final BigDecimal expectedFromBalance = new BigDecimal("5960.00");
        assertEquals(expectedFromBalance, actual.getAccountFrom().getBalance());

        final BigDecimal expectedToBalance = new BigDecimal("4000.00");
        assertEquals(expectedToBalance, actual.getAccountTo().getBalance());
    }

    @Test
    void test_processTransaction_toExternalAccount_changesTransactionStatus_worksOnlyWithFromBalance() {
        final Transaction transaction = new Transaction(
                "1",
                LocalDateTime.now(),
                new Account("4111111111111111", AccountType.INTERNAL, "343", YearMonth.now(), new BigDecimal("10000.00")),
                new Account("5500000000000004", AccountType.EXTERNAL, null, null, null),
                new BigDecimal("4000.00"),
                new BigDecimal("0.01"),
                new BigDecimal("4040.00"),
                TransactionStatus.REQUESTED,
                LocalDateTime.MIN,
                "0000"
        );

        transactionService.processTransaction(transaction);

        final TransactionStatus expectedStatus = TransactionStatus.PROCESSED;
        assertEquals(expectedStatus, transaction.getTransactionStatus());

        final LocalDateTime defaultMinimalTime = LocalDateTime.MIN;
        assertNotEquals(defaultMinimalTime, transaction.getProcessedTime());

        final BigDecimal expectedFromBalance = new BigDecimal("5960.00");
        assertEquals(expectedFromBalance, transaction.getAccountFrom().getBalance());

        assertNull(transaction.getAccountTo().getBalance());
    }

    @Test
    void test_getById_ifPresent_returnsOptionalTransaction() {
        final String id = "1";
        final Transaction transaction = new Transaction();
        transaction.setOperationId(id);

        when(repository.getById(id)).thenReturn(Optional.of(transaction));
        final Optional<Transaction> expected = Optional.of(transaction);
        assertEquals(expected, transactionService.getById(id));
    }

    @Test
    void test_getById_ifNotPresent_returnsEmptyOptional() {
        final String id = "1";
        when(repository.getById(id)).thenReturn(Optional.empty());
        final Optional<Transaction> expected = Optional.empty();
        assertEquals(expected, transactionService.getById(id));
    }

    @Test
    void test_calculateTotalAmount() {
        final BigDecimal amount1 = new BigDecimal("1000");
        final BigDecimal expected1 = new BigDecimal("1010.00");

        assertEquals(expected1, transactionService.calculateTotalAmount(amount1));

        final BigDecimal amount2 = new BigDecimal("1000.00");
        final BigDecimal expected2 = new BigDecimal("1010.00");

        assertEquals(expected2, transactionService.calculateTotalAmount(amount2));
    }

    @Test
    void test_isEnoughFunds_enoughFunds_returnsTrue() {
        final BigDecimal totalAmount = new BigDecimal("1010.00");
        final Account accountFrom = new Account();
        accountFrom.setBalance(new BigDecimal("2000.00"));

        assertTrue(transactionService.isEnoughFunds(accountFrom, totalAmount));
    }

    @Test
    void test_isEnoughFunds_evenFunds_returnsTrue() {
        final BigDecimal totalAmount = new BigDecimal("1010.00");
        final Account accountFrom = new Account();
        accountFrom.setBalance(new BigDecimal("1010.00"));

        assertTrue(transactionService.isEnoughFunds(accountFrom, totalAmount));
    }

    @Test
    void test_isEnoughFunds_notEnoughFunds_returnsFalse() {
        final BigDecimal totalAmount = new BigDecimal("1010.00");
        final Account accountFrom = new Account();
        accountFrom.setBalance(new BigDecimal("1009.99"));

        assertFalse(transactionService.isEnoughFunds(accountFrom, totalAmount));
    }
}