package com.group2.sp25swp391group2se1889vj.config;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggingRequestMatcher implements RequestMatcher {
    private static final Logger logger = LoggerFactory.getLogger(LoggingRequestMatcher.class);

    @Override
    public boolean matches(HttpServletRequest request) {
        // format: 127.0.0.1 - - [12/Jan/2025 16:38:13] "GET / HTTP/1.1" -

        var time = LocalDateTime.now();
        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss");
        var timeString = time.format(formatter);

        logger.info("{} - - [{}] \"{} {} {}\" -",
                request.getRemoteAddr(),
                timeString,
                request.getMethod(),
                request.getRequestURI(),
                request.getProtocol());
        return false;
    }
}
