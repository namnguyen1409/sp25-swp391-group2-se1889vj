package com.group2.sp25swp391group2se1889vj.controller;

import com.group2.sp25swp391group2se1889vj.dto.ChangePasswordDTO;
import com.group2.sp25swp391group2se1889vj.security.RecaptchaService;
import com.group2.sp25swp391group2se1889vj.dto.LoginDTO;
import com.group2.sp25swp391group2se1889vj.dto.RegisterDTO;
import com.group2.sp25swp391group2se1889vj.exception.InvalidRegistrationTokenException;
import com.group2.sp25swp391group2se1889vj.security.CustomUserDetails;
import com.group2.sp25swp391group2se1889vj.security.JwtTokenProvider;
import com.group2.sp25swp391group2se1889vj.security.RefreshTokenProvider;
import com.group2.sp25swp391group2se1889vj.service.StorageService;
import com.group2.sp25swp391group2se1889vj.util.CookieUtil;
import com.group2.sp25swp391group2se1889vj.util.EncryptionUtil;
import com.group2.sp25swp391group2se1889vj.entity.RefreshToken;
import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.repository.RefreshTokenRepository;
import com.group2.sp25swp391group2se1889vj.repository.RegistrationTokenRepository;
import com.group2.sp25swp391group2se1889vj.repository.UserRepository;
import com.group2.sp25swp391group2se1889vj.validation.annotation.RecaptchaRequired;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
@Data
@Transactional
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final RecaptchaService recaptchaService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RegistrationTokenRepository registrationTokenRepository;
    private final EncryptionUtil encryptionUtil;
    private final CookieUtil cookieUtil;
    private final StorageService storageService;

    @Value("${refresh.token.expiration}")
    private String refreshTokenExpiration;

    @Value("${jwt.expiration}")
    private String jwtTokenExpiration;

    private User getUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }

    @GetMapping("/login")
    public String login(Model model) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof CustomUserDetails) {
            return "redirect:/";
        }
        model.addAttribute("title", "Login");
        model.addAttribute("loginDTO", new LoginDTO());

        return "auth/login";
    }

    @PostMapping("/login")
    @RecaptchaRequired
    public String login(
            @ModelAttribute("loginDTO") @Validated LoginDTO loginDTO,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/login";
        }

        Optional<User> userOptional = userRepository.findByUsername(loginDTO.getUsername());
        if (userOptional.isEmpty() || !passwordEncoder.matches(loginDTO.getPassword(), userOptional.get().getPassword())) {
            bindingResult.rejectValue("password", "error.login", "Mật khẩu không đúng");
            return "auth/login";
        }
        var user = userOptional.get();
        var jwt = jwtTokenProvider.generateToken(new CustomUserDetails(user));
        if (Boolean.TRUE.equals(loginDTO.getRemember())) {
            var refreshToken = refreshTokenProvider.generateRefreshToken(UUID.randomUUID().toString());
            RefreshToken refreshTokenEntity = new RefreshToken();
            refreshTokenEntity.setToken(refreshTokenProvider.getKeyFromRefreshToken(refreshToken));
            refreshTokenEntity.setUser(user);
            refreshTokenEntity.setExpiryDate(LocalDateTime.now().plusSeconds(Long.parseLong(refreshTokenExpiration)));
            refreshTokenRepository.save(refreshTokenEntity);
            cookieUtil.addCookie("jwtToken", jwt, Integer.parseInt(Long.parseLong(jwtTokenExpiration) / 1000L + ""), "/", true, false);
            cookieUtil.addCookie("refreshToken", refreshToken, Integer.parseInt(Long.parseLong(refreshTokenExpiration) / 1000L + ""), "/", true, false);
        } else {
            cookieUtil.addCookie("jwtToken", jwt, Integer.parseInt(Long.parseLong(jwtTokenExpiration) / 1000L + ""), "/", true, false);
        }

        return "redirect:/";
    }

    @GetMapping("/register")
    public String register(
            @RequestParam("token") String token,
            Model model
    ) throws Exception {


        var registrationToken = registrationTokenRepository.findByToken(encryptionUtil.encrypt(token));
        if (registrationToken == null) {
            throw new InvalidRegistrationTokenException("Token không hợp lệ, liên hệ với quản trị viên để được hỗ trợ");
        }
        if (registrationToken.getUpdatedAt().plusDays(1).isBefore(LocalDateTime.now())) {
            throw new InvalidRegistrationTokenException("Token đã hết hạn, liên hệ với quản trị viên để được hỗ trợ");
        }

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setToken(token);
        model.addAttribute("registerDTO", registerDTO);
        model.addAttribute("title", "register");
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Validated @ModelAttribute("registerDTO") RegisterDTO registerDTO,
            BindingResult bindingResult,
            @RequestParam("g-recaptcha-response") String recaptchaResponse
    ) throws Exception {
        if (!recaptchaService.verifyRecaptcha(recaptchaResponse)) {
            bindingResult.rejectValue("recaptchaResponse", "error.recaptcha", "Vui lòng xác minh bạn không phải là robot");
        }
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        var registrationToken = registrationTokenRepository.findByToken(encryptionUtil.encrypt(registerDTO.getToken()));
        if (registrationToken == null) {
            throw new InvalidRegistrationTokenException("Token không hợp lệ, liên hệ với quản trị viên để được hỗ trợ");
        }
        if (registrationToken.getUpdatedAt().plusDays(1).isBefore(LocalDateTime.now())) {
            throw new InvalidRegistrationTokenException("Token đã hết hạn, liên hệ với quản trị viên để được hỗ trợ");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPass()));
        user.setFirstName(registerDTO.getFirstName());
        user.setLastName(registerDTO.getLastName());
        user.setPhone(registerDTO.getPhone());
        user.setGender(registerDTO.isGender());
        user.setBirthday(LocalDate.parse(registerDTO.getDob()));
        user.setAddress(registerDTO.getAddress());
        user.setEmail(registrationToken.getEmail());
        user.setRole(registrationToken.getRole());
        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(
    ) {
        var refreshToken = cookieUtil.getCookie("refreshToken");
        if (refreshToken != null) {
            refreshTokenRepository.deleteByToken(refreshTokenProvider.getKeyFromRefreshToken(refreshToken));
        }
        cookieUtil.deleteCookie("jwtToken", "/", true, false);
        cookieUtil.deleteCookie("refreshToken", "/", true, false);
        return "redirect:/login";
    }

    @GetMapping("profile")
    public String profile(Model model) {
        model.addAttribute("title", "Profile");
        return "auth/profile";
    }

    @PostMapping("profile")
    public String profile(@RequestParam("avatar") String avatar,
                          @RequestParam("g-recaptcha-response") String recaptchaResponse,
                          RedirectAttributes redirectAttributes
    ) {
        if (!recaptchaService.verifyRecaptcha(recaptchaResponse)) {
            redirectAttributes.addFlashAttribute("flashMessage", "Vui lòng xác minh bạn không phải là robot");
            redirectAttributes.addFlashAttribute("flashMessageType", "danger");
        } else {
            User user = getUser();
            if (user.getAvatar()!= null &&!user.getAvatar().isEmpty()) {
                storageService.deleteFile(user.getAvatar());
            }
            user.setAvatar(avatar);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("flashMessage", "Cập nhật ảnh đại diện thành công");
            redirectAttributes.addFlashAttribute("flashMessageType", "success");
        }

        return "redirect:/profile";
    }

    @GetMapping("change-password")
    public String changePassword(Model model) {
        model.addAttribute("changePasswordDTO", new ChangePasswordDTO());
        return "auth/change-password";
    }

    @PostMapping("change-password")
    public String changePassword(
            @Validated @ModelAttribute("changePasswordDTO") ChangePasswordDTO changePasswordDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/change-password";
        }
        User user = getUser();
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            bindingResult.rejectValue("oldPassword", "error.changePassword", "Mật khẩu cũ không đúng");
            return "auth/change-password";
        }
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getPassword()));
        userRepository.save(user);
        return "redirect:/change-password";
    }




}