package com.group2.sp25swp391group2se1889vj.controller;

import com.group2.sp25swp391group2se1889vj.dto.ChangePasswordDTO;
import com.group2.sp25swp391group2se1889vj.dto.LoginDTO;
import com.group2.sp25swp391group2se1889vj.dto.RegisterDTO;
import com.group2.sp25swp391group2se1889vj.dto.ResetPasswordDTO;
import com.group2.sp25swp391group2se1889vj.entity.RefreshToken;
import com.group2.sp25swp391group2se1889vj.entity.ResetPasswordToken;
import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.exception.Http400;
import com.group2.sp25swp391group2se1889vj.exception.InvalidRegistrationTokenException;
import com.group2.sp25swp391group2se1889vj.repository.RefreshTokenRepository;
import com.group2.sp25swp391group2se1889vj.repository.RegistrationTokenRepository;
import com.group2.sp25swp391group2se1889vj.repository.ResetPasswordTokenRepository;
import com.group2.sp25swp391group2se1889vj.repository.UserRepository;
import com.group2.sp25swp391group2se1889vj.security.CustomUserDetails;
import com.group2.sp25swp391group2se1889vj.security.JwtTokenProvider;
import com.group2.sp25swp391group2se1889vj.security.RecaptchaService;
import com.group2.sp25swp391group2se1889vj.security.RefreshTokenProvider;
import com.group2.sp25swp391group2se1889vj.service.EmailService;
import com.group2.sp25swp391group2se1889vj.service.StorageService;
import com.group2.sp25swp391group2se1889vj.service.UserService;
import com.group2.sp25swp391group2se1889vj.util.CookieUtil;
import com.group2.sp25swp391group2se1889vj.util.EncryptionUtil;
import com.group2.sp25swp391group2se1889vj.validation.annotation.RecaptchaRequired;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RegistrationTokenRepository registrationTokenRepository;
    private final EncryptionUtil encryptionUtil;
    private final CookieUtil cookieUtil;
    private final StorageService storageService;
    private final UserService userService;
    private final EmailService emailService;

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
    @RecaptchaRequired
    public String register(
            @Validated @ModelAttribute("registerDTO") RegisterDTO registerDTO,
            BindingResult bindingResult
    ) throws Exception {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        userService.registerUser(registerDTO);
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
    @RecaptchaRequired
    public String profile(@RequestParam("avatar") String avatar,
                          RedirectAttributes redirectAttributes
    ) {
        User user = getUser();
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            storageService.deleteFile(user.getAvatar());
        }
        user.setAvatar(avatar);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("flashMessage", "Cập nhật ảnh đại diện thành công");
        redirectAttributes.addFlashAttribute("flashMessageType", "success");
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

    @GetMapping("forgot-password")
    public String forgotPassword() {
        return "auth/forgot-password";
    }

    @PostMapping("forgot-password")
    public String forgotPassword(
            @RequestParam("email") String email,
            Model model
    ) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            model.addAttribute("error", "Email không tồn tại");
            return "auth/forgot-password";
        }

        ResetPasswordToken token = resetPasswordTokenRepository.findByUserEmail(email);
        LocalDateTime now = LocalDateTime.now();

        if (token != null) {
            if (token.getUpdatedAt().plusHours(1).isAfter(now)) {
                String expiredTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(token.getUpdatedAt().plusHours(1));
                model.addAttribute("error", "Một yêu cầu đã được gửi trước đó, vui lòng kiểm tra email của bạn hoặc thử lại sau " + expiredTime);
                return "auth/forgot-password";
            }
            token.setToken(UUID.randomUUID().toString());
        } else {
            token = new ResetPasswordToken();
            token.setToken(UUID.randomUUID().toString());
            token.setUser(user);
        }

        resetPasswordTokenRepository.save(token);

        String resetLink = "http://localhost:8080/reset-password?token=" + token.getToken();
        emailService.sendHTMLMail(email, "Reset password", "Vui lòng click vào link sau để reset mật khẩu: <a href='" + resetLink + "'>Reset password</a>");

        model.addAttribute("success", "Một email đã được gửi tới bạn, vui lòng kiểm tra email của bạn");
        return "auth/forgot-password";
    }

    @GetMapping("reset-password")
    public String resetPassword(
            @RequestParam("token") String token,
            Model model
    ) {
        ResetPasswordToken resetPasswordToken = resetPasswordTokenRepository.findByToken(token);
        if (resetPasswordToken == null) {
            throw new Http400("Token không hợp lệ, vui lòng thử lại");
        }
        if (resetPasswordToken.getUpdatedAt().plusHours(1).isBefore(LocalDateTime.now())) {
            throw new Http400("Token đã hết hạn, vui lòng truy cập lại trang quên mật khẩu để nhận một token mới");
        }
        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setToken(token);
        model.addAttribute("resetPasswordDTO", resetPasswordDTO);
        return "auth/reset-password";
    }

    @PostMapping("reset-password")
    public String resetPassword(
            @Validated @ModelAttribute("resetPasswordDTO") ResetPasswordDTO resetPasswordDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/reset-password";
        }
        ResetPasswordToken resetPasswordToken = resetPasswordTokenRepository.findByToken(resetPasswordDTO.getToken());
        if (resetPasswordToken == null) {
            throw new Http400("Token không hợp lệ, vui lòng thử lại");
        }
        if (resetPasswordToken.getUpdatedAt().plusHours(1).isBefore(LocalDateTime.now())) {
            throw new Http400("Token đã hết hạn, vui lòng truy cập lại trang quên mật khẩu để nhận một token mới");
        }
        User user = resetPasswordToken.getUser();
        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getPassword()));
        userRepository.save(user);
        resetPasswordTokenRepository.delete(resetPasswordToken);
        redirectAttributes.addFlashAttribute("flashMessage", "Đổi mật khẩu thành công");
        redirectAttributes.addFlashAttribute("flashMessageType", "success");
        return "redirect:/login";
    }



}