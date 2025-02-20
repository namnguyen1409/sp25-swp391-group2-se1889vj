package com.group2.sp25swp391group2se1889vj.security;

import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUserDetails(user.get());
    }

    public UserDetails loadUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUserDetails(user.get());
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public CustomUserDetails loadUserByRefreshToken(String refreshToken) {
        Optional<User> user = userRepository.findByRefreshTokens_Token(refreshToken);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUserDetails(user.get());
    }


}
