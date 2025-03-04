package com.group2.sp25swp391group2se1889vj.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD}) // đánh dấu annotation này chỉ được sử dụng trên method
@Retention(RetentionPolicy.RUNTIME) // đánh dấu annotation này sẽ được giữ lại ở runtime
public @interface RecaptchaRequired {

}
