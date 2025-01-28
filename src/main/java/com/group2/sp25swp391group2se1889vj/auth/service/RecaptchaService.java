package com.group2.sp25swp391group2se1889vj.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class RecaptchaService {
    @Value("${google.recaptcha.key.secret}")
    private String recaptchaSecretKey;

    @Value("${google.recaptcha.url}")
    private String recaptchaUrl;

    public boolean verifyRecaptcha(String recaptchaResponse) {
        RestTemplate restTemplate = new RestTemplate();
        String url = recaptchaUrl + "?secret=" + recaptchaSecretKey + "&response=" + recaptchaResponse;
        Map<String, Object> response = restTemplate.postForObject(url, null, Map.class);
        return response != null && Boolean.TRUE.equals(response.get("success"));
    }
}
