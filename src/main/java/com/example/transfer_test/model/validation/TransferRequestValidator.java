package com.example.transfer_test.model.validation;

import com.example.transfer_test.model.request.Amount;
import com.example.transfer_test.model.request.Currency;
import com.example.transfer_test.model.request.TransferRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TransferRequestValidator implements ConstraintValidator<ValidTransferRequest, TransferRequest> {

    private static final Pattern NOT_DIGITS = Pattern.compile("\\D");
    private static final Pattern CVV_PATTERN = Pattern.compile("\\d{3}");
    private static final Set<String> CURRENCIES = Arrays.stream(Currency.values())
            .map(Currency::toString)
            .collect(Collectors.toSet());

    @Override
    public void initialize(ValidTransferRequest constraintAnnotation) {
    }

    @Override
    public boolean isValid(TransferRequest request, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (request == null) {
            context.buildConstraintViolationWithTemplate("{object.is.invalid}")
                    .addConstraintViolation();
            return false;
        }

        int counter = 0;

        final String cardFrom = request.getCardFromNumber();
        if (cardFrom == null || cardFrom.isBlank() || NOT_DIGITS.matcher(cardFrom).find() || !checkNumber(cardFrom)) {
            context.buildConstraintViolationWithTemplate("{card.sender.number.is.invalid}")
                    .addConstraintViolation();
            counter--;
        }

        final String cardTo = request.getCardToNumber();
        if (cardTo == null || cardTo.isBlank() || NOT_DIGITS.matcher(cardTo).find() || !checkNumber(cardTo)) {
            context.buildConstraintViolationWithTemplate("{card.receiver.number.is.invalid}")
                    .addConstraintViolation();
            counter--;
        }

        if (counter == 0 && cardFrom.equals(cardTo)) {
            context.buildConstraintViolationWithTemplate("{same.card.same.numbers.message}")
                    .addConstraintViolation();
            counter--;
        }

        final YearMonth cardFromValidTill = request.getCardFromValidTill();
        if (cardFromValidTill == null) {
            context.buildConstraintViolationWithTemplate("{card.expiry.is.invalid}")
                    .addConstraintViolation();
            counter--;
        } else if (cardFromValidTill.isBefore(YearMonth.now())) {
            context.buildConstraintViolationWithTemplate("{card.expired}")
                    .addConstraintViolation();
            counter--;
        }

        final String cvv = request.getCardFromCVV();
        if (cvv == null || !CVV_PATTERN.matcher(cvv).matches()) {
            context.buildConstraintViolationWithTemplate("{card.cvv.is.invalid}")
                    .addConstraintViolation();
            counter--;
        }

        final Amount amount = request.getAmount();
        if (amount == null) {
            context.buildConstraintViolationWithTemplate("{amount.is.blank}")
                    .addConstraintViolation();
            return false;
        }

        final BigDecimal value = amount.getValue();
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            context.buildConstraintViolationWithTemplate("{amount.is.invalid}")
                    .addConstraintViolation();
            counter--;
        }

        final String currency = amount.getCurrency();
        if (currency == null || currency.isBlank() || !CURRENCIES.contains(amount.getCurrency())) {
            context.buildConstraintViolationWithTemplate("{currency.is.invalid}")
                    .addConstraintViolation();
            return false;
        }
        return counter == 0;
    }

    private static boolean checkNumber(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }
}
