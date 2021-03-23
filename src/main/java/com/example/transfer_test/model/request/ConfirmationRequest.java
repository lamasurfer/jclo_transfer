package com.example.transfer_test.model.request;

import com.example.transfer_test.model.validation.ValidConfirmationRequest;

import java.util.Objects;

@ValidConfirmationRequest
public class ConfirmationRequest {
    private String operationId;
    private String code;

    public ConfirmationRequest() {
    }

    public ConfirmationRequest(String operationId, String code) {
        this.operationId = operationId;
        this.code = code;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfirmationRequest that = (ConfirmationRequest) o;
        return Objects.equals(operationId, that.operationId) && Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operationId, code);
    }

    @Override
    public String toString() {
        return "ConfirmationRequest{" +
                "operationId='" + operationId + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
