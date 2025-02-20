package com.group2.sp25swp391group2se1889vj.service;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {

        void sendMail(String to, String subject, String body);

        boolean sendHTMLMail(String to, String subject, String body);
}
