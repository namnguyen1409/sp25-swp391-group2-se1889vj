package com.group2.sp25swp391group2se1889vj.validation.validator;

import com.group2.sp25swp391group2se1889vj.validation.annotation.UsernameUnique;
import com.group2.sp25swp391group2se1889vj.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UsernameUniqueValidator implements ConstraintValidator<UsernameUnique, String> {

    private UserRepository userRepo;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isExist = userRepo.existsByUsername(value);
        return !isExist;
    }
}
