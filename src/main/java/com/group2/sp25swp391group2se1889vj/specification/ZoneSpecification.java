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
            if (zoneFilterDTO.getProductName() != null && !zoneFilterDTO.getProductName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("product").get("name")), "%" + zoneFilterDTO.getProductName().toLowerCase() + "%"));
            }
            if (zoneFilterDTO.getMinCreatedAt() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createAt"), zoneFilterDTO.getMinCreatedAt()));
            }
            if (zoneFilterDTO.getMaxCreatedAt() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createAt"), zoneFilterDTO.getMaxCreatedAt()));
            }
            if(zoneFilterDTO.getMinUpdateAt() != null){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("updateAt"), zoneFilterDTO.getMinUpdateAt()));
            }
            if(zoneFilterDTO.getMaxUpdateAt() != null){
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("updateAt"), zoneFilterDTO.getMaxUpdateAt()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
