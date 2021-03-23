package com.example.transfer_test.service;

import com.example.transfer_test.model.account.Account;
import com.example.transfer_test.model.account.AccountType;
import com.example.transfer_test.model.request.Amount;
import com.example.transfer_test.model.request.TransferRequest;
import com.example.transfer_test.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository repository;
    @InjectMocks
    private AccountService accountService;

    @Test
    void test_getSenderAccount_ifRequestAndAccountDataMatches_returnsOptionalAccount() {
        final TransferRequest transferRequest = new TransferRequest(
                "4111111111111111",
                YearMonth.now(),
                "343",
                "5500000000000004",
                new Amount(BigDecimal.ONE, "RUR"));

        final Account account = new Account(
                "4111111111111111",
                AccountType.INTERNAL,
                "343",
                YearMonth.now(),
                BigDecimal.ONE);

        final String id = transferRequest.getCardFromNumber();
        when(repository.getByCardNumber(id)).thenReturn(Optional.of(account));

        final Optional<Account> expected = Optional.of(account);
        assertEquals(expected, accountService.getSenderAccount(transferRequest));
    }

    @Test
    void test_getSenderAccount_accountIsNotPresent_returnsEmptyOptional() {
        final String wrongCardFromNumber = "4111111111111111";

        final TransferRequest transferRequest = new TransferRequest();
        transferRequest.setCardFromNumber(wrongCardFromNumber);

        when(repository.getByCardNumber(wrongCardFromNumber)).thenReturn(Optional.empty());

        final Optional<Account> expected = Optional.empty();
        assertEquals(expected, accountService.getSenderAccount(transferRequest));
    }

    @Test
    void test_getSenderAccount_requestAndAccountNumberMatches_otherDataDoesNotMatch_returnsEmptyOptional() {
        final String cardFromNumber = "4111111111111111";
        final TransferRequest transferRequest1 = new TransferRequest(
                cardFromNumber,
                YearMonth.now(),
                "545", // different
                "5500000000000004",
                new Amount(BigDecimal.ONE, "RUR")
        );
        final TransferRequest transferRequest2 = new TransferRequest(
                cardFromNumber,
                YearMonth.of(2025, 5), // different
                "343",
                "5500000000000004",
                new Amount(BigDecimal.ONE, "RUR")
        );
        final Account account = new Account(
                cardFromNumber,
                AccountType.INTERNAL,
                "343", //
                YearMonth.now(), //
                BigDecimal.ONE);

        when(repository.getByCardNumber(cardFromNumber)).thenReturn(Optional.of(account));

        final Optional<Account> expected = Optional.empty();
        assertEquals(expected, accountService.getSenderAccount(transferRequest1));
        assertEquals(expected, accountService.getSenderAccount(transferRequest2));
    }

    @Test
    void test_getSenderAccount_doesNotReturnExternalTypeAccounts_evenIfDataIsPresent() {
        final String cardFromNumber = "4111111111111111";
        final Account externalAccount = new Account(
                cardFromNumber,
                AccountType.EXTERNAL, // external
                "343",
                YearMonth.now(),
                BigDecimal.ONE);

        final TransferRequest transferRequest = new TransferRequest();
        transferRequest.setCardFromNumber(cardFromNumber);

        when(repository.getByCardNumber(cardFromNumber)).thenReturn(Optional.of(externalAccount));

        final Optional<Account> expected = Optional.empty();
        assertEquals(expected, accountService.getSenderAccount(transferRequest));
    }

    @Test
    void test_getReceiverAccount_ifPresent_returnsInternalAccount() {
        final String cardNumber = "4111111111111111";
        final Account account = new Account(
                cardNumber,
                AccountType.INTERNAL,
                "343",
                YearMonth.now(),
                BigDecimal.ONE);

        when(repository.getByCardNumber(cardNumber)).thenReturn(Optional.of(account));
        assertEquals(account, accountService.getReceiverAccount(cardNumber));
    }

    @Test
    void test_getReceiverAccount_ifNotPresent_returnsNewExternalAccount() {
        final String cardNumber = "5500000000000004";
        when(repository.getByCardNumber(cardNumber)).thenReturn(Optional.empty());

        final Account expected = new Account(cardNumber, AccountType.EXTERNAL, null, null, null);
        final Account actual = accountService.getReceiverAccount(cardNumber);
        assertEquals(expected, actual);
        verify(repository).save(actual);
    }

    @Test
    void test_getReceiverAccount_ifPresent_returnsExternalAccount() {
        final String cardNumber = "5500000000000004";
        final Account expected = new Account(cardNumber, AccountType.EXTERNAL, null, null, null);

        when(repository.getByCardNumber(cardNumber)).thenReturn(Optional.of(expected));

        final Account actual = accountService.getReceiverAccount(cardNumber);
        assertEquals(expected, actual);
        verify(repository).getByCardNumber(cardNumber);
    }

    @Test
    void test_addNewInternalAccount_createsSavesAndReturnsNewInternalAccount() {
        final String cardNumber = "4111111111111111";
        final AccountType accountType = AccountType.INTERNAL;
        final String cvv = "343";
        final YearMonth validThruTill = YearMonth.now();
        final BigDecimal balance = new BigDecimal("100000.00");

        final Account expected = new Account(cardNumber, accountType, cvv, validThruTill, balance);
        final Account actual = accountService.addNewInternalAccount(cardNumber, cvv, validThruTill, balance);

        assertEquals(expected, actual);
        verify(repository).save(actual);
    }

    @Test
    void test_addNewExternalAccount_createsSavesAndReturnsNewInternalAccount() {
        final String cardNumber = "5500000000000004";
        final Account expected = new Account(cardNumber, AccountType.EXTERNAL, null, null, null);

        final Account actual = accountService.addNewExternalAccount(cardNumber);
        assertEquals(expected, actual);
        verify(repository).save(actual);
    }
}