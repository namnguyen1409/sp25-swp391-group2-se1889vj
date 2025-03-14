package com.group2.sp25swp391group2se1889vj.scheduler;

import com.group2.sp25swp391group2se1889vj.controller.WebSocketController;
import com.group2.sp25swp391group2se1889vj.entity.Debt;
import com.group2.sp25swp391group2se1889vj.repository.DebtRepository;
import com.group2.sp25swp391group2se1889vj.service.CustomerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BalanceScheduler {
    private final CustomerService customerService;
    private final DebtRepository debtRepository;
    private final WebSocketController webSocketController;

    @Scheduled(cron = "* * * * * *")
    @Transactional
    public void updateBalance() {
        List<Debt> debts = debtRepository.findDebtsByIsProcessedIsFalse();
        for (Debt debt : debts) {
            BigDecimal amount = debt.getAmount();
            switch (debt.getType()) {
                case CUSTOMER_BORROW, SHOP_REPAY -> customerService.subtractBalance(debt.getCustomer().getId(), amount);
                case CUSTOMER_REPAY, SHOP_BORROW -> customerService.addBalance(debt.getCustomer().getId(), amount);
            }
            debt.setProcessed(true);
            debt = debtRepository.save(debt);
            if (debt.getCreatedBy() != null) {
                webSocketController.sendInfo("Đã thêm bản ghi nợ mới cho khách hàng: " + debt.getCustomer().getFullName(), debt.getCreatedBy().getUsername());
            }
        }
    }
}
