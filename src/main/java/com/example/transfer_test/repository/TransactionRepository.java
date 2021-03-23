package com.example.transfer_test.repository;

import com.example.transfer_test.model.transaction.Transaction;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TransactionRepository {

    private final Map<String, Transaction> transactions = new ConcurrentHashMap<>();

    public Transaction save(Transaction transaction) {
        transactions.put(transaction.getOperationId(), transaction);
        return transaction;
    }

    public Optional<Transaction> getById(String operationId) {
        return Optional.ofNullable(transactions.get(operationId));
    }

    public List<Transaction> getAll() {
        return new ArrayList<>(transactions.values());
    }

    public void removeById(String operationId) {
        transactions.remove(operationId);
    }

}
