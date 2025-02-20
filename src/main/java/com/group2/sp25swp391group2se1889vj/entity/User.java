package com.group2.sp25swp391group2se1889vj.entity;


import com.group2.sp25swp391group2se1889vj.enums.RoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseEntity{

    @Column(name = "username", nullable = false, unique = true, columnDefinition = "nvarchar(50)")
    private String username;

    @Column(name = "password", nullable = false, columnDefinition = "nvarchar(100)")
    private String password;

    @Column(name = "first_name", nullable = false, columnDefinition = "nvarchar(50)")
    private String firstName;

    @Column(name = "last_name", nullable = false, columnDefinition = "nvarchar(50)")
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, columnDefinition = "nvarchar(100)")
    private String email;

    @Column(name = "phone", nullable = false, unique = true, columnDefinition = "nvarchar(20)")
    private String phone;

    @Column(name = "gender", nullable = false, columnDefinition = "bit")
    private Boolean gender;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "address", nullable = false, columnDefinition = "nvarchar(255)")
    private String address;

    @Column(name = "is_locked", nullable = false, columnDefinition = "bit")
    private boolean isLocked = false;

    @Column(name = "lock_reason", columnDefinition = "nvarchar(255)")
    private String lockReason;

    @Column(name = "avatar", columnDefinition = "nvarchar(255)")
    private String avatar;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private Set<User> subordinates = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "nvarchar(20)")
    private RoleType role;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.EAGER)
    private Set<Warehouse> warehouses = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse assignedWarehouse;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.EAGER)
    private Set<Product> products = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<RefreshToken> refreshTokens = new HashSet<>();

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.EAGER)
    private Set<RegistrationToken> registrationTokens = new HashSet<>();
}
