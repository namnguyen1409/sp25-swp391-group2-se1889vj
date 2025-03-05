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


@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "debt")
public class Debt extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "nvarchar(20)")
    private DebtType type;

    @EqualsAndHashCode.Include
    @Column(name = "description", nullable = false, columnDefinition = "nvarchar(max)")
    private String description;

    @EqualsAndHashCode.Include
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @EqualsAndHashCode.Include
    @Column(name = "debt_at", nullable = false)
    private LocalDateTime debtAt;

    @EqualsAndHashCode.Include
    @Column(name = "image", columnDefinition = "nvarchar(255)")
    private String image;

    @Column(nullable = false, columnDefinition = "bit")
    private boolean isProcessed = false;

    @Column(nullable = false, columnDefinition = "bit")
    private boolean isSuccess = false;

    @ManyToOne
    @JoinColumn(name="customer_id", nullable = false)
    private Customer customer;

}
