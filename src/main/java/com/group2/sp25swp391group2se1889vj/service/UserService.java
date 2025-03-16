package com.group2.sp25swp391group2se1889vj.service;

import com.group2.sp25swp391group2se1889vj.dto.*;
import com.group2.sp25swp391group2se1889vj.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface  UserService {

    Page<UserDTO> findPaginatedUsers(Pageable pageable);
    Page<UserDTO> findPaginatedUsersByOwnerId(Long owner, Pageable pageable);
    Page<UserDTO> findPaginatedUsersByAssignedWarehouseId(Long warehouseId, Pageable pageable);
    Page<UserDTO> searchUsers(Long ownerId, UserFilterDTO userFilterDTO, Pageable pageable);
    Page<UserDTO> searchUsersByAdmin(UserFilterDTO filter, Pageable pageable);

    UserDTO findUserById(Long id);
    UserDTO findUserByEmail(String email);
    User findUserByUsername(String username);

    void saveUser(UserDTO userDTO);
    void updateUser(UserDTO userDTO);
    void sendVerificationCode(String email);
    void updateEmail(Long id, String newEmail);
    void registerUser(RegisterDTO registerDTO) throws Exception;

    boolean existsByEmail(String value);
    Boolean existsByPhoneAndIdNot(String phone, Long id);
    Boolean existsByEmailAndIdNot(String email, Long id);

    Boolean existsByEmailAndId(String email, Long id);

    public String getStoredVerificationCode(String email);
}
