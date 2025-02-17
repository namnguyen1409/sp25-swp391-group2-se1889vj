package com.group2.sp25swp391group2se1889vj.user.service.impl;

import com.group2.sp25swp391group2se1889vj.user.dto.UserDTO;
import com.group2.sp25swp391group2se1889vj.user.entity.User;
import com.group2.sp25swp391group2se1889vj.user.mapper.UserMapper;
import com.group2.sp25swp391group2se1889vj.user.repository.UserRepository;
import com.group2.sp25swp391group2se1889vj.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private MessageSource messageSource;

    @Override
    public Page<UserDTO> findPaginatedUsers(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<User> page = userRepository.findAll(pageable);
        return page.map(userMapper::mapToUserDTO);
    }

    @Override
    public Page<UserDTO> findPaginatedUsersByUserName(String username, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<User> page = userRepository.findByUsernameContaining(username, pageable);
        return null;
    }

    /*
    * Optional<> la mot container thuong dc dung de bao boc 1 doi tuong co the null
    * Khi su dung Optional, ta co the kiem tra xem doi tuong do co null hay khong
    * Neu co null thi ta co the xu ly no
     */
    @Override
    public UserDTO findUserById(Long id) {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            return userOptional.map(user -> userMapper.mapToUserDTO(user)).orElse(null);
        } catch (Exception exception) {
            String message = messageSource.getMessage("entity.notfound", new Object[]{id}, null);
            throw new RuntimeException(message);
        }
    }

    @Override
    public void saveUser(UserDTO userDTO) {
        User user = userMapper.mapToUser(userDTO);
        userRepository.save(user);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
