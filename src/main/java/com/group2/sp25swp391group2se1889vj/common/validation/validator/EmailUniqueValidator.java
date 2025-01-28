package com.group2.sp25swp391group2se1889vj.common.validation.validator;

import com.group2.sp25swp391group2se1889vj.common.validation.annotation.EmailUnique;
import com.group2.sp25swp391group2se1889vj.user.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmailUniqueValidator implements ConstraintValidator<EmailUnique, String> {

    private UserRepository userRepo;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isExist = userRepo.existsByEmail(value);
        return !isExist;
    }
}
