package com.group2.sp25swp391group2se1889vj.user.controller;

import com.group2.sp25swp391group2se1889vj.auth.service.RecaptchaService;
import com.group2.sp25swp391group2se1889vj.common.service.impl.EmailServiceImpl;
import com.group2.sp25swp391group2se1889vj.common.util.EncryptionUtil;
import com.group2.sp25swp391group2se1889vj.user.entity.RegistrationToken;
import com.group2.sp25swp391group2se1889vj.user.repository.RegistrationTokenRepository;
import com.group2.sp25swp391group2se1889vj.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.UUID;

@Controller
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

        private final RegistrationTokenRepository registrationTokenRepository;
        //private final RecaptchaService recaptchaService;
        //private final PasswordEncoder passwordEncoder;
        private final EmailServiceImpl emailService;
        private final UserRepository userRepository;
        private final EncryptionUtil encryptionUtil;

        @GetMapping({"", "/", "/list"})
        public String listUsers(Model model) {
            model.addAttribute("users", registrationTokenRepository.findAll());
            return "user/list";
        }

        @GetMapping("/add")
        public String addUser(Model model) {
            return "user/add";
        }

    /**
     * Thêm người dùng mới
     *
     * @param email
     * //@param recaptchaResponse
     * @param model
     * @param redirectAttributes
     *
     *  - Kiểm tra reCAPTCHA
     *  - Kiểm tra email đã tồn tại chưa
     *  - Kiểm tra email đã được mời chưa
     *  - Tạo token mới
     *  - Gửi email mời
     *  - Lưu token
     * @return
     * @throws Exception
     */

    @PostMapping("/add")
    public String addUser(
            @RequestParam String email,
            //@RequestParam String recaptchaResponse,
            Model model,
            RedirectAttributes  redirectAttributes
    ) throws Exception {
//        if (!recaptchaService.verifyRecaptcha(recaptchaResponse)) {
//            model.addAttribute("error", "Invalid reCAPTCHA");
//            return "user/add";
//        }
        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Email already exists");
            return "user/add";
        }
        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Email already invited");
            return "user/add";
        }

        RegistrationToken registrationToken = registrationTokenRepository.findByEmail(email);
        if (registrationToken != null) {
            model.addAttribute("error", "User already exists.");
            return "user/add";
        }
        RegistrationToken newRegistrationToken = new RegistrationToken();
        newRegistrationToken.setEmail(email);
        var token = UUID.randomUUID().toString();
        var check = emailService.sendHTMLMail(
                email,
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
                      """.formatted(token)
        );
        if(!check){
            registrationTokenRepository.save(newRegistrationToken);
            redirectAttributes.addFlashAttribute("success", "User added successfully");
        }else{
            redirectAttributes.addFlashAttribute("error", "Failed to send email. Please try again or contact admin for support");
        }

        return "redirect:/users";
    }
}
