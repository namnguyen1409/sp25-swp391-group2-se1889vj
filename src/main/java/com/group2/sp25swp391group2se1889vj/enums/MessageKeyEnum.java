package com.group2.sp25swp391group2se1889vj.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageKeyEnum {
    ERROR_FILE_SIZE_EXCEEDS_LIMIT("error.file.size.exceeds.limit"),
    ERROR_FILE_IS_NOT_IMAGE("error.file.is.not.image"),
    ERROR_USERNAME_NOT_FOUND("error.username.not.found"),
    ERROR_RECAPTCHA("error.recaptcha"),
    ERROR_LOGIN_PASSWORD("error.login.password"),
    ERROR_REGISTER_TOKEN_INVALID("error.register.token.invalid"),
    ERROR_REGISTER_TOKEN_EXPIRED("error.register.token.expired"),
    ERROR_CHANGE_PASSWORD_OLD("error.change.password.old"),
    SUCCESS_FILE_UPLOADED("success.file.uploaded"),
    SUCCESS_UPDATE_AVATAR("success.update.avatar"),
    SUCCESS_CHANGE_PASSWORD("success.change.password"),



    ;

    private final String key;
}
