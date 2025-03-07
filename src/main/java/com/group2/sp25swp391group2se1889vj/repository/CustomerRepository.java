package com.group2.sp25swp391group2se1889vj.repository;

import com.group2.sp25swp391group2se1889vj.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    Boolean existsByPhoneAndWarehouseId(String phone, Long warehouseId);
    Boolean existsByEmailAndWarehouseId(String email, Long warehouseId);
    Boolean existsByPhoneAndIdNot(String phone, Long id);
    Boolean existsByEmailAndIdNot(String email, Long id);

    Optional<Customer> findByPhone(String phone);

    @Modifying
    @Query("update Customer c set c.balance = c.balance + ?2, c.updatedAt = CURRENT_TIMESTAMP where c.id = ?1")
    void increaseBalance(Long id, BigDecimal amount);

    @Modifying
    @Query("update Customer c set c.balance = c.balance - ?2, c.updatedAt = CURRENT_TIMESTAMP where c.id = ?1")
    void decreaseBalance(Long id, BigDecimal amount);

    Boolean existsByPhoneAndWarehouseIdAndIdNot(String phone, Long warehouseId, Long id);

    Boolean existsByEmailAndWarehouseIdAndIdNot(String email, Long warehouseId, Long id);
}
