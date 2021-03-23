package com.example.transfer_test.exception;

public class AccountException extends RuntimeException {

    public AccountException() {
    }

    public AccountException(String message) {
        super(message);
    }
}
