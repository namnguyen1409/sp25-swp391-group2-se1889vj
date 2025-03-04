package com.group2.sp25swp391group2se1889vj.specification;

import com.group2.sp25swp391group2se1889vj.dto.DebtFilterDTO;
import com.group2.sp25swp391group2se1889vj.entity.Debt;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class DebtSpecification {
    public static Specification<Debt> filterDebts(Long customerId, DebtFilterDTO filterDTO) {
        return(root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("customer").get("id"), customerId));

            if (filterDTO.getType() != null) {
                predicates.add(criteriaBuilder.like(root.get("type"), filterDTO.getType().name()));
            }

            if (filterDTO.getDescription() != null && !filterDTO.getDescription().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + filterDTO.getDescription().toLowerCase() + "%"));
            }
            if (filterDTO.getMinAmount() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), filterDTO.getMinAmount()));
            }
            if (filterDTO.getMaxAmount() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("amount"), filterDTO.getMaxAmount()));
            }
            if (filterDTO.getMinDebtAt() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("debtAt"), filterDTO.getMinDebtAt()));
            }
            if (filterDTO.getMaxDebtAt() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("debtAt"), filterDTO.getMaxDebtAt()));
            }
            if (filterDTO.getMinCreatedAt() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), filterDTO.getMinCreatedAt()));
            }
            if (filterDTO.getMaxCreatedAt() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), filterDTO.getMaxCreatedAt()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
