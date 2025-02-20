package com.group2.sp25swp391group2se1889vj.controller;

import com.group2.sp25swp391group2se1889vj.dto.UserDTO;
import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.enums.RoleType;
import com.group2.sp25swp391group2se1889vj.security.CustomUserDetails;
import com.group2.sp25swp391group2se1889vj.security.RecaptchaService;
import com.group2.sp25swp391group2se1889vj.service.UserService;
import com.group2.sp25swp391group2se1889vj.service.impl.EmailServiceImpl;
import com.group2.sp25swp391group2se1889vj.util.EncryptionUtil;
import com.group2.sp25swp391group2se1889vj.entity.RegistrationToken;
import com.group2.sp25swp391group2se1889vj.repository.RegistrationTokenRepository;
import com.group2.sp25swp391group2se1889vj.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

        private final RegistrationTokenRepository registrationTokenRepository;
        private final RecaptchaService recaptchaService;
        private final PasswordEncoder passwordEncoder;
        private final EmailServiceImpl emailService;
        private final UserRepository userRepository;
        private final EncryptionUtil encryptionUtil;
    private final UserService userService;

    private User getUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }


    private Map<String, String> createPairs(List<String> fields, List<String> fieldTitles) {
        Map<String, String> pairs = new HashMap<>();
        for (int i = 0; i < fields.size(); i++) {
            pairs.put(fields.get(i), fieldTitles.get(i));
        }
        return pairs;
    }

    @GetMapping({"", "/", "/list"})
    public String listUsers(
            Model model,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "searchBy", required = false, defaultValue = "name") String searchBy,
            @RequestParam(value = "orderBy", required = false, defaultValue = "createdAt") String orderBy,
            @RequestParam(value = "direction", required = false, defaultValue = "desc") String direction
    ) {
        List<String> fields = Arrays.asList("username", "firstName","lastName", "email", "phone", "address", "role");
        Map<String, String> fieldTitles = createPairs(fields, Arrays.asList("Tên đăng nhập",  "Họ", "Tên", "Email", "Số điện thoại", "Địa chỉ", "Vai trò"));
        Map<String, String> fieldClasses = createPairs(fields, Arrays.asList("text","text", "text", "email", "text", "text", "select"));
        List<String> searchAbleFields = Arrays.asList("username", "firstName", "lastName", "email", "phone", "address");
        model.addAttribute("fields", fields);
        model.addAttribute("fieldTitles", fieldTitles);
        model.addAttribute("fieldClasses", fieldClasses);
        model.addAttribute("searchAbleFields", searchAbleFields);
        if (!fields.contains(searchBy)) {
            searchBy = "username";
        }
        if (!fields.contains(orderBy)) {
            orderBy = "id";
        }
        Sort sortDirection = "asc".equalsIgnoreCase(direction)
                ? Sort.by(orderBy).ascending()
                : Sort.by(orderBy).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sortDirection);

        Page<UserDTO> users;
        if (search != null && !search.isEmpty()) {
            users = switch (searchBy) {
                case "username" -> userService.findPaginatedUsersByUserName(search, pageable);
                default -> userService.findPaginatedUsers(pageable);
            };
        } else {
            users = userService.findPaginatedUsers(pageable);
        }
        model.addAttribute("users", users);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("search", search);
        model.addAttribute("orderBy", orderBy);
        model.addAttribute("searchBy", searchBy);
        model.addAttribute("direction", direction);
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
            @RequestParam("email") String email,
            @RequestParam("g-recaptcha-response") String recaptchaResponse,
            Model model,
            RedirectAttributes redirectAttributes
    ) throws Exception {
        if (recaptchaService.notVerifyRecaptcha(recaptchaResponse)) {
            model.addAttribute("error", "reCAPTCHA không hợp lệ. Vui lòng thử lại.");
            return "user/add";
        }

        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Người dùng đã tồn tại.");
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
        newRegistrationToken.setToken(encryptionUtil.encrypt(token));
        if (getUser().getRole().equals(RoleType.ADMIN)) {
            newRegistrationToken.setRole(RoleType.OWNER);
        } else {
            newRegistrationToken.setRole(RoleType.STAFF);
        }
        newRegistrationToken.setCreatedBy(getUser());

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


        if (check) {
            registrationTokenRepository.save(newRegistrationToken);
            redirectAttributes.addFlashAttribute("success", "Đã gửi thư mời thành công.");
        }
        else {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi gửi email, hãy thử lại hoặc liên hệ quản trị viên.");
        }
        return "redirect:/users";
    }
}
