package com.example.transfer_test.model.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ConfirmationRequestValidator.class)
@Documented
public @interface ValidConfirmationRequest {

    String message() default "{default.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
