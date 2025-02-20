package com.group2.sp25swp391group2se1889vj.messaging;

import com.group2.sp25swp391group2se1889vj.config.RabbitMQConfig;
import com.group2.sp25swp391group2se1889vj.dto.DebtDTO;
import com.group2.sp25swp391group2se1889vj.service.DebtService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DebtConsumer {

    private final DebtService debtService;
    private final SimpMessagingTemplate simpMessagingTemplate;


    @RabbitListener(queues = RabbitMQConfig.DEBT_QUEUE)
    public void processDebt(DebtDTO debtDTO) {
        try {
            debtService.saveDebt(debtDTO);
            simpMessagingTemplate.convertAndSend("/topic/notifications",
                    "Bản ghi nợ của khách hàng #" + debtDTO.getCustomerId() + " đã được xử lý");
        } catch (Exception e) {
            simpMessagingTemplate.convertAndSend("/topic/notifications",
                    "Xử lý bản ghi nợ của khách hàng #" + debtDTO.getCustomerId() + " thất bại");
        }
    }
}
