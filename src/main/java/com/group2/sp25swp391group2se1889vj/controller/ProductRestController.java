package com.group2.sp25swp391group2se1889vj.controller;

import com.group2.sp25swp391group2se1889vj.dto.ProductDTO;
import com.group2.sp25swp391group2se1889vj.entity.Product;
import com.group2.sp25swp391group2se1889vj.entity.ProductPackage;
import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.enums.RoleType;
import com.group2.sp25swp391group2se1889vj.security.CustomUserDetails;
import com.group2.sp25swp391group2se1889vj.service.ProductPackageService;
import com.group2.sp25swp391group2se1889vj.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/product")
public class ProductRestController {
    
}
