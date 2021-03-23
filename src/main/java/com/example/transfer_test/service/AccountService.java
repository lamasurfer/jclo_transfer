package com.example.transfer_test.service;

import com.example.transfer_test.model.account.Account;
import com.example.transfer_test.model.account.AccountType;
import com.example.transfer_test.model.request.TransferRequest;
import com.example.transfer_test.repository.AccountRepository;
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

    // возвращает optional-аккаунт отправителя,
    // проверяет, есть ли аккаунт с таким номером карты в репозитории, если есть - смотрит, точно ли он
    // внутреннего (AccountType.INTERNAL) типа (т.к. только у таких аккаунтов есть баланс и остальные данные), сверяет данные,
    // возвращает только заранее записанные в тестовых целях аккаунты
    public Optional<Account> getSenderAccount(TransferRequest transferRequest) {
        final String cardFromNumber = transferRequest.getCardFromNumber();
        final String cardFromCvv = transferRequest.getCardFromCVV();
        final YearMonth cardFromValidTill = transferRequest.getCardFromValidTill();
        return accountRepository.getByCardNumber(cardFromNumber)
                .filter(account -> account.getAccountType() == AccountType.INTERNAL
                        && account.getCvv().equals(cardFromCvv)
                        && account.getValidThruTill().equals(cardFromValidTill));
    }

    // возвращает аккаунт получателя
    // если это заранее записанный аккаунт (AccountType.INTERNAL) - вернет его, и соответственно будет вестись
    // учет баланса дальше в других методах
    // если зарегистрированного аккаунта нет - то вернет (сначала создаст методом ниже , т.к. я их не добавлял)
    // внешний аккаунт AccountType.EXTERNAL - у которого есть только номер карты и с которого отправлять нельзя
    public Account getReceiverAccount(String cardToNumber) {
        return accountRepository.getByCardNumber(cardToNumber)
                .orElse(addNewExternalAccount(cardToNumber));
    }

    // "регистрирует аккаунт", нужен больше для тестов, т.к. фронт пока это сделать не позволяет
    // добавляет аккаунт AccountType.INTERNAL типа
    public Account addNewInternalAccount(String cardNumber, String cvv, YearMonth validThruTill, BigDecimal balance) {
        final Account account = new Account(cardNumber, AccountType.INTERNAL, cvv, validThruTill, balance);
        accountRepository.save(account);
        return account;
    }

    // создает и сохраняет внешние аккаунты
    public Account addNewExternalAccount(String cardNumber) {
        final Account account = new Account(cardNumber, AccountType.EXTERNAL, null, null, null);
        accountRepository.save(account);
        return account;
    }
}
