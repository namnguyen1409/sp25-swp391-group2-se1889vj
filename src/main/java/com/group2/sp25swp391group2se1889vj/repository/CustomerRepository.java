package com.group2.sp25swp391group2se1889vj.repository;

import com.group2.sp25swp391group2se1889vj.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Page<Customer> findByFullNameContaining(String fullName, Pageable pageable);
    Page<Customer> findByCreatedById(Long createdById, Pageable pageable);
    Page<Customer> findByCreatedByIdAndFullNameContaining(Long createdById, String fullName, Pageable pageable);
    Page<Customer> findByCreatedByIdAndPhoneContaining(Long createdById, String phone, Pageable pageable);
    Page<Customer> findByCreatedByIdAndEmailContaining(Long createdById, String email, Pageable pageable);
    Page<Customer> findByCreatedByIdAndAddressContaining(Long createdById, String address, Pageable pageable);
    Boolean existsByPhone(String phone);
    Boolean existsByEmail(String email);
    Boolean existsByPhoneAndIdNot(String phone, Long id);
    Boolean existsByEmailAndIdNot(String email, Long id);

    Optional<Customer> findByPhone(String phone);
}
