package com.group2.sp25swp391group2se1889vj.specification;

import com.group2.sp25swp391group2se1889vj.dto.InvoiceFilterDTO;
import com.group2.sp25swp391group2se1889vj.entity.Invoice;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class InvoiceSpecification {
    public static Specification<Invoice> filterInvoices(Long warehouseId, InvoiceFilterDTO invoiceFilterDTO) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("warehouse").get("id"), warehouseId));
            if (invoiceFilterDTO.getType() != null) {
                predicates.add(criteriaBuilder.like(root.get("type"), invoiceFilterDTO.getType().name()));
            }
            if(invoiceFilterDTO.getTotalPriceFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("totalPrice"), invoiceFilterDTO.getTotalPriceFrom()));
            }
            if(invoiceFilterDTO.getTotalPriceTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("totalPrice"), invoiceFilterDTO.getTotalPriceTo()));
            }
            if(invoiceFilterDTO.getTotalDiscountFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("totalDiscount"), invoiceFilterDTO.getTotalDiscountFrom()));
            }
            if(invoiceFilterDTO.getTotalDiscountTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("totalDiscount"), invoiceFilterDTO.getTotalDiscountTo()));
            }
            if(invoiceFilterDTO.getCustomerBalanceFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("customerBalance"), invoiceFilterDTO.getCustomerBalanceFrom()));
            }
            if(invoiceFilterDTO.getCustomerBalanceTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("customerBalance"), invoiceFilterDTO.getCustomerBalanceTo()));
            }
            if(invoiceFilterDTO.getTotalPayableFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("totalPayable"), invoiceFilterDTO.getTotalPayableFrom()));
            }
            if(invoiceFilterDTO.getTotalPayableTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("totalPayable"), invoiceFilterDTO.getTotalPayableTo()));
            }
            if(invoiceFilterDTO.getTotalPaidFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("totalPaid"), invoiceFilterDTO.getTotalPaidFrom()));
            }
            if(invoiceFilterDTO.getTotalPaidTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("totalPaid"), invoiceFilterDTO.getTotalPaidTo()));
            }
            if(invoiceFilterDTO.getTotalDebtFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("totalDebt"), invoiceFilterDTO.getTotalDebtFrom()));
            }
            if(invoiceFilterDTO.getTotalDebtTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("totalDebt"), invoiceFilterDTO.getTotalDebtTo()));
            }
            if(invoiceFilterDTO.getCustomerFullName() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("customer").get("fullName")), "%" + invoiceFilterDTO.getCustomerFullName().toLowerCase() + "%"));
            }
            if(invoiceFilterDTO.getCustomerPhone() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("customer").get("phone")), "%" + invoiceFilterDTO.getCustomerPhone().toLowerCase() + "%"));
            }
            if (invoiceFilterDTO.getMinCreatedAt() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), invoiceFilterDTO.getMinCreatedAt()));
            }
            if (invoiceFilterDTO.getMaxCreatedAt() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), invoiceFilterDTO.getMaxCreatedAt()));
            }
            if (invoiceFilterDTO.getCreatedByUsername() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("createdBy").get("username")), "%" + invoiceFilterDTO.getCreatedByUsername().toLowerCase() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
