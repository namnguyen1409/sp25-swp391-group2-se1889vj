package com.group2.sp25swp391group2se1889vj.validation.validator;

import com.group2.sp25swp391group2se1889vj.validation.annotation.PhoneUnique;
import com.group2.sp25swp391group2se1889vj.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PhoneUniqueValidator implements ConstraintValidator<PhoneUnique, String> {

    private UserRepository userRepo;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isExist = userRepo.existsByPhone(value);
        return !isExist;
    }
}
