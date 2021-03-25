package com.example.transfer.model.account;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Objects;

public class Account {
    private String cardNumber;
    private AccountType accountType;
    private String cvv;
    private YearMonth validThruTill;
    private BigDecimal balance;

    public Account() {
    }

    public Account(String cardNumber, AccountType accountType, String cvv, YearMonth validThruTill, BigDecimal balance) {
        this.cardNumber = cardNumber;
        this.accountType = accountType;
        this.cvv = cvv;
        this.validThruTill = validThruTill;
        this.balance = balance;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public YearMonth getValidThruTill() {
        return validThruTill;
    }

    public void setValidThruTill(YearMonth validThruTill) {
        this.validThruTill = validThruTill;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(cardNumber, account.cardNumber)
                && accountType == account.accountType
                && Objects.equals(cvv, account.cvv)
                && Objects.equals(validThruTill, account.validThruTill)
                && Objects.equals(balance, account.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber, accountType, cvv, validThruTill, balance);
    }

    @Override
    public String toString() {
        return "Account{" +
                "cardNumber='" + cardNumber + '\'' +
                ", accountType=" + accountType +
                ", cvv='" + cvv + '\'' +
                ", validThruTill=" + validThruTill +
                ", balance=" + balance +
                '}';
    }
}
