package com.example.transfer.service;

import com.example.transfer.model.transaction.Transaction;
import org.springframework.stereotype.Service;

@Service
public class VerificationService {

    private static final String VERIFICATION_CODE = "0000";

    public String setVerificationCode(Transaction transaction) {
        transaction.setVerificationCode(VERIFICATION_CODE);
        return transaction.getVerificationCode();
    }

    public boolean verifyTransaction(Transaction transaction) {
        return VERIFICATION_CODE.equals(transaction.getVerificationCode());
    }
}
