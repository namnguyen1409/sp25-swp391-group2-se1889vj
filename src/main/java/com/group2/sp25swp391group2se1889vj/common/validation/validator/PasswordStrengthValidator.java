package com.group2.sp25swp391group2se1889vj.common.validation.validator;

import com.group2.sp25swp391group2se1889vj.common.validation.annotation.PasswordStrength;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordStrengthValidator implements ConstraintValidator<PasswordStrength, String> {
    private static final double LENGTH_WEIGHT = 0.2;
    private static final double UPPERCASE_WEIGHT = 0.5;
    private static final double LOWERCASE_WEIGHT = 0.5;
    private static final double NUMBER_WEIGHT = 0.7;
    private static final double SYMBOL_WEIGHT = 1.0;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return calculatePasswordStrength(value) >= 3;
    }

    private static double calculatePasswordStrength(String password) {
        double strength = 0;

        strength += password.length() * LENGTH_WEIGHT;

        if (password.matches(".*[A-Z].*")) {
            strength += UPPERCASE_WEIGHT;
        } else {
            strength -= UPPERCASE_WEIGHT;
        }

        if (password.matches(".*[a-z].*")) {
            strength += LOWERCASE_WEIGHT;
        } else {
            strength -= LOWERCASE_WEIGHT;
        }

        if (password.matches(".*\\d.*")) {
            strength += NUMBER_WEIGHT;
        } else {
            strength -= NUMBER_WEIGHT;
        }

        if (password.matches(".*[^A-Za-z0-9].*")) {
            strength += SYMBOL_WEIGHT;
        } else {
            strength -= SYMBOL_WEIGHT;
        }

        if (password.length() < 8) {
            strength = 0;
        }

        return strength;
    }
}
