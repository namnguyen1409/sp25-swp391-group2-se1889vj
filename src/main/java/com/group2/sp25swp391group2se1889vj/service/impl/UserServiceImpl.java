package com.group2.sp25swp391group2se1889vj.service.impl;

import ch.qos.logback.classic.spi.IThrowableProxy;
import com.group2.sp25swp391group2se1889vj.dto.*;
import com.group2.sp25swp391group2se1889vj.entity.Customer;
import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.entity.Warehouse;
import com.group2.sp25swp391group2se1889vj.enums.RoleType;
import com.group2.sp25swp391group2se1889vj.exception.InvalidRegistrationTokenException;
import com.group2.sp25swp391group2se1889vj.exception.NoWarehouseElementException;
import com.group2.sp25swp391group2se1889vj.exception.UserNoSuchElementException;
import com.group2.sp25swp391group2se1889vj.mapper.UserMapper;
import com.group2.sp25swp391group2se1889vj.repository.RegistrationTokenRepository;
import com.group2.sp25swp391group2se1889vj.repository.UserRepository;
import com.group2.sp25swp391group2se1889vj.repository.WarehouseRepository;
import com.group2.sp25swp391group2se1889vj.service.EmailService;
import com.group2.sp25swp391group2se1889vj.service.MessageService;
import com.group2.sp25swp391group2se1889vj.service.UserService;
import com.group2.sp25swp391group2se1889vj.specification.CustomerSpecification;
import com.group2.sp25swp391group2se1889vj.specification.UserSpecification;
import com.group2.sp25swp391group2se1889vj.util.EncryptionUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final RegistrationTokenRepository registrationTokenRepository;
    private final EncryptionUtil encryptionUtil;
    private final PasswordEncoder passwordEncoder;
    private final WarehouseRepository warehouseRepository;
    private final MessageService messageService;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private MessageSource messageSource;
    private final EmailService emailService;

    @Override
    public Page<UserDTO> findPaginatedUsers(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return page.map(userMapper::mapToUserDTO);
    }

    @Override
    public Page<UserDTO> findPaginatedUsersByOwnerId(Long id , Pageable pageable) {
        Optional<User> owner = userRepository.findById(id);
        if(owner.isPresent()) {
            Page<User> page = userRepository.findByOwner(owner.get(), pageable);
            return page.map(userMapper::mapToUserDTO);
        }
        return null;
    }

    @Override
    public Page<UserDTO> findPaginatedUsersByAssignedWarehouseId(Long warehouseId, Pageable pageable) {
        Optional<Warehouse> warehouse = warehouseRepository.findById(warehouseId);
        if(warehouse.isPresent()) {
            Page<User> page = userRepository.findAllByAssignedWarehouseId(warehouseId, pageable);
            return page.map(userMapper::mapToUserDTO);
        }
        return null;
    }

    @Override
    public Page<UserDTO> searchUsers(Long ownerId, UserFilterDTO userFilterDTO, Pageable pageable) {
        Specification<User> spec = UserSpecification.filterUsers(ownerId, userFilterDTO);
        return userRepository.findAll(spec, pageable).map(userMapper::mapToUserDTO);
    }

    @Override
    public Page<UserDTO> searchUsersByAdmin(UserFilterDTO filter, Pageable pageable) {
        Specification<User> specification = UserSpecification.filterUsersByAdmin(filter);
        return userRepository.findAll(specification, pageable).map(userMapper::mapToUserDTO);
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
    public UserDTO findUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        UserDTO userDTO = new UserDTO();
        if (userOptional.isPresent()) {
            userDTO = userMapper.mapToUserDTO(userOptional.get());

        }
        return userDTO;
    }

    @Override
    public User findUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElse(null);
    }

    @Override
    public void saveUser(UserDTO userDTO) {
        User user = userMapper.mapToUser(userDTO);
        userRepository.save(user);
    }

    @Override
    public void updateUser(UserDTO userDTO) {
        Optional<User> userOptional = userRepository.findById(userDTO.getId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUsername(userDTO.getUsername());
            user.setPhone(userDTO.getPhone());
            user.setAddress(userDTO.getAddress());
            user.setEmail(userDTO.getEmail());
            user.setLocked(userDTO.isLocked());
            user.setLockReason(userDTO.getLockReason());
            userRepository.save(user);
        } else {
            String message = messageService.getMessage("entity.notfound", userDTO.getId());
            throw new UserNoSuchElementException(message, userDTO.getId());
        }
    }

    @Override
    public void sendVerificationCode(String email) {
        String code = emailService.generatedVerificationCode();
        emailService.storeVerificationCode(email, code);
        emailService.sendVerificationEmail(email, code);
    }

    @Override
    public void updateEmail(Long id, String newEmail) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEmail(newEmail);
            userRepository.save(user);
        }
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
            warehouse.setMaxDiscount(0);
            warehouse = warehouseRepository.save(warehouse);
            user.setWarehouse(warehouse);
        } else {
            // Gán vào kho của chủ shop
            User owner = registrationToken.getCreatedBy();
            user.setAssignedWarehouse(owner.getWarehouse());
            user.setOwner(owner);
        }

        userRepository.save(user);
        registrationTokenRepository.deleteByToken(registrationToken.getToken());
    }

    @Override
    public boolean existsByEmail(String value) {
        return userRepository.existsByEmail(value);
    }

    @Override
    public Boolean existsByPhoneAndIdNot(String phone, Long id) {
        return userRepository.existsByPhoneAndIdNot(phone, id);
    }

    @Override
    public Boolean existsByEmailAndIdNot(String email, Long id) {
        return userRepository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public Boolean existsByEmailAndId(String email, Long id) {
        return userRepository.existsByEmailAndId(email, id);
    }

    @Override
    public String getStoredVerificationCode(String email) {
        return emailService.getStoredVerificationCode(email);
    }


}