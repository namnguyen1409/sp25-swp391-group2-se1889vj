package com.group2.sp25swp391group2se1889vj.entity;

import com.group2.sp25swp391group2se1889vj.enums.DebtType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "debt")
public class Debt extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "nvarchar(10)")
    private DebtType type;

    @Column(name = "description", nullable = false, columnDefinition = "nvarchar(max)")
    private String description;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "debt_at", nullable = false)
    private LocalDateTime debtAt;

    @Column(name = "image", columnDefinition = "nvarchar(255)")
    private String image;

    @ManyToOne
    @JoinColumn(name="customer_id", nullable = false)
    private Customer customer;

}
