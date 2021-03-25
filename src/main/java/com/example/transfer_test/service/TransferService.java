package com.example.transfer_test.service;

import com.example.transfer_test.exception.AccountException;
import com.example.transfer_test.exception.TransactionException;
import com.example.transfer_test.model.account.Account;
import com.example.transfer_test.model.request.ConfirmationRequest;
import com.example.transfer_test.model.request.TransferRequest;
import com.example.transfer_test.model.response.SuccessMessage;
import com.example.transfer_test.model.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransferService {

    private static final Logger LOGGER = LoggerFactory.getLogger("transaction_logger");

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final VerificationService verificationService;
    private final MessageSourceAccessor messages;

    public TransferService(TransactionService transactionService,
                           AccountService accountService,
                           VerificationService verificationService,
                           MessageSourceAccessor messages) {
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.verificationService = verificationService;
        this.messages = messages;
    }

    public ResponseEntity<Object> transfer(TransferRequest transferRequest) {
        final Account accountFrom = accountService.getSenderAccount(transferRequest)
                .orElseThrow(() -> new AccountException(messages.getMessage("wrong.sender.data")));

        final Account accountTo = accountService.getReceiverAccount(transferRequest.getCardToNumber());

        final BigDecimal amount = transferRequest.getAmount().getValue();

        final Transaction transaction = transactionService.getTransaction(accountFrom, accountTo, amount)
                .orElseThrow(() -> new AccountException(messages.getMessage("insufficient.funds")));

        verificationService.setVerificationCode(transaction);
        LOGGER.info(transaction.toString());
        return ResponseEntity.ok().body(new SuccessMessage(transaction.getOperationId()));
    }

    public ResponseEntity<Object> confirmOperation(ConfirmationRequest confirmationRequest) {
        final Transaction transaction = transactionService.getById(confirmationRequest.getOperationId())
                .orElseThrow(() -> new TransactionException(messages.getMessage("transaction.not.found")));

        if (!verificationService.verifyTransaction(transaction)) {
            throw new TransactionException(messages.getMessage("invalid.transaction.code"));
        }

        transactionService.processTransaction(transaction);
        LOGGER.info(transaction.toString());
        return ResponseEntity.ok().body(new SuccessMessage(transaction.getOperationId()));
    }
}
