package com.example.transfer_test.repository;

import com.example.transfer_test.model.account.Account;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    public Account save(Account account) {
        accounts.put(account.getCardNumber(), account);
        return account;
    }

    public Optional<Account> getByCardNumber(String cardNumber) {
        return Optional.ofNullable(accounts.get(cardNumber));
    }

    public List<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }

    public void removeByCardNumber(String cardNumber) {
        accounts.remove(cardNumber);
    }
}
