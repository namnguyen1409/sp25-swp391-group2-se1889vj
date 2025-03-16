package com.group2.sp25swp391group2se1889vj.service;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {

        void sendMail(String to, String subject, String body);

        boolean sendHTMLMail(String to, String subject, String body);

        public String generatedVerificationCode();

        public void storeVerificationCode(String email, String code);

        public String getStoredVerificationCode(String email);

        public boolean sendVerificationEmail(String email, String code);

        public boolean verifyCode(String email, String code);
}
