package com.group2.sp25swp391group2se1889vj.user.entity;


import com.group2.sp25swp391group2se1889vj.user.roles.RoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, columnDefinition = "nvarchar(50)")
    private String username;

    @Column(name = "password", nullable = false, columnDefinition = "nvarchar(100)")
    private String pass;

    @Column(name = "first_name", nullable = false, columnDefinition = "nvarchar(50)")
    private String firstName;

    @Column(name = "last_name", nullable = false, columnDefinition = "nvarchar(50)")
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, columnDefinition = "nvarchar(100)")
    private String email;

    @Column(name = "phone", nullable = false, unique = true, columnDefinition = "nvarchar(100)")
    private String phone;

    @Column(name = "gender", nullable = false, columnDefinition = "bit")
    private Boolean gender;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "address", nullable = false, columnDefinition = "nvarchar(255)")
    private String address;

    @Column(name = "is_locked", nullable = false, columnDefinition = "bit")
    private boolean isLocked = false;

    @Column(name = "lock_reason", nullable = true, columnDefinition = "nvarchar(255)")
    private String lockReason;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "bit")
    private boolean isDelete = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    @Column(name = "avatar", nullable = true, columnDefinition = "nvarchar(255)")
    private String avatar;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "nvarchar(20)")
    private RoleType role;

//    @OneToMany(mappedBy = "owner")
//    private Set<Inventory> inventories = new HashSet<>();
//
//    @ManyToOne
//    @JoinColumn(name = "inventory_id", nullable = true)
//    private Inventory assignedInventory;
//
//    @OneToMany(mappedBy = "productOwner")
//    private Set<Products> products = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<RefreshToken> refreshTokens = new HashSet<>();

    @OneToMany(mappedBy = "createdBy")
    private Set<RegistrationToken> registrationTokens = new HashSet<>();
}
