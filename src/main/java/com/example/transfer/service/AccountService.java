package com.example.transfer.service;

import com.example.transfer.model.account.Account;
import com.example.transfer.model.account.AccountType;
import com.example.transfer.model.request.TransferRequest;
import com.example.transfer.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Optional<Account> getSenderAccount(TransferRequest transferRequest) {
        final String cardFromNumber = transferRequest.getCardFromNumber();
        final String cardFromCvv = transferRequest.getCardFromCVV();
        final YearMonth cardFromValidTill = transferRequest.getCardFromValidTill();
        return accountRepository.getByCardNumber(cardFromNumber)
                .filter(account -> AccountType.INTERNAL == account.getAccountType()
                        && cardFromCvv.equals(account.getCvv())
                        && cardFromValidTill.equals(account.getValidThruTill()));
    }

    public Account getReceiverAccount(String cardToNumber) {
        return accountRepository.getByCardNumber(cardToNumber)
                .orElseGet(() -> addNewExternalAccount(cardToNumber));
    }

    public Account addNewInternalAccount(String cardNumber, String cvv, YearMonth validThruTill, BigDecimal balance) {
        final Account account = new Account(cardNumber, AccountType.INTERNAL, cvv, validThruTill, balance);
        accountRepository.save(account);
        return account;
    }

    public Account addNewExternalAccount(String cardNumber) {
        final Account account = new Account(cardNumber, AccountType.EXTERNAL, null, null, null);
        accountRepository.save(account);
        return account;
    }
}
