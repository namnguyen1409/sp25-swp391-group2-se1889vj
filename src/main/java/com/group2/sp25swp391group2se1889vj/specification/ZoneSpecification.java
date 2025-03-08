package com.group2.sp25swp391group2se1889vj.specification;

import com.group2.sp25swp391group2se1889vj.dto.ZoneFilterDTO;
import com.group2.sp25swp391group2se1889vj.entity.Zone;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta. persistence. criteria. Predicate;

public class ZoneSpecification {
    public static Specification<Zone> filterZones(Long warehouseId, ZoneFilterDTO zoneFilterDTO) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("warehouse").get("id"), warehouseId));
            if (zoneFilterDTO.getName() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + zoneFilterDTO.getName().toLowerCase() + "%"));
            }
            if (zoneFilterDTO.getProductName() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), "%" + zoneFilterDTO.getProductName().toLowerCase() + "%"));
            }
            if (zoneFilterDTO.getMinQuantity() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), zoneFilterDTO.getMinQuantity()));
            }
            if (zoneFilterDTO.getMaxQuantity() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), zoneFilterDTO.getMinQuantity()));
            }
            if (zoneFilterDTO.getMinCreatedAt() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createAt"), zoneFilterDTO.getMinCreatedAt()));
            }
            if (zoneFilterDTO.getMaxCreatedAt() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createAt"), zoneFilterDTO.getMaxCreatedAt()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
