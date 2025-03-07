package com.group2.sp25swp391group2se1889vj.service;

import com.group2.sp25swp391group2se1889vj.dto.RegisterDTO;
import com.group2.sp25swp391group2se1889vj.dto.UserDTO;
import com.group2.sp25swp391group2se1889vj.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface  UserService {

    Page<UserDTO> findPaginatedUsers(Pageable pageable);
    Page<UserDTO> findPaginatedUsersByUserName(String username, Pageable pageable);
    UserDTO findUserById(Long id);
    void saveUser(UserDTO userDTO);
    User findUserByUsername(String username);

    void registerUser(RegisterDTO registerDTO) throws Exception;

    boolean existsByEmail(String value);
}
