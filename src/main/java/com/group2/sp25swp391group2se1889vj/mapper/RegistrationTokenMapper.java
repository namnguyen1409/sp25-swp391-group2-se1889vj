package com.group2.sp25swp391group2se1889vj.mapper;

import com.group2.sp25swp391group2se1889vj.dto.RegistrationTokenDTO;
import com.group2.sp25swp391group2se1889vj.entity.RegistrationToken;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component

public class RegistrationTokenMapper {
    ModelMapper modelMapper = new ModelMapper();

    public RegistrationTokenDTO mapToRegistrationTokenDTO(RegistrationToken registrationToken) {
        RegistrationTokenDTO dto = new RegistrationTokenDTO();
        dto.setId(registrationToken.getId());
        dto.setEmail(registrationToken.getEmail());
        dto.setToken(registrationToken.getToken());
        dto.setRole(registrationToken.getRole());
        return dto;
    }

    public RegistrationToken mapToRegistrationToken(RegistrationTokenDTO registrationTokenDTO) {
        return modelMapper.map(registrationTokenDTO, RegistrationToken.class);
    }
}
