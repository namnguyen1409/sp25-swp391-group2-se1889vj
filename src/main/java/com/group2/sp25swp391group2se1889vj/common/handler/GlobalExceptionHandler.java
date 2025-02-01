package com.group2.sp25swp391group2se1889vj.common.handler;

import com.group2.sp25swp391group2se1889vj.auth.security.CustomUserDetails;
import com.group2.sp25swp391group2se1889vj.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUser();
        }
        return null;
    }


    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleHttp400(Model model) {
        model.addAttribute("currentUser", getCurrentUser());
        model.addAttribute("error",
                new Error("400",
                        "Lỗi yêu cầu",
                        "Xin lỗi, yêu cầu của bạn không hợp lệ."));
        return "common/error";
    }



    @ExceptionHandler({NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleHttp404(Model model) {
        model.addAttribute("currentUser", getCurrentUser());
        model.addAttribute("error",
                new Error("404",
                        "Ôi không tìm thấy trang này",
                        "Xin lỗi, trang bạn đang tìm kiếm không tồn tại hoặc đã bị xóa."));
        return "common/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception exception,
                                         Model model) {

        model.addAttribute("currentUser", getCurrentUser());
        model.addAttribute("error",
                new Error("500",
                        "Lỗi hệ thống",
                        "Xin lỗi, đã xảy ra lỗi hệ thống. Vui lòng thử lại sau."));
        System.out.println(exception.getMessage());
        return "common/error";
    }

    @Data
    @AllArgsConstructor
    static class Error {
        private String code;
        private String title;
        private String message;

    }

}