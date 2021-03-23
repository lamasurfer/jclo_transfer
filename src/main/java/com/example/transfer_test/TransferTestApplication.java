package com.example.transfer_test;

import com.example.transfer_test.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.YearMonth;

@SpringBootApplication
public class TransferTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransferTestApplication.class, args);
    }

    @Bean
    CommandLineRunner addAccounts(AccountService accountService) {
        return args -> {
            accountService.addNewInternalAccount("4111111111111111", "343", YearMonth.now(), new BigDecimal("100000.00"));
            accountService.addNewInternalAccount("5500000000000004", "543", YearMonth.now(), new BigDecimal("10000.00"));
            accountService.addNewInternalAccount("5555555555554444", "123", YearMonth.now(), new BigDecimal("0.00"));
            accountService.addNewInternalAccount("4242424242424242", "424", YearMonth.now(), new BigDecimal("1000000.00"));
        };
    }
}
