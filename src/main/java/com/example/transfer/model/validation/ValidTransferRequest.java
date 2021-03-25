package com.example.transfer.model.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TransferRequestValidator.class)
@Documented
public @interface ValidTransferRequest {

    String message() default "{default.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
