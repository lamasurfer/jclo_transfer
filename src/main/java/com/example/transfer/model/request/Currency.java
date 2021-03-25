package com.example.transfer.model.request;

public enum Currency {
    RUR("RUR");
//    USD("USD");

    private final String currency;

    Currency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return currency;
    }
}
