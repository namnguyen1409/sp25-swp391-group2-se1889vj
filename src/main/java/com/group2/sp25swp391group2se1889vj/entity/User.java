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

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseEntity{

    @EqualsAndHashCode.Include
    @Column(name = "username", nullable = false, unique = true, columnDefinition = "nvarchar(50)")
    private String username;

    @EqualsAndHashCode.Include
    @Column(name = "password", nullable = false, columnDefinition = "nvarchar(100)")
    private String password;

    @EqualsAndHashCode.Include
    @Column(name = "first_name", nullable = false, columnDefinition = "nvarchar(50)")
    private String firstName;

    @EqualsAndHashCode.Include
    @Column(name = "last_name", nullable = false, columnDefinition = "nvarchar(50)")
    private String lastName;

    @EqualsAndHashCode.Include
    @Column(name = "email", nullable = false, unique = true, columnDefinition = "nvarchar(100)")
    private String email;

    @EqualsAndHashCode.Include
    @Column(name = "phone", nullable = false, unique = true, columnDefinition = "nvarchar(20)")
    private String phone;

    @EqualsAndHashCode.Include
    @Column(name = "gender", nullable = false, columnDefinition = "bit")
    private Boolean gender;

    @EqualsAndHashCode.Include
    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @EqualsAndHashCode.Include
    @Column(name = "address", nullable = false, columnDefinition = "nvarchar(255)")
    private String address;

    @Column(name = "is_locked", nullable = false, columnDefinition = "bit")
    private boolean isLocked = false;

    @Column(name = "lock_reason", columnDefinition = "nvarchar(255)")
    private String lockReason;

    @Column(name = "avatar", columnDefinition = "nvarchar(255)")
    private String avatar;

    @OneToMany(mappedBy = "owner")
    private Set<User> subordinates = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "nvarchar(20)")
    private RoleType role;

    @OneToOne(mappedBy = "owner")
    private Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse assignedWarehouse;

    @OneToMany(mappedBy = "createdBy")
    private Set<Product> products = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<RefreshToken> refreshTokens = new HashSet<>();

    @OneToMany(mappedBy = "createdBy")
    private Set<RegistrationToken> registrationTokens = new HashSet<>();
}
