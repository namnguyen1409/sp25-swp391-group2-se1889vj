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
    private final ProductService productService;
    private final ProductPackageService productPackageService;

    public ProductRestController(ProductService productService, ProductPackageService productPackageService) {
        this.productService = productService;
        this.productPackageService = productPackageService;
    }

    private User getUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }

    private Long getWarehouseId() {
        var currentUser = getUser();
        if(currentUser.getRole() == RoleType.OWNER) return currentUser.getWarehouse().getId();
        else return currentUser.getAssignedWarehouse().getId();
    }

    @GetMapping("/search")
    public Object search(
            @RequestParam("name") String keyword
    ) {
        return ResponseEntity.ok(productService.searchProducts(getWarehouseId(), keyword));
    }

    @GetMapping("/get/{id}")
    public Object getProduct(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(productService.findProductByIdAndWarehouseId(id, getWarehouseId()));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(
            @Valid @RequestBody ProductDTO productDTO
    ) {
        try {
            productDTO.setWarehouseId(getWarehouseId());
            productService.addProduct(productDTO);
            return ResponseEntity.ok("Thêm sản phẩm thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi thêm sản phẩm: " + e.getMessage());
        }
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<?> editProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO
    ) throws Exception {
        System.out.println("Dữ liệu nhận từ frontend: " + productDTO);
        productDTO.setWarehouseId(getWarehouseId());
        productService.updateProduct(id, productDTO);
        return ResponseEntity.ok("{\"message\": \"Sửa sản phẩm thành công\"}");
    }


}
