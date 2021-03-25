package com.example.transfer.model.response;

public class SuccessMessage {
    private String operationId;

    public SuccessMessage() {
    }

    public SuccessMessage(String operationId) {
        this.operationId = operationId;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    @Override
    public String toString() {
        return "SuccessMessage{" +
                "operationId='" + operationId + '\'' +
                '}';
    }
}
