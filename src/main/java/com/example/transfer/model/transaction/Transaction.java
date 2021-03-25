package com.example.transfer.model.transaction;

import com.example.transfer.model.account.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction {

    private String operationId;
    private LocalDateTime requestedTime;
    private Account accountFrom;
    private Account accountTo;
    private BigDecimal amount;
    private BigDecimal transferFee;
    private BigDecimal totalAmount;
    private TransactionStatus transactionStatus;
    private LocalDateTime processedTime;
    private String verificationCode;

    public Transaction() {
    }

    public Transaction(String operationId,
                       LocalDateTime requestedTime,
                       Account accountFrom,
                       Account accountTo,
                       BigDecimal amount,
                       BigDecimal transferFee,
                       BigDecimal totalAmount,
                       TransactionStatus transactionStatus,
                       LocalDateTime processedTime,
                       String verificationCode) {
        this.operationId = operationId;
        this.requestedTime = requestedTime;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
        this.transferFee = transferFee;
        this.totalAmount = totalAmount;
        this.transactionStatus = transactionStatus;
        this.processedTime = processedTime;
        this.verificationCode = verificationCode;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public LocalDateTime getRequestedTime() {
        return requestedTime;
    }

    public void setRequestedTime(LocalDateTime requestedTime) {
        this.requestedTime = requestedTime;
    }

    public Account getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(Account accountFrom) {
        this.accountFrom = accountFrom;
    }

    public Account getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(Account accountTo) {
        this.accountTo = accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getTransferFee() {
        return transferFee;
    }

    public void setTransferFee(BigDecimal transferFee) {
        this.transferFee = transferFee;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public LocalDateTime getProcessedTime() {
        return processedTime;
    }

    public void setProcessedTime(LocalDateTime processedTime) {
        this.processedTime = processedTime;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(operationId, that.operationId)
                && Objects.equals(requestedTime, that.requestedTime)
                && Objects.equals(accountFrom, that.accountFrom)
                && Objects.equals(accountTo, that.accountTo)
                && Objects.equals(amount, that.amount)
                && Objects.equals(transferFee, that.transferFee)
                && Objects.equals(totalAmount, that.totalAmount)
                && transactionStatus == that.transactionStatus
                && Objects.equals(processedTime, that.processedTime)
                && Objects.equals(verificationCode, that.verificationCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operationId, requestedTime, accountFrom, accountTo, amount, transferFee, totalAmount,
                transactionStatus, processedTime, verificationCode);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "operationId='" + operationId + '\'' +
                ", requestedTime=" + requestedTime +
                ", accountFrom=" + accountFrom +
                ", accountTo=" + accountTo +
                ", amount=" + amount +
                ", transferFee=" + transferFee +
                ", totalAmount=" + totalAmount +
                ", transactionStatus=" + transactionStatus +
                ", processedTime=" + processedTime +
                ", verificationCode='" + verificationCode + '\'' +
                '}';
    }
}
