package com.group2.sp25swp391group2se1889vj.controller;


import com.group2.sp25swp391group2se1889vj.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/home" })
    public String home(Model model, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        model.addAttribute("title", "Home");
        model.addAttribute("user", customUserDetails.getUser());
        return "common/home";
    }
}

