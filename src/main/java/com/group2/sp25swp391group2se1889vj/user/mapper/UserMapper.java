package com.group2.sp25swp391group2se1889vj.user.mapper;

import com.group2.sp25swp391group2se1889vj.user.dto.UserDTO;
import com.group2.sp25swp391group2se1889vj.user.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    ModelMapper modelMapper = new ModelMapper();

    public UserDTO mapToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public User mapToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }
}
