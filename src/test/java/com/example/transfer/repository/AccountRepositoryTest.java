package com.example.transfer.repository;

import com.example.transfer.model.account.Account;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AccountRepositoryTest {

    private static Account account1;
    private static Account account2;

    private AccountRepository accountRepository;

    @BeforeAll
    static void init() {
        account1 = new Account();
        account1.setCardNumber("4111111111111111");
        account2 = new Account();
        account2.setCardNumber("5500000000000004");
    }

    @BeforeEach
    public void setUp() {
        accountRepository = new AccountRepository();
    }

    @Test
    void test_save_savesAndReturnsAccount() {
        final Account actual = accountRepository.save(account1);
        final List<Account> accounts = accountRepository.getAll();
        assertTrue(accounts.contains(account1));
        final int expectedSize = 1;
        assertEquals(expectedSize, accounts.size());
        assertEquals(account1, actual);
    }

    @Test
    void test_getById_ifPresent_returnsOptionalAccount() {
        final String cardNumber = account1.getCardNumber();
        accountRepository.save(account1);

        final Optional<Account> expected = Optional.of(account1);
        assertEquals(expected, accountRepository.getByCardNumber(cardNumber));
    }

    @Test
    void test_getByCardNumber_ifNotPresent_returnsEmptyOptional() {
        final String cardNumber = "absentCardNumber";
        final Optional<Account> expected = Optional.empty();
        assertEquals(expected, accountRepository.getByCardNumber(cardNumber));
    }

    @Test
    void test_getAll_returnsListWithAllAccounts() {
        accountRepository.save(account1);
        accountRepository.save(account2);
        final List<Account> accounts = accountRepository.getAll();
        final int expectedSize = 2;

        assertTrue(accounts.contains(account1));
        assertTrue(accounts.contains(account2));
        assertEquals(expectedSize, accounts.size());
    }

    @Test
    void test_removeByCardNumber_removesAccount() {
        accountRepository.save(account1);
        accountRepository.save(account2);
        final String cardNumber = account1.getCardNumber();
        accountRepository.removeByCardNumber(cardNumber);
        final int sizeBefore = 2;
        final List<Account> accounts = accountRepository.getAll();
        assertFalse(accounts.contains(account1));
        assertEquals(sizeBefore - 1, accounts.size());
    }
}