package com.group2.sp25swp391group2se1889vj.service;

import com.group2.sp25swp391group2se1889vj.dto.UserDTO;
import com.group2.sp25swp391group2se1889vj.entity.User;
import org.springframework.data.domain.Page;

public interface  UserService {

    Page<UserDTO> findPaginatedUsers(int pageNumber, int pageSize);
    Page<UserDTO> findPaginatedUsersByUserName(String username, int pageNo, int pageSize);
    UserDTO findUserById(Long id);
    void saveUser(UserDTO userDTO);
    User findUserByUsername(String username);
}
