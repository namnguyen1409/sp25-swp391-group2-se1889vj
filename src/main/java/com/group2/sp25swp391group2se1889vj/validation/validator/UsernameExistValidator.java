package com.group2.sp25swp391group2se1889vj.validation.validator;

import com.group2.sp25swp391group2se1889vj.validation.annotation.UsernameExist;
import com.group2.sp25swp391group2se1889vj.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class UsernameExistValidator implements ConstraintValidator<UsernameExist, String> {
    private final UserService userService;

    public UsernameExistValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        return userService.findUserByUsername(username) != null;
    }
}
