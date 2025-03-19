package com.group2.sp25swp391group2se1889vj.specification;

import com.group2.sp25swp391group2se1889vj.dto.ProductFilterDTO;
import com.group2.sp25swp391group2se1889vj.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {
    public static Specification<Product> filterProducts(Long warehouseId, ProductFilterDTO filterDTO) {
        return(root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("warehouse").get("id"), warehouseId));

            if (filterDTO.getName() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + filterDTO.getName().toLowerCase() + "%"));
            }

            if (filterDTO.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), filterDTO.getMinPrice()));
            }

            if (filterDTO.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), filterDTO.getMaxPrice()));
            }

            if (filterDTO.getMinQuantity() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("quantity"), filterDTO.getMinQuantity()));
            }

            if (filterDTO.getMaxQuantity() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("quantity"), filterDTO.getMaxQuantity()));
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
