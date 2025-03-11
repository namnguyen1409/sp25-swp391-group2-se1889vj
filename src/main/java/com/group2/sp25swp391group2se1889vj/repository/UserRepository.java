package com.group2.sp25swp391group2se1889vj.repository;

import com.group2.sp25swp391group2se1889vj.dto.UserDTO;
import com.group2.sp25swp391group2se1889vj.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByPhone(String phone);
    Page<User> findByUsernameContaining(String username, Pageable pageable);
    Page<User> findByFirstNameContaining(String firstName, Pageable pageable);
    Page<User> findByLastNameContaining(String lastName, Pageable pageable);
    Page<User> findByEmailContaining(String email, Pageable pageable);
    Page<User> findByPhoneContaining(String phone, Pageable pageable);
    Page<User> findByAddressContaining(String address, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.createdBy = ?1")
    Page<User> findByCreatedByContaining(User user, Pageable pageable);



    Optional<User> findByRefreshTokens_Token(String token);
    Boolean existsByPhoneAndWarehouseIdAndIdNot(String phone, Long warehouseId, Long id);

    Boolean existsByEmailAndWarehouseIdAndIdNot(String email, Long warehouseId, Long id);
}
