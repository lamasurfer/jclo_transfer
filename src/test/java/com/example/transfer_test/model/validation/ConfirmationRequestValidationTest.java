package com.example.transfer_test.model.validation;

import com.example.transfer_test.model.request.ConfirmationRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@SpringBootTest
public class ConfirmationRequestValidationTest {

    @Autowired
    private Validator validator;

    @Test
    void test_confirmationRequest_validRequest_noViolations() {
        final ConfirmationRequest request = new ConfirmationRequest("1", "0000");
        Set<ConstraintViolation<ConfirmationRequest>> violations = validator.validate(request);
        Assertions.assertEquals(0, violations.size());

    }

    @Test
    void test_confirmationRequest_nullId_expectedOneViolation() {
        final ConfirmationRequest request = new ConfirmationRequest(null, "0000");
        Set<ConstraintViolation<ConfirmationRequest>> violations = validator.validate(request);
        Assertions.assertEquals(1, violations.size());
    }

    @Test
    void test_confirmationRequest_emptyId_expectedOneViolation() {
        final ConfirmationRequest request = new ConfirmationRequest("", "0000");
        Set<ConstraintViolation<ConfirmationRequest>> violations = validator.validate(request);
        Assertions.assertEquals(1, violations.size());
    }

    @Test
    void test_confirmationRequest_blankId_expectedOneViolation() {
        final ConfirmationRequest request = new ConfirmationRequest("  ", "0000");
        Set<ConstraintViolation<ConfirmationRequest>> violations = validator.validate(request);
        Assertions.assertEquals(1, violations.size());
    }

    @Test
    void test_confirmationRequest_nullCode_expectedOneViolation() {
        final ConfirmationRequest request = new ConfirmationRequest("1", null);
        Set<ConstraintViolation<ConfirmationRequest>> violations = validator.validate(request);
        Assertions.assertEquals(1, violations.size());
    }

    @Test
    void test_confirmationRequest_emptyCode_expectedOneViolation() {
        final ConfirmationRequest request = new ConfirmationRequest("1", "");
        Set<ConstraintViolation<ConfirmationRequest>> violations = validator.validate(request);
        Assertions.assertEquals(1, violations.size());
    }

    @Test
    void test_confirmationRequest_blankCode_expectedOneViolation() {
        final ConfirmationRequest request = new ConfirmationRequest("1", "  ");
        Set<ConstraintViolation<ConfirmationRequest>> violations = validator.validate(request);
        Assertions.assertEquals(1, violations.size());
    }

    @Test
    void test_confirmationRequest_digitCode_noViolation() {
        final ConfirmationRequest request = new ConfirmationRequest("1", "546755");
        Set<ConstraintViolation<ConfirmationRequest>> violations = validator.validate(request);
        Assertions.assertEquals(0, violations.size());
    }

    @Test
    void test_confirmationRequest_nonDigitCode_expectedOneViolation() {
        final ConfirmationRequest request = new ConfirmationRequest("1", "c11od&*#@@e");
        Set<ConstraintViolation<ConfirmationRequest>> violations = validator.validate(request);
        Assertions.assertEquals(1, violations.size());
    }


}
