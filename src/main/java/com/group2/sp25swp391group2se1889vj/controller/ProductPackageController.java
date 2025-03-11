package com.group2.sp25swp391group2se1889vj.controller;

import com.group2.sp25swp391group2se1889vj.dto.ProductPackageDTO;
import com.group2.sp25swp391group2se1889vj.dto.ProductPackageFilterDTO;
import com.group2.sp25swp391group2se1889vj.dto.ZoneDTO;
import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.enums.RoleType;
import com.group2.sp25swp391group2se1889vj.repository.ProductRepository;
import com.group2.sp25swp391group2se1889vj.security.CustomUserDetails;
import com.group2.sp25swp391group2se1889vj.service.ProductPackageService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/product-package")
@AllArgsConstructor
public class ProductPackageController {

    private final ProductPackageService productPackageService;
    private final ProductRepository productRepository;

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

    private Map<String, String> createPairs(List<String> fields, List<String> fieldTitles) {
        Map<String, String> pairs = new HashMap<>();
        for (int i = 0; i < fields.size(); i++) {
            pairs.put(fields.get(i), fieldTitles.get(i));
        }
        return pairs;
    }

    @GetMapping("/add")
    public String addProductPackage(Model model) {
        model.addAttribute("productPackageDTO", new ProductPackageDTO());
        return "product-package/add";
    }

    @PostMapping("/add")
    public String addProductPackage(
            @ModelAttribute("productPackageDTO") ProductPackageDTO productPackageDTO,
            BindingResult bindingResult, Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "product-package/add";
        }
        productPackageDTO.setWarehouseId(getWarehouseId());
        productPackageService.addProductPackage(productPackageDTO);
        return "redirect:/product-package/list";
    }

    @GetMapping({"/list", "", "/"})
    public String listProductPackages(
            Model model,
            @ModelAttribute(value = "productPackageFilterDTO", binding = false) ProductPackageFilterDTO productPackageFilterDTO
    ) {
        if (productPackageFilterDTO == null) {
            productPackageFilterDTO = new ProductPackageFilterDTO();
        }
        Sort sortDirection = "asc".equalsIgnoreCase(productPackageFilterDTO.getDirection())
                ? Sort.by(productPackageFilterDTO.getOrderBy()).ascending()
                : Sort.by(productPackageFilterDTO.getOrderBy()).descending();

        List<String> fields = Arrays.asList("name", "weight", "createdAt", "updatedAt");
        Map<String, String> fieldTitles = createPairs(fields, Arrays.asList("tên quy cách", "khối lượng", "Ngày tạo", "Ngày cập nhật"));
        Map<String, String> fieldClasses = createPairs(fields, Arrays.asList("", "weight", "dateTime", "dateTime"));

        model.addAttribute("fields", fields);
        model.addAttribute("fieldTitles", fieldTitles);
        model.addAttribute("fieldClasses", fieldClasses);

        Pageable pageable = PageRequest.of(productPackageFilterDTO.getPage() - 1, productPackageFilterDTO.getSize(), sortDirection);

        Long warehouseId = getWarehouseId();

        Page<ProductPackageDTO> productPackages = productPackageService.searchProductPackages(warehouseId, productPackageFilterDTO, pageable);
        model.addAttribute("productPackages", productPackages);
        model.addAttribute("productPackageFilterDTO", productPackageFilterDTO);

        return "product-package/list";
    }

    @PostMapping({"/list", "", "/"})
    public String listProductPackages(
            @ModelAttribute("productPackageFilterDTO") ProductPackageFilterDTO productPackageFilterDTO,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("productPackageFilterDTO", productPackageFilterDTO);
        return "redirect:/product-package/list";
    }

    @GetMapping("/edit/{id}")
    public String editProductPackage(Model model, @PathVariable("id") Long id) {
        ProductPackageDTO productPackageDTO = productPackageService.findProductPackageById(id);
        if(!Objects.equals(productPackageDTO.getWarehouseId(), getWarehouseId())) {
            return "redirect:/product-package/list";
        }
        model.addAttribute("productPackageDTO", productPackageDTO);
        return "product-package/edit";
    }

    @PostMapping("/edit")
    public String editProductPackage(
            @Validated @ModelAttribute("productPackageDTO") ProductPackageDTO productPackageDTO,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "product-package/edit";
        }
        System.out.println(productPackageDTO.toString());
        if (productPackageDTO.getId() == null) {
            return "redirect:/product-package/list";
        }

        var check = productPackageService.findProductPackageById(productPackageDTO.getId());
        if (check == null || !Objects.equals(check.getWarehouseId(), getWarehouseId())) {
            return "redirect:/product-package/list";
        }

        productPackageDTO.setWarehouseId(getWarehouseId());
        productPackageService.saveProductPackage(productPackageDTO);
        return "redirect:/product-package";
    }

    @GetMapping("/detail/{id}")
    public String detailProductPackage(Model model, @PathVariable("id") Long id) {
        ProductPackageDTO productPackageDTO = productPackageService.findProductPackageById(id);
        if(!Objects.equals(productPackageDTO.getWarehouseId(), getWarehouseId())) {
            return "redirect:/product-package/list";
        }
        model.addAttribute("productPackageDTO", productPackageDTO);
        return "product-package/detail";
    }
}
