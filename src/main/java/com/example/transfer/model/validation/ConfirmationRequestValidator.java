package com.example.transfer.model.validation;

import com.example.transfer.model.request.ConfirmationRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class ConfirmationRequestValidator implements ConstraintValidator<ValidConfirmationRequest, ConfirmationRequest> {

    private static final Pattern NOT_DIGITS = Pattern.compile("\\D");

    @Override
    public void initialize(ValidConfirmationRequest constraintAnnotation) {
    }

    @Override
    public boolean isValid(ConfirmationRequest request, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (request == null) {
            context.buildConstraintViolationWithTemplate("{object.is.invalid}")
                    .addConstraintViolation();
            return false;
        }

        int counter = 0;

        final String operationId = request.getOperationId();
        if (operationId == null || operationId.isBlank()) {
            context.buildConstraintViolationWithTemplate("{operationId.is.invalid}")
                    .addConstraintViolation();
            counter--;
        }

        final String code = request.getCode();
        if (code == null || code.isBlank() || NOT_DIGITS.matcher(code).find()) {
            context.buildConstraintViolationWithTemplate("{code.is.invalid}")
                    .addConstraintViolation();
            counter--;
        }

        return counter == 0;
    }
}
