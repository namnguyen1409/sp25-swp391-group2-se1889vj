package com.group2.sp25swp391group2se1889vj.controller;

import com.group2.sp25swp391group2se1889vj.dto.*;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;



@Controller
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final RegistrationTokenRepository registrationTokenRepository;
    private final RecaptchaService recaptchaService;
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

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @GetMapping({"", "/", "/list"})
    public String listUsers(
            Model model,
            @ModelAttribute(value = "userFilterDTO", binding = false)UserFilterDTO userFilterDTO
            ) {
        if(userFilterDTO == null){
            userFilterDTO = new UserFilterDTO();
        }

        Map<String, RoleType> roleMap = new HashMap<>();
        roleMap.put("ADMIN", RoleType.ADMIN);
        roleMap.put("OWNER", RoleType.OWNER);
        roleMap.put("STAFF", RoleType.STAFF);

        Sort sortDirection = "asc".equalsIgnoreCase(userFilterDTO.getDirection())
                ? Sort.by(userFilterDTO.getOrderBy()).ascending()
                : Sort.by(userFilterDTO.getOrderBy()).descending();


        List<String> fields = Arrays.asList("username", "firstName","lastName", "phone", "email", "address", "role","createdAt");
        Map<String, String> fieldTitles = createPairs(fields, Arrays.asList("Tên đăng nhập",  "Họ", "Tên", "Số điện thoại", "Email",  "Địa chỉ", "Vai trò", "Ngày tạo"));
        Map<String, String> fieldClasses = createPairs(fields, Arrays.asList("text","text", "text", "text","email", "text", "select","date"));
        model.addAttribute("fields", fields);
        model.addAttribute("fieldTitles", fieldTitles);
        model.addAttribute("fieldClasses", fieldClasses);

        Pageable pageable = PageRequest.of(userFilterDTO.getPage() - 1, userFilterDTO.getSize(), sortDirection);

        Page<UserDTO> users = null;
        Long ownerId = null;
        if(getUser().getRole().equals(RoleType.OWNER)) {
            ownerId = getUser().getId();
            users = userService.searchUsers(ownerId, userFilterDTO, pageable);
        }else{
            users = userService.searchUsersByAdmin(userFilterDTO, pageable);
        }

        int n1 = users.getNumber() * users.getSize() + 1;
        if (users.getTotalElements() == 0) {
            n1 = 0;
        }
        int n2 = Math.min((users.getNumber() + 1) * users.getSize(), (int) users.getTotalElements());

        model.addAttribute("n1", n1);
        model.addAttribute("n2", n2);
        model.addAttribute("total", users.getTotalElements());
        model.addAttribute("users", users);
        model.addAttribute("userFilterDTO", userFilterDTO);
        return "user/list";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @PostMapping({"/list", "", "/"})
    public String list(
            @ModelAttribute("userFilterDTO") UserFilterDTO userFilterDTO,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("userFilterDTO", userFilterDTO);
        return "redirect:/users/list";
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @GetMapping("/add")
    public String addUser(Model model) {
            return "user/add";
        }

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

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @GetMapping("/waiter-list")
    public String listWaiter(Model model,
                                @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                @RequestParam(value = "search", required = false) String search,
                                @RequestParam(value = "searchBy", required = false, defaultValue = "email") String searchBy,
                                @RequestParam(value = "orderBy", required = false, defaultValue = "createdAt") String orderBy,
                                @RequestParam(value = "direction", required = false, defaultValue = "desc") String direction

    ) {

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

        model.addAttribute("waiters", waiters);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("search", search);
        model.addAttribute("orderBy", orderBy);
        model.addAttribute("searchBy", searchBy);
        model.addAttribute("direction", direction);
        return "user/waiter-list";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
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

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
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



    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        UserDTO userDTO = userService.findUserById(id);
        if(getUser().getRole().equals(RoleType.OWNER)) {
            if (!Objects.equals(userDTO.getOwnerId(), getUser().getId())) return "redirect:/users/list";
        }else{
            if(userDTO == null) return "redirect:/users/list";
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

    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
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
        String storedCode = emailService.getStoredVerificationCode(verificationCodeDTO.getEmail());
        if(!verificationCodeDTO.getCode().equals(storedCode)) {
            redirectAttributes.addFlashAttribute("error", "Mã xác thực không chính xác");
            return "redirect:/users/verify-email?pendingEmail=" + verificationCodeDTO.getEmail() + "&userId=" + verificationCodeDTO.getId();
        }
        userService.updateEmail(verificationCodeDTO.getId(), verificationCodeDTO.getEmail());
        return "redirect:/users/list";
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @GetMapping("/detail/{id}")
    public String detailUser(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        UserDTO userDTO = userService.findUserById(id);
        if(userDTO == null) {
            redirectAttributes.addFlashAttribute("error", "Lỗi thông tin người dùng.");
            return "redirect:/users/list";
        }
        model.addAttribute("user", userDTO);
        return "user/detail";
    }
}
