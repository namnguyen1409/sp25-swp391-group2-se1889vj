package com.group2.sp25swp391group2se1889vj.messaging;

import com.group2.sp25swp391group2se1889vj.config.RabbitMQConfig;
import com.group2.sp25swp391group2se1889vj.dto.DebtDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DebtProducer {

    private final RabbitTemplate rabbitTemplate;

    public void addDebtToQueue(DebtDTO debtDTO) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.DEBT_EXCHANGE, RabbitMQConfig.DEBT_ROUTING_KEY, debtDTO);
    }

}
