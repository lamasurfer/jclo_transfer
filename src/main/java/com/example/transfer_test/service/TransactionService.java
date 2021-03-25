package com.example.transfer_test.service;

import com.example.transfer_test.model.account.Account;
import com.example.transfer_test.model.account.AccountType;
import com.example.transfer_test.model.transaction.Transaction;
import com.example.transfer_test.model.transaction.TransactionStatus;
import com.example.transfer_test.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {

    private static final RoundingMode BANKERS = RoundingMode.HALF_EVEN;
    private static final int SCALE = 2;

    private static final BigDecimal TRANSFER_FEE = new BigDecimal("0.01");

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public Optional<Transaction> getTransaction(Account accountFrom, Account accountTo, BigDecimal amount) {
        final BigDecimal totalAmount = calculateTotalAmount(amount);
        return isEnoughFunds(accountFrom, totalAmount)
                ? Optional.of(createTransaction(accountFrom, accountTo, amount, totalAmount))
                : Optional.empty();
    }

    public Transaction createTransaction(Account accountFrom, Account accountTo, BigDecimal amount, BigDecimal totalAmount) {
        final Transaction transaction = new Transaction(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                accountFrom,
                accountTo,
                amount,
                TRANSFER_FEE,
                totalAmount,
                TransactionStatus.REQUESTED,
                LocalDateTime.MIN,
                null);
        repository.save(transaction);
        return transaction;
    }

    public Transaction processTransaction(Transaction transaction) {
        final Account accountFrom = transaction.getAccountFrom();
        final Account accountTo = transaction.getAccountTo();
        final BigDecimal totalAmount = transaction.getTotalAmount();
        if (accountTo.getAccountType() == AccountType.INTERNAL) {
            final BigDecimal amount = transaction.getAmount();
            accountTo.setBalance(accountTo.getBalance().add(amount));
        }
        accountFrom.setBalance(accountFrom.getBalance().subtract(totalAmount));
        transaction.setTransactionStatus(TransactionStatus.PROCESSED);
        transaction.setProcessedTime(LocalDateTime.now());
        return transaction;
    }

    public Optional<Transaction> getById(String operationId) {
        return repository.getById(operationId);
    }

    public BigDecimal calculateTotalAmount(BigDecimal amount) {
        final BigDecimal multiplicand = TRANSFER_FEE.add(BigDecimal.ONE);
        return amount.multiply(multiplicand).setScale(SCALE, BANKERS);
    }

    public boolean isEnoughFunds(Account accountFrom, BigDecimal totalAmount) {
        return accountFrom.getBalance().compareTo(totalAmount) >= 0;
    }
}
