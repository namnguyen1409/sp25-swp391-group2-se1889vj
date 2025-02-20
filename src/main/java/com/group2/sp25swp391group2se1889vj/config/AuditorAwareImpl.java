package com.group2.sp25swp391group2se1889vj.config;

import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.security.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;


/**
 * Lớp AuditorAwareImpl là một triển khai của giao diện {@link AuditorAware},
 * được sử dụng để cung cấp thông tin về người dùng hiện tại cho mục đích kiểm toán (auditing).
 *
 * Lớp này sẽ kiểm tra thông tin xác thực được lưu trong {@link SecurityContextHolder}
 * để xác định người dùng hiện tại. Nếu người dùng đã đăng nhập và là một người dùng tùy chỉnh,
 * lớp sẽ trả về đối tượng {@link User}. Nếu không, kết quả rỗng sẽ được trả về.
 *
 * Lớp này thường được sử dụng trong Spring Data để tự động gán thông tin
 * về "người tạo" hoặc "người thay đổi" trong các thực thể khi chúng được tạo hoặc
 * cập nhật.
 */
@Component
public class AuditorAwareImpl implements AuditorAware<User> {


    /**
     * Phương thức cung cấp thông tin về người dùng hiện tại đang được xác thực
     * trong ứng dụng. Phương thức này được sử dụng trong cơ chế kiểm toán (auditing)
     * để xác định người dùng thực hiện các hoạt động như tạo mới hoặc cập nhật thực thể.
     *
     * @return Một {@link Optional} chứa đối tượng người dùng {@link User} hiện tại nếu người dùng
     * đang được xác thực và là một người dùng tùy chỉnh; nếu không có người dùng hoặc người dùng
     * là "anonymousUser", trả về {@link Optional#empty()}.
     */
    @Override
    public Optional<User> getCurrentAuditor() {
        if (SecurityContextHolder.getContext().getAuthentication() == null
                || SecurityContextHolder.getContext().getAuthentication().getPrincipal() == "anonymousUser") {
            return Optional.empty();
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails customUserDetails) {
            return Optional.of(customUserDetails.getUser());
        }
        return Optional.empty();
    }
}
