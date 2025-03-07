package com.group2.sp25swp391group2se1889vj.controller;

import com.group2.sp25swp391group2se1889vj.dto.ProductPackageDTO;
import com.group2.sp25swp391group2se1889vj.dto.ProductPackageFilterDTO;
import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.enums.RoleType;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/product-package")
@AllArgsConstructor
public class ProductPackageController {

    private final ProductPackageService productPackageService;

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
        return "redirect:/product-package/add";
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



}
