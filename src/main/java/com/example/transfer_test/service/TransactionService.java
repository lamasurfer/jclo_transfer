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

    // проверяет методом ниже что средств для перевода с комиссией достаточно
    // возвращает optional-транзакцию или пустой optional
    // для создания транзакции использует метод дальше
    public Optional<Transaction> getTransaction(Account accountFrom, Account accountTo, BigDecimal amount) {
        final BigDecimal totalAmount = calculateTotalAmount(amount);
        return isEnoughFunds(accountFrom, totalAmount)
                ? Optional.of(createTransaction(accountFrom, accountTo, amount, totalAmount))
                : Optional.empty();
    }

    // создает, сохраняет в репозиторий и возвращает новую транзакцию
    // operationId - randomUUID
    // статус транзакции до подтверждения - TransactionStatus.REQUESTED, время - время при создании
    // транзакция еще не подтверждена поэтому время подтверждения - LocalDateTime.MIN
    // код null - его выставит VerificationService позже
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

    // проводит транзакцию, списывает средства с аккаунта отправителя
    // если получатель - зарегистрированный аккаунт - добавит ему средства
    // выставляет статус TransactionStatus.PROCESSED и время проведения вместо LocalDateTime.MIN
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

    // возвращает транзакцию по id если такая есть
    public Optional<Transaction> getById(String operationId) {
        return repository.getById(operationId);
    }

    // рассчитывает полную стоимость перевода с комиссией
    public BigDecimal calculateTotalAmount(BigDecimal amount) {
        final BigDecimal multiplicand = TRANSFER_FEE.add(BigDecimal.ONE);
        return amount.multiply(multiplicand).setScale(SCALE, BANKERS);
    }

    // проверяет, достаточно ли средств для перевода с учетом комиссии
    public boolean isEnoughFunds(Account accountFrom, BigDecimal totalAmount) {
        return accountFrom.getBalance().compareTo(totalAmount) >= 0;
    }
}
