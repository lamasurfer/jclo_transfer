package com.example.transfer.repository;

import com.example.transfer.model.transaction.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TransactionRepositoryTest {

    private static Transaction transaction1;
    private static Transaction transaction2;
    private TransactionRepository transactionRepository;

    @BeforeAll
    static void init() {
        transaction1 = new Transaction();
        transaction1.setOperationId("1");
        transaction2 = new Transaction();
        transaction2.setOperationId("2");
    }

    @BeforeEach
    public void setUp() {
        transactionRepository = new TransactionRepository();
    }

    @Test
    void test_save_savesAndReturnsTransaction() {
        final Transaction actual = transactionRepository.save(transaction1);
        final List<Transaction> transactions = transactionRepository.getAll();
        assertTrue(transactions.contains(transaction1));
        final int expectedSize = 1;
        assertEquals(expectedSize, transactions.size());
        assertEquals(transaction1, actual);
    }

    @Test
    void test_getById_ifPresent_returnsOptionalTransaction() {
        final String id = transaction1.getOperationId();
        transactionRepository.save(transaction1);

        final Optional<Transaction> expected = Optional.of(transaction1);
        assertEquals(expected, transactionRepository.getById(id));
    }

    @Test
    void test_getById_ifNotPresent_returnsEmptyOptional() {
        final String id = "someWrongId";

        final Optional<Transaction> expected = Optional.empty();
        assertEquals(expected, transactionRepository.getById(id));
    }

    @Test
    void test_getAll_returnsListWithAllTransactions() {
        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        final List<Transaction> transactions = transactionRepository.getAll();
        final int expectedSize = 2;

        assertTrue(transactions.contains(transaction1));
        assertTrue(transactions.contains(transaction2));
        assertEquals(expectedSize, transactions.size());
    }

    @Test
    void test_removeById_removesTransaction() {
        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        final String id = transaction1.getOperationId();
        transactionRepository.removeById(id);
        final int sizeBefore = 2;
        final List<Transaction> transactions = transactionRepository.getAll();
        assertFalse(transactions.contains(transaction1));
        assertEquals(sizeBefore - 1, transactions.size());
    }
}