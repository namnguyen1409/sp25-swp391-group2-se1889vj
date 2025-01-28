package com.group2.sp25swp391group2se1889vj.common.controller;


import com.group2.sp25swp391group2se1889vj.auth.security.CustomUserDetails;
import com.group2.sp25swp391group2se1889vj.user.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private User getUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }

    @GetMapping({"/", "/home" })
    public String home(Model model) {
        model.addAttribute("title", "Home");
        model.addAttribute("user", getUser());
        return "common/home";
    }
}

