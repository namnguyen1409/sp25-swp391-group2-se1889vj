package com.group2.sp25swp391group2se1889vj.specification;

import com.group2.sp25swp391group2se1889vj.dto.UserFilterDTO;
import com.group2.sp25swp391group2se1889vj.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> filterUsers(Long ownerId, UserFilterDTO userFilterDTO) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("owner").get("id"), ownerId));
            if (userFilterDTO.getUsername() != null && !userFilterDTO.getUsername().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + userFilterDTO.getUsername().toLowerCase() + "%"));
            }
            if (userFilterDTO.getFirstName() != null && !userFilterDTO.getFirstName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + userFilterDTO.getFirstName().toLowerCase() + "%"));
            }
            if (userFilterDTO.getLastName() != null && !userFilterDTO.getLastName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + userFilterDTO.getLastName().toLowerCase() + "%"));
            }
            if (userFilterDTO.getPhone() != null && !userFilterDTO.getPhone().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), "%" + userFilterDTO.getPhone().toLowerCase() + "%"));
            }
            if (userFilterDTO.getEmail() != null && !userFilterDTO.getEmail().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + userFilterDTO.getEmail().toLowerCase() + "%"));
            }
            if (userFilterDTO.getAddress() != null && !userFilterDTO.getAddress().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), "%" + userFilterDTO.getAddress().toLowerCase() + "%"));
            }
            if (userFilterDTO.getRole() != null) {
                predicates.add(criteriaBuilder.equal(root.get("role"), userFilterDTO.getRole()));
            }
            if (userFilterDTO.getMinCreatedAt() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), userFilterDTO.getMinCreatedAt()));
            }
            if (userFilterDTO.getMaxCreatedAt() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), userFilterDTO.getMaxCreatedAt()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<User> filterUsersByAdmin(UserFilterDTO userFilterDTO) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userFilterDTO.getUsername() != null && !userFilterDTO.getUsername().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + userFilterDTO.getUsername().toLowerCase() + "%"));
            }
            if (userFilterDTO.getFirstName() != null && !userFilterDTO.getFirstName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + userFilterDTO.getFirstName().toLowerCase() + "%"));
            }
            if (userFilterDTO.getLastName() != null && !userFilterDTO.getLastName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + userFilterDTO.getLastName().toLowerCase() + "%"));
            }
            if (userFilterDTO.getPhone() != null && !userFilterDTO.getPhone().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), "%" + userFilterDTO.getPhone().toLowerCase() + "%"));
            }
            if (userFilterDTO.getEmail() != null && !userFilterDTO.getEmail().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + userFilterDTO.getEmail().toLowerCase() + "%"));
            }
            if (userFilterDTO.getAddress() != null && !userFilterDTO.getAddress().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), "%" + userFilterDTO.getAddress().toLowerCase() + "%"));
            }
            if (userFilterDTO.getRole() != null) {
                predicates.add(criteriaBuilder.equal(root.get("role"), userFilterDTO.getRole()));
            }
            if (userFilterDTO.getMinCreatedAt() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), userFilterDTO.getMinCreatedAt()));
            }
            if (userFilterDTO.getMaxCreatedAt() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), userFilterDTO.getMaxCreatedAt()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
