package com.group2.sp25swp391group2se1889vj.service.impl;

import com.group2.sp25swp391group2se1889vj.dto.RegistrationTokenDTO;
import com.group2.sp25swp391group2se1889vj.entity.RegistrationToken;
import com.group2.sp25swp391group2se1889vj.mapper.RegistrationTokenMapper;
import com.group2.sp25swp391group2se1889vj.repository.RegistrationTokenRepository;
import com.group2.sp25swp391group2se1889vj.repository.UserRepository;
import com.group2.sp25swp391group2se1889vj.service.EmailService;
import com.group2.sp25swp391group2se1889vj.service.RegistrationTokenService;
import com.group2.sp25swp391group2se1889vj.util.EncryptionUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class RegistrationTokenServiceImpl implements RegistrationTokenService {
    private final UserRepository userRepository;
    private final RegistrationTokenRepository registrationTokenRepository;
    private final RegistrationTokenMapper mapper;
    private final EmailService emailService;
    private final EncryptionUtil encryptionUtil;

    @Override
    public void saveToken(String token, Long userId) {

    }

    @Override
    public void deleteToken(String token) {

    }

    @Override
    public Long getUserIdByToken(String token) {
        return 0L;
    }

    @Override
    public Page<RegistrationTokenDTO> findPaginationRegisterTokenDTO(Pageable pageable) {
        Page<RegistrationToken> registrationTokenPage = registrationTokenRepository.findAll(pageable);
        return registrationTokenPage.map(mapper::mapToRegistrationTokenDTO);
    }

    @SneakyThrows
    @Override
    public void resendEmailInvitation(Long id) {
        RegistrationToken existingToken = registrationTokenRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Token không tồn tại"));
        try {
            if (userRepository.findByEmail(existingToken.getEmail()).isPresent())
                throw new RuntimeException("Email này đã được sử dụng");


            String newToken = UUID.randomUUID().toString();
            existingToken.setToken(encryptionUtil.encrypt(newToken));

            registrationTokenRepository.save(existingToken);
            boolean emailSent = emailService.sendHTMLMail(
                    existingToken.getEmail(),
                    "Thông báo kích hoạt tài khoản RSMS của bạn",
                    """
                            <div style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                                <h2 style="color: #4CAF50;">Xin chào,</h2>
                                <p>
                                    Bạn đã được mời tham gia vào hệ thống quản lý cửa hàng <b>RSMS</b> của chúng tôi. 
                                    Vui lòng nhấn vào đường dẫn bên dưới để kích hoạt tài khoản của bạn:
                                </p>
                                <p style="text-align: center; margin: 20px 0;">
                                    <a href="http://localhost:8080/register?token=%s" 
                                       style="background-color: #4CAF50; color: white; text-decoration: none; 
                                              padding: 10px 20px; font-size: 16px; border-radius: 5px;">
                                        Kích hoạt tài khoản
                                    </a>
                                </p>
                                <p><b>Chú ý:</b> Đường link trên sẽ hết hạn sau <b>24 giờ</b> kể từ thời điểm bạn nhận được email này.</p>
                                <p style="margin-top: 30px;">
                                    Trân trọng,<br>
                                    <b>Đội ngũ RSMS</b>
                                </p>
                                <hr style="border: none; border-top: 1px solid #ddd; margin: 30px 0;">
                                <p style="font-size: 14px; color: #777;">
                                    <i>Đây là email tự động, vui lòng không trả lời lại email này.</i>
                                </p>
                            </div>
                            """.formatted(newToken)
            );

            if (!emailSent) {
                throw new RuntimeException("Lỗi khi gửi lại email mời.");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Không thể gửi lại email mời: " + e.getMessage());
        }
    }


    @Override
    public Page<RegistrationTokenDTO> findByEmailContaining(String email, Pageable pageable) {
        Page<RegistrationToken> page = registrationTokenRepository.findByEmailContaining(email, pageable);
        return page.map(mapper::mapToRegistrationTokenDTO);
    }

    @Override
    public Page<RegistrationTokenDTO> findByTokenContaining(String token, Pageable pageable) {
        Page<RegistrationToken> page = registrationTokenRepository.findByTokenContaining(token, pageable);
        return page.map(mapper::mapToRegistrationTokenDTO);
    }

}
