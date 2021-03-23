package com.example.transfer_test.service;

import com.example.transfer_test.model.transaction.Transaction;
import org.springframework.stereotype.Service;

@Service
public class VerificationService {
    // код из фронта всегда 0000, поэтому тут нет репозитория с картами - кодами
    private static final String VERIFICATION_CODE = "0000";

    // устанавливает код, тут должно быть все сложно с отправкой на телефон...)
    public String setVerificationCode(Transaction transaction) {
        transaction.setVerificationCode(VERIFICATION_CODE);
        return transaction.getVerificationCode();
    }
    // проверяет код
    public boolean verifyTransaction(Transaction transaction) {
        return VERIFICATION_CODE.equals(transaction.getVerificationCode());
    }
}
