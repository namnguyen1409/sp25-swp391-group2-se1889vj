package com.group2.sp25swp391group2se1889vj.controller;

import com.group2.sp25swp391group2se1889vj.message.InvoiceStatusMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendInvoiceStatus(Long orderId, String status, String message,String createdBy) {
        InvoiceStatusMessage messageData = new InvoiceStatusMessage(orderId, status, message,createdBy);
        messagingTemplate.convertAndSendToUser(createdBy, "/queue/invoice-status", messageData);
    }
}









