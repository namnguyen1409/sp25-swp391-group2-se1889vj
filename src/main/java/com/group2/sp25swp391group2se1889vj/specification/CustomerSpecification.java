package com.group2.sp25swp391group2se1889vj.specification;

import com.group2.sp25swp391group2se1889vj.dto.CustomerFilterDTO;
import com.group2.sp25swp391group2se1889vj.entity.Customer;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CustomerSpecification {


    public static Specification<Customer> filterCustomers(Long warehouseId, CustomerFilterDTO customerFilterDTO) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("warehouse").get("id"), warehouseId));
            if (customerFilterDTO.getFullName() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), "%" + customerFilterDTO.getFullName().toLowerCase() + "%"));
            }
            if (customerFilterDTO.getPhone() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), "%" + customerFilterDTO.getPhone().toLowerCase() + "%"));
            }
            if (customerFilterDTO.getEmail() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + customerFilterDTO.getEmail().toLowerCase() + "%"));
            }
            if (customerFilterDTO.getAddress() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), "%" + customerFilterDTO.getAddress().toLowerCase() + "%"));
            }
            if (customerFilterDTO.getMinBalance() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("balance"), customerFilterDTO.getMinBalance()));
            }
            if (customerFilterDTO.getMaxBalance() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("balance"), customerFilterDTO.getMaxBalance()));
            }
            if (customerFilterDTO.getMinCreatedAt() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), customerFilterDTO.getMinCreatedAt()));
            }
            if (customerFilterDTO.getMaxCreatedAt() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), customerFilterDTO.getMaxCreatedAt()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
