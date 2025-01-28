package com.group2.sp25swp391group2se1889vj.auth.controller;

import com.group2.sp25swp391group2se1889vj.auth.dto.LoginDTO;
import com.group2.sp25swp391group2se1889vj.auth.dto.RegisterDTO;
import com.group2.sp25swp391group2se1889vj.auth.exception.InvalidRegistrationTokenException;
import com.group2.sp25swp391group2se1889vj.auth.security.CustomUserDetails;
import com.group2.sp25swp391group2se1889vj.auth.security.JwtTokenProvider;
import com.group2.sp25swp391group2se1889vj.auth.security.RefreshTokenProvider;
import com.group2.sp25swp391group2se1889vj.auth.service.RecaptchaService;
import com.group2.sp25swp391group2se1889vj.common.service.StorageService;
import com.group2.sp25swp391group2se1889vj.common.util.CookieUtil;
import com.group2.sp25swp391group2se1889vj.common.util.EncryptionUtil;
import com.group2.sp25swp391group2se1889vj.user.entity.RefreshToken;
import com.group2.sp25swp391group2se1889vj.user.entity.User;
import com.group2.sp25swp391group2se1889vj.user.repository.RefreshTokenRepository;
import com.group2.sp25swp391group2se1889vj.user.repository.RegistrationTokenRepository;
import com.group2.sp25swp391group2se1889vj.user.repository.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
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
        model.addAttribute("title", "Login");
        model.addAttribute("loginDTO", new LoginDTO());

        return "auth/login";
    }

    @PostMapping("/login")
    public String login(
            @ModelAttribute("loginDTO") @Validated LoginDTO loginDTO,
            BindingResult bindingResult,
            @RequestParam("g-recaptcha-response") String recaptchaResponse
    ) {
        if (!recaptchaService.verifyRecaptcha(recaptchaResponse)) {
            bindingResult.rejectValue("recaptchaResponse", "error.recaptcha", "Vui lòng xác minh bạn không phải là robot");
        }

        if (bindingResult.hasErrors()) {
            return "auth/login";
        }

        Optional<User> userOptional = userRepository.findByUsername(loginDTO.getUsername());
        if (userOptional.isEmpty() || !passwordEncoder.matches(loginDTO.getPassword(), userOptional.get().getPass())) {
            bindingResult.rejectValue("password", "error.login", "Mật khẩu không đúng");
            return "auth/login";
        }
        var user = userOptional.get();
        var jwt = jwtTokenProvider.generateToken(new CustomUserDetails(user));
        if (loginDTO.getRemember()) {
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
        if (registrationToken.getLastUpdated().plusDays(1).isBefore(LocalDateTime.now())) {
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
        if (registrationToken.getLastUpdated().plusDays(1).isBefore(LocalDateTime.now())) {
            throw new InvalidRegistrationTokenException("Token đã hết hạn, liên hệ với quản trị viên để được hỗ trợ");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPass(passwordEncoder.encode(registerDTO.getPass()));
        user.setPass(registerDTO.getPass());
        user.setFirstName(registerDTO.getFirstName());
        user.setLastName(registerDTO.getLastName());
        user.setPhone(registerDTO.getPhone());
        user.setGender(registerDTO.isGender());
        user.setDob(LocalDate.parse(registerDTO.getDob()));
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
        System.out.println(avatar);
        if (!recaptchaService.verifyRecaptcha(recaptchaResponse)) {
            redirectAttributes.addFlashAttribute("flashMessage", "Vui lòng xác minh bạn không phải là robot");
            redirectAttributes.addFlashAttribute("flashMessageType", "danger");
        } else {
            User user = getUser();
            if (user.getAvatar() != null) {
                storageService.deleteFile(user.getAvatar());
            }
            user.setAvatar(avatar);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("flashMessage", "Cập nhật ảnh đại diện thành công");
            redirectAttributes.addFlashAttribute("flashMessageType", "success");
        }

        return "redirect:/profile";
    }


}
