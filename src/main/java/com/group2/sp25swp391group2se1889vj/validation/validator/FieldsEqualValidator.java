package com.group2.sp25swp391group2se1889vj.validation.validator;

import com.group2.sp25swp391group2se1889vj.validation.annotation.FieldsEqual;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class FieldsEqualValidator implements ConstraintValidator<FieldsEqual, Object> {

    private String field1;
    private String field2;
    private String message;

    @Override
    public void initialize(FieldsEqual constraintAnnotation) {
        this.field1 = constraintAnnotation.field1();
        this.field2 = constraintAnnotation.field2();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Field field1Value = value.getClass().getDeclaredField(field1);
            Field field2Value = value.getClass().getDeclaredField(field2);
            field1Value.setAccessible(true);
            field2Value.setAccessible(true);
            Object field1Data = field1Value.get(value);
            Object field2Data = field2Value.get(value);

            boolean valid = field1Data != null && field1Data.equals(field2Data);
            if (!valid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                        .addPropertyNode(field2)
                        .addConstraintViolation();
            }
            return valid;
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            return false;
        }
    }

}
