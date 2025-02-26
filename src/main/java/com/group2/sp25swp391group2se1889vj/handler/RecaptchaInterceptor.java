package com.group2.sp25swp391group2se1889vj.handler;

import com.group2.sp25swp391group2se1889vj.exception.Http400;
import com.group2.sp25swp391group2se1889vj.security.RecaptchaService;
import com.group2.sp25swp391group2se1889vj.validation.annotation.RecaptchaRequired;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
public class RecaptchaInterceptor implements HandlerInterceptor {
    private final RecaptchaService recaptchaService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            var recaptchaRequired = handlerMethod.getMethod().getAnnotation(RecaptchaRequired.class);
            if (recaptchaRequired != null) {
                var recaptchaResponse = request.getParameter("g-recaptcha-response");
                if (recaptchaResponse == null || !recaptchaService.verifyRecaptcha(recaptchaResponse)) {
                    throw new Http400("Vui lòng xác minh bạn không phải là robot");
                }
            }
        }
        return true;
    }
}
