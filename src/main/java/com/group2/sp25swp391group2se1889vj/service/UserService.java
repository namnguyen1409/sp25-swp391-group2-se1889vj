package com.group2.sp25swp391group2se1889vj.service;

import com.group2.sp25swp391group2se1889vj.dto.RegisterDTO;
import com.group2.sp25swp391group2se1889vj.dto.UserDTO;
import com.group2.sp25swp391group2se1889vj.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface  UserService {

    Page<UserDTO> findPaginatedUsers(Pageable pageable);
    Page<UserDTO> findPaginationUsersCreatedBy(Long id, Pageable pageable);
    Page<UserDTO> findPaginatedUsersByUserName(String username, Pageable pageable);
    Page<UserDTO> findPaginatedUsersByFirstName(String firstName, Pageable pageable);
    Page<UserDTO> findPaginatedUsersByLastName(String lastName, Pageable pageable);
    Page<UserDTO> findPaginatedUsersByEmail(String email, Pageable pageable);
    Page<UserDTO> findPaginatedUsersByPhone(String phone, Pageable pageable);
    Page<UserDTO> findPaginatedUsersByAddress(String address, Pageable pageable);
    UserDTO findUserById(Long id);
    User findUserByUsername(String username);

    void saveUser(UserDTO userDTO);
    void updateUser(UserDTO userDTO);
    void sendVerificationCode(String email);
    void updateEmail(String email);
    void registerUser(RegisterDTO registerDTO) throws Exception;

    boolean existsByEmail(String value);
    Boolean existsByPhoneAndWarehouseIdAndIdNot(String phone, Long warehouseId, Long id);
    Boolean existsByEmailAndWarehouseIdAndIdNot(String email, Long warehouseId, Long id);

    public String getStoredVerificationCode(String email);
}
