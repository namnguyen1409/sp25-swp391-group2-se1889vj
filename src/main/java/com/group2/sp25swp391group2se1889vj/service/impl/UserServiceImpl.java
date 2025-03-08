package com.group2.sp25swp391group2se1889vj.service.impl;

import com.group2.sp25swp391group2se1889vj.dto.RegisterDTO;
import com.group2.sp25swp391group2se1889vj.dto.UserDTO;
import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.entity.Warehouse;
import com.group2.sp25swp391group2se1889vj.enums.RoleType;
import com.group2.sp25swp391group2se1889vj.exception.InvalidRegistrationTokenException;
import com.group2.sp25swp391group2se1889vj.mapper.UserMapper;
import com.group2.sp25swp391group2se1889vj.repository.RegistrationTokenRepository;
import com.group2.sp25swp391group2se1889vj.repository.UserRepository;
import com.group2.sp25swp391group2se1889vj.repository.WarehouseRepository;
import com.group2.sp25swp391group2se1889vj.service.UserService;
import com.group2.sp25swp391group2se1889vj.util.EncryptionUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final RegistrationTokenRepository registrationTokenRepository;
    private final EncryptionUtil encryptionUtil;
    private final PasswordEncoder passwordEncoder;
    private final WarehouseRepository warehouseRepository;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private MessageSource messageSource;

    @Override
    public Page<UserDTO> findPaginatedUsers(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return page.map(userMapper::mapToUserDTO);
    }

    @Override
    public Page<UserDTO> findPaginatedUsersByUserName(String username, Pageable pageable) {
        Page<User> page = userRepository.findByUsernameContaining(username, pageable);
        return page.map(userMapper::mapToUserDTO);
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


    @Override
    @Transactional
    public void registerUser(RegisterDTO registerDTO) throws Exception {
        // Kiểm tra token hợp lệ
        var registrationToken = registrationTokenRepository.findByToken(encryptionUtil.encrypt(registerDTO.getToken()));
        if (registrationToken == null) {
            throw new InvalidRegistrationTokenException("Token không hợp lệ, liên hệ với quản trị viên để được hỗ trợ");
        }
        if (userRepository.existsByEmail(registrationToken.getEmail())) {
            throw new InvalidRegistrationTokenException("Email đã được sử dụng, không thể đăng ký mới");
        }
        if (registrationToken.getUpdatedAt().plusDays(1).isBefore(LocalDateTime.now())) {
            throw new InvalidRegistrationTokenException("Token đã hết hạn, liên hệ với quản trị viên để được hỗ trợ");
        }

        // Tạo user
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setFirstName(registerDTO.getFirstName());
        user.setLastName(registerDTO.getLastName());
        user.setPhone(registerDTO.getPhone());
        user.setGender(registerDTO.isGender());
        user.setBirthday(registerDTO.getBirthday());
        user.setAddress(registerDTO.getAddress());
        user.setEmail(registrationToken.getEmail());
        user.setRole(registrationToken.getRole());

        user = userRepository.save(user);

        if (registrationToken.getRole() == RoleType.OWNER) {
            // Tạo kho cho chủ shop
            Warehouse warehouse = new Warehouse();
            warehouse.setName("Kho của " + user.getUsername());
            warehouse.setLocation(user.getAddress());
            warehouse.setOwner(user);
            user.setWarehouse(warehouse);
            warehouseRepository.save(warehouse);
        } else {
            // Gán vào kho của chủ shop
            User owner = registrationToken.getCreatedBy();
            user.setAssignedWarehouse(owner.getWarehouse());
            user.setOwner(owner);
        }

        userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String value) {
        return userRepository.existsByEmail(value);
    }



}
