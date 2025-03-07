package com.group2.sp25swp391group2se1889vj.specification;


import com.group2.sp25swp391group2se1889vj.dto.ProductPackageFilterDTO;
import com.group2.sp25swp391group2se1889vj.entity.ProductPackage;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductPackageSpecification {
    public static Specification<ProductPackage> filterProductPackages(Long warehouseId, ProductPackageFilterDTO filterDTO) {
        return(root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (warehouseId != null) {
                predicates.add(criteriaBuilder.equal(root.get("warehouse").get("id"), warehouseId));
            }

            if (filterDTO.getName() != null) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + filterDTO.getName() + "%"));
            }

            if (filterDTO.getWeightFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("weight"), filterDTO.getWeightFrom()));
            }
            if (filterDTO.getWeightTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("weight"), filterDTO.getWeightTo()));
            }

            if (filterDTO.getMinCreatedAt() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), filterDTO.getMinCreatedAt()));
            }
            if (filterDTO.getMaxCreatedAt() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), filterDTO.getMaxCreatedAt()));
            }

            if (filterDTO.getMinUpdatedAt() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("updatedAt"), filterDTO.getMinUpdatedAt()));
            }

            if (filterDTO.getMaxUpdatedAt() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("updatedAt"), filterDTO.getMaxUpdatedAt()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
