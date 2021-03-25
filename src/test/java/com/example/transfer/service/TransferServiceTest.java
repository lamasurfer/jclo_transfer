package com.example.transfer.service;

import com.example.transfer.exception.AccountException;
import com.example.transfer.exception.TransactionException;
import com.example.transfer.model.account.Account;
import com.example.transfer.model.account.AccountType;
import com.example.transfer.model.request.Amount;
import com.example.transfer.model.request.ConfirmationRequest;
import com.example.transfer.model.request.TransferRequest;
import com.example.transfer.model.response.SuccessMessage;
import com.example.transfer.model.transaction.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountService accountService;
    @Mock
    private TransactionService transactionService;
    @Mock
    private VerificationService verificationService;
    @Mock
    private MessageSourceAccessor messages;
    @InjectMocks
    private TransferService transferService;

    @Test
    void test_transfer_validRequest_expectedBehaviour() {
        final TransferRequest transferRequest = new TransferRequest(
                "4111111111111111",
                YearMonth.now(),
                "343",
                "5500000000000004",
                new Amount(new BigDecimal("1000.00"), "RUR"));

        final Account accountFrom = new Account();
        when(accountService.getSenderAccount(transferRequest)).thenReturn(Optional.of(accountFrom));

        final Account accountToInternal = new Account();
        when(accountService.getReceiverAccount(transferRequest.getCardToNumber())).thenReturn(accountToInternal);

        final BigDecimal amount = transferRequest.getAmount().getValue();

        final Transaction transaction = new Transaction();
        final String operationId = "1";
        transaction.setOperationId(operationId);

        when(transactionService.getTransaction(accountFrom, accountToInternal, amount))
                .thenReturn(Optional.of(transaction));

        assertEquals(ResponseEntity.ok().body(new SuccessMessage(operationId)).toString(),
                transferService.transfer(transferRequest).toString());

    }

    @Test
    void test_transfer_ifNotEnoughFunds_throwsAccountException() {
        final TransferRequest requestedTooMuch = new TransferRequest(
                "4111111111111111",
                YearMonth.now(),
                "343",
                "5500000000000004",
                new Amount(new BigDecimal("100000000.00"), "RUR") // too much
        );

        final Account accountFrom = new Account(
                "4111111111111111",
                AccountType.INTERNAL,
                "343",
                YearMonth.now(),
                new BigDecimal("10000.00"));

        final BigDecimal amount = requestedTooMuch.getAmount().getValue();
        when(accountService.getSenderAccount(requestedTooMuch)).thenReturn(Optional.of(accountFrom));

        final Account accountTo = new Account();
        when(accountService.getReceiverAccount(requestedTooMuch.getCardToNumber())).thenReturn(accountTo);
        when(transactionService.getTransaction(accountFrom, accountTo, amount)).thenReturn(Optional.empty());

        assertThrows(AccountException.class, () -> transferService.transfer(requestedTooMuch));
    }

    @Test
    void test_transfer_ifWrongAccountData_throwsAccountException() {
        final TransferRequest transferRequest = new TransferRequest();

        when(accountService.getSenderAccount(transferRequest)).thenReturn(Optional.empty());
        assertThrows(AccountException.class, () -> transferService.transfer(transferRequest));
    }

    @Test
    void test_confirmOperation_expectedBehaviour() {
        final String operationId = "1";
        final String code = "0000";
        final ConfirmationRequest confirmationRequest = new ConfirmationRequest(operationId, code);

        final Transaction transaction = new Transaction();
        transaction.setOperationId(operationId);
        transaction.setVerificationCode(code);

        when(transactionService.getById(operationId)).thenReturn(Optional.of(transaction));
        when(verificationService.verifyTransaction(transaction)).thenReturn(true);

        assertEquals(ResponseEntity.ok().body(new SuccessMessage(operationId)).toString(),
                transferService.confirmOperation(confirmationRequest).toString());

    }

    @Test
    void test_confirmOperation_ifWrongId_throwsTransactionException() {
        final String operationId = "wrongId";
        final String code = "0000";
        final ConfirmationRequest confirmationRequest = new ConfirmationRequest(operationId, code);

        when(transactionService.getById(operationId)).thenReturn(Optional.empty());

        assertThrows(TransactionException.class, () -> transferService.confirmOperation(confirmationRequest));
    }

    @Test
    void test_confirmOperation_ifWrongCode_throwsTransactionException() {
        final String operationId = "1";
        final String code = "1111";
        final ConfirmationRequest confirmationRequest = new ConfirmationRequest(operationId, code);

        final Transaction transaction = new Transaction();
        transaction.setOperationId(operationId);
        transaction.setVerificationCode(code);

        when(transactionService.getById(operationId)).thenReturn(Optional.of(transaction));
        when(verificationService.verifyTransaction(transaction)).thenReturn(false);

        assertThrows(TransactionException.class, () -> transferService.confirmOperation(confirmationRequest));
    }
}