package com.example.transfer.exception;

public class AccountException extends RuntimeException {

    public AccountException() {
    }

    public AccountException(String message) {
        super(message);
    }
}
