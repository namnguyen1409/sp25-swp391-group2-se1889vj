package com.group2.sp25swp391group2se1889vj.controller;

import com.group2.sp25swp391group2se1889vj.dto.CustomerDTO;
import com.group2.sp25swp391group2se1889vj.dto.RegistrationTokenDTO;
import com.group2.sp25swp391group2se1889vj.dto.UserDTO;
import com.group2.sp25swp391group2se1889vj.dto.VerificationCodeDTO;
import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.enums.RoleType;
import com.group2.sp25swp391group2se1889vj.security.CustomUserDetails;
import com.group2.sp25swp391group2se1889vj.security.RecaptchaService;
import com.group2.sp25swp391group2se1889vj.service.RegistrationTokenService;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
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
    private final RegistrationTokenService registrationTokenService;

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

    private Long getWarehouseId() {
        var currentUser = getUser();
        if(currentUser.getRole() == RoleType.OWNER) return currentUser.getWarehouse().getId();
        else {
            return currentUser.getAssignedWarehouse() != null ? currentUser.getAssignedWarehouse().getId() : null;
        }
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
                case "firstName" -> userService.findPaginatedUsersByFirstName(search, pageable);
                case "lastName" -> userService.findPaginatedUsersByLastName(search, pageable);
                case "email" -> userService.findPaginatedUsersByEmail(search, pageable);
                case "phone" -> userService.findPaginatedUsersByPhone(search, pageable);
                case "address" -> userService.findPaginatedUsersByAddress(search, pageable);
                default -> userService.findPaginatedUsers(pageable);
            };
        } else {
            if(getUser().getRole() == RoleType.OWNER) {
                users = userService.findPaginatedUsersByWarehouseId(getWarehouseId(), pageable);
//                System.out.println(getWarehouseId());
//                for(UserDTO user : users) {
//                    System.out.println(user);
//                    System.out.println("User warehouse ID: " + user.getWarehouseId());
//                }
            } else {
                users = userService.findPaginatedUsers(pageable);
            }
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
            model.addAttribute("error", "Người dùng đã tồn tại.");
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
        return "user/add";
    }

    @GetMapping("/waiter-list")
    public String listWaiter(Model model,
                                @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                @RequestParam(value = "search", required = false) String search,
                                @RequestParam(value = "searchBy", required = false, defaultValue = "email") String searchBy,
                                @RequestParam(value = "orderBy", required = false, defaultValue = "createdAt") String orderBy,
                                @RequestParam(value = "direction", required = false, defaultValue = "desc") String direction

    ) {

//        List<Map<String, String>> waitersInfo = new ArrayList<>();
//        for (RegistrationTokenDTO token : waiterPage.getContent()) {
//            Map<String, String> waiterMap = new HashMap<>();
//            waiterMap.put("email", token.getEmail());
//            waiterMap.put("status", (userRepository.findByEmail(token.getEmail()).isPresent()?"Đã kích hoạt":"Chưa kích hoạt"));
//            waiterMap.put("role", token.getRole().toString());
//            waitersInfo.add(waiterMap);
//            System.out.println(waiterMap);
//        }

        List<String> fields = Arrays.asList("email", "token","role");
        Map<String, String> fieldTitles = createPairs(fields, Arrays.asList("Email", "Mã đăng kí","Vai trò"));
        Map<String, String> fieldClasses = createPairs(fields, Arrays.asList("", "", ""));
        List<String> searchAbleFields = Arrays.asList("email", "token","role");

        model.addAttribute("fields", fields);
        model.addAttribute("fieldTitles", fieldTitles);
        model.addAttribute("fieldClasses", fieldClasses);
        model.addAttribute("searchAbleFields", searchAbleFields);

        if (!fields.contains(searchBy)) {
            searchBy = "email";
        }
        if (!fields.contains(orderBy)) {
            orderBy = "id";
        }
        Sort sortDirection = "asc".equalsIgnoreCase(direction)
                ? Sort.by(orderBy).ascending()
                : Sort.by(orderBy).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sortDirection);

        Page<RegistrationTokenDTO> waiters;
        if (search != null && !search.isEmpty()) {
            waiters = switch (searchBy) {
                case "email" -> registrationTokenService.findByEmailContaining(search, pageable);
                case "token" -> registrationTokenService.findByTokenContaining(search, pageable);
                default -> registrationTokenService.findPaginationRegisterTokenDTO(pageable);
            };
        } else {
            waiters = registrationTokenService.findPaginationRegisterTokenDTO(pageable);
        }

        // Các tham số phân trang và tìm kiếm
        model.addAttribute("waiters", waiters);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("search", search);
        model.addAttribute("orderBy", orderBy);
        model.addAttribute("searchBy", searchBy);
        model.addAttribute("direction", direction);
        return "user/waiter-list";
    }

    @GetMapping({"/resend-email/{id}"})
    public String resendEmail(@PathVariable("id") Long id,
                              RedirectAttributes redirectAttributes) {
        try{
            registrationTokenService.resendEmailInvitation(id);
            redirectAttributes.addFlashAttribute("success", "Đã gửi lại email mời.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi gửi email, hãy thử lại hoặc liên hệ quản trị viên.");
        }
        return "redirect:/users/waiter-list";
    }

    @GetMapping("/delete-email/{id}")
    public String deleteEmail(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            registrationTokenService.deleteInvitationEmailById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa email thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Xóa email thất bại");
        }
        return "redirect:/users/waiter-list";
    }



    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        UserDTO userDTO = userService.findUserById(id);
        if(!Objects.equals(userDTO.getWarehouseId(), getWarehouseId())) {
            return "redirect:/users/list";
        }
        model.addAttribute("user", userDTO);
        return "user/edit";
    }

    @PostMapping("/edit")
    public String edit(
            @Validated @ModelAttribute("user") UserDTO userDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if(Boolean.TRUE.equals(userService.existsByPhoneAndIdNot(userDTO.getPhone(), userDTO.getId()))) {
            bindingResult.rejectValue("phone", "error.phone", "Số điện thoại đã tồn tại");
        }
        if(Boolean.TRUE.equals(userService.existsByEmailAndIdNot(userDTO.getEmail(),userDTO.getId()))) {
            bindingResult.rejectValue("email", "error.email", "Email đã tồn tại");
        }else if(Boolean.FALSE.equals(userService.existsByEmailAndId(userDTO.getEmail(), userDTO.getId()))) {
            userService.sendVerificationCode(userDTO.getEmail());
            redirectAttributes.addFlashAttribute("pendingEmail", userDTO.getEmail());
            return "redirect:/users/verify-email?pendingEmail=" + userDTO.getEmail() + "&userId=" + userDTO.getId();
        }

        if (bindingResult.hasErrors()) {
            return "user/edit";
        }

        var check = userService.findUserById(userDTO.getId());
        if (!Objects.equals(check.getWarehouseId(), getWarehouseId())) {
            return "redirect:/users/list";
        }

        userService.updateUser(userDTO);
        return "redirect:/users/list";
    }

    @GetMapping("/verify-email")
    public String showEmailVerificationPage(@RequestParam(value = "pendingEmail", required = false)
                                                String pendingEmail,
                                            @RequestParam(value = "userId", required = false) Long userId,
                                            Model model) {
        VerificationCodeDTO verificationCodeDTO = new VerificationCodeDTO();
        if(pendingEmail != null) {
            verificationCodeDTO.setEmail(pendingEmail);
            verificationCodeDTO.setId(userId);
            model.addAttribute("verificationCode", verificationCodeDTO);
        }
        return "user/verify-email";
    }

    @PostMapping("/verify-email")
    public String verifyEmail(@ModelAttribute("verificationCode") VerificationCodeDTO verificationCodeDTO,
                              RedirectAttributes redirectAttributes) {
        System.out.println(verificationCodeDTO.getEmail());

        String storedCode = emailService.getStoredVerificationCode(verificationCodeDTO.getEmail());
        System.out.println(storedCode);
        if(!verificationCodeDTO.getCode().equals(storedCode)) {
            redirectAttributes.addFlashAttribute("error", "Mã xác thực không chính xác");
            return "redirect:/users/verify-email";
        }

        userService.updateEmail(verificationCodeDTO.getId(), verificationCodeDTO.getEmail());
        UserDTO userDTO = userService.findUserByEmail(getUser().getEmail());
        if(userDTO != null) {
            System.out.println(userDTO.getEmail());
        }
        return "redirect:/users/list";
    }


    @GetMapping("/detail/{id}")
    public String detailUser(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        UserDTO userDTO = userService.findUserById(id);
        if(userDTO == null) {
            redirectAttributes.addFlashAttribute("error", "Lỗi thông tin người dùng.");
            return "redirect:/users/list";
        }
        System.out.println(userDTO.getBirthday());
        model.addAttribute("birthday", userDTO.getBirthday().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        Map<String, Object> userWithLockedStatus = new HashMap<>();
        userWithLockedStatus.putAll(model.asMap());//giu nguyen cac gia tri cua model
        userWithLockedStatus.put("locked", userDTO.isLocked() ? "Đang khóa" : "Đang hoạt động");

        model.addAttribute("user", userDTO);
        return "user/detail";
    }

}
