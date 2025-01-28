package com.group2.sp25swp391group2se1889vj.auth.filter;

import com.group2.sp25swp391group2se1889vj.auth.security.CustomUserDetails;
import com.group2.sp25swp391group2se1889vj.auth.security.JwtTokenProvider;
import com.group2.sp25swp391group2se1889vj.auth.security.RefreshTokenProvider;
import com.group2.sp25swp391group2se1889vj.auth.service.CustomUserDetailsService;
import com.group2.sp25swp391group2se1889vj.common.util.CookieUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.auth.login.AccountLockedException;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RefreshTokenProvider refreshTokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CookieUtil cookieUtil;



    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        try {
            var contextPath = request.getContextPath();
            var requestURI = request.getRequestURI();

            if (requestURI.startsWith(contextPath + "/error")) {
                filterChain.doFilter(request, response);
                return;
            }

            var jwt = getJwtFromRequest(request);
            var refreshToken = getRefreshTokenFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                Long userId = jwtTokenProvider.getUserIdFromToken(jwt);
                var userDetails = customUserDetailsService.loadUserById(userId);
                if (userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    if (!userDetails.isAccountNonLocked()) {
                        var message = messageSource.getMessage("user.locked", new Object[]{((CustomUserDetails) userDetails).getUser().getLockReason()}, request.getLocale());
                        throw new AccountLockedException(message);
                    }

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } else if (StringUtils.hasText(refreshToken) && refreshTokenProvider.validateRefreshToken(refreshToken)) {
                var key = refreshTokenProvider.getKeyFromRefreshToken(refreshToken);
                var customUserDetails = customUserDetailsService.loadUserByRefreshToken(key);
                if (customUserDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    if (!customUserDetails.isAccountNonLocked()) {
                        var message = messageSource.getMessage("user.locked", new Object[]{customUserDetails.getUser().getLockReason()}, request.getLocale());
                        throw new AccountLockedException(message);
                    }

                    var newJwtToken = jwtTokenProvider.generateToken(customUserDetails);
                    var jwtCookie = new Cookie("jwtToken", newJwtToken);
                    jwtCookie.setPath("/");
                    jwtCookie.setHttpOnly(true);
                    jwtCookie.setMaxAge(Integer.parseInt(Long.parseLong(jwtTokenProvider.getExpiration()) / 1000L + ""));
                    response.addCookie(jwtCookie);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            customUserDetails, null, customUserDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

            } else {
                SecurityContextHolder.clearContext();
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        var bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        var cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String getRefreshTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
