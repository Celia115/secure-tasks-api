package com.celia.securetasksapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NoInjectionLikeContentValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface NoInjectionLikeContent {
    String message() default "El texto contiene patrones no permitidos";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}