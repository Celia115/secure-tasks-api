package com.celia.securetasksapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoInjectionLikeContentValidator implements ConstraintValidator<NoInjectionLikeContent, String> {

    private static final String[] BLOCKED_PATTERNS = {
            "<script", "javascript:", "onerror=", "onload=",
            "--", "/*", "*/", ";drop", " union ", " select ",
            " or 1=1", " xp_", " exec "
    };

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String normalized = value.toLowerCase().trim();

        for (String pattern : BLOCKED_PATTERNS) {
            if (normalized.contains(pattern)) {
                return false;
            }
        }

        return true;
    }
}