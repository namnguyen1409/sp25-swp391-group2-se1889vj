package com.group2.sp25swp391group2se1889vj.mapper;

import com.group2.sp25swp391group2se1889vj.dto.UserDTO;
import com.group2.sp25swp391group2se1889vj.entity.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ModelMapper modelMapper;

    public UserDTO mapToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public User mapToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }
}
