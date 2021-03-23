package com.example.transfer_test;

import com.example.transfer_test.controller.TransferController;
import com.example.transfer_test.repository.AccountRepository;
import com.example.transfer_test.repository.TransactionRepository;
import com.example.transfer_test.service.AccountService;
import com.example.transfer_test.service.TransactionService;
import com.example.transfer_test.service.TransferService;
import com.example.transfer_test.service.VerificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.MessageSourceAccessor;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class TransferTestApplicationTests {

    @Autowired
    private TransferController transferController;

    @Autowired
    private TransferService transferService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private MessageSourceAccessor messageSourceAccessor;

    @Test
    void test_contextLoads_expectedBehaviour() {
        assertNotNull(transferController);
        assertNotNull(transferService);
        assertNotNull(transactionService);
        assertNotNull(transactionRepository);
        assertNotNull(accountService);
        assertNotNull(accountRepository);
        assertNotNull(verificationService);
        assertNotNull(messageSourceAccessor);
    }
}
