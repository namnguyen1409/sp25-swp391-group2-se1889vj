package com.group2.sp25swp391group2se1889vj.controller;

import com.group2.sp25swp391group2se1889vj.dto.ProductDTO;
import com.group2.sp25swp391group2se1889vj.dto.ProductFilterDTO;
import com.group2.sp25swp391group2se1889vj.dto.ZoneDTO;
import com.group2.sp25swp391group2se1889vj.entity.ProductPackage;
import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.enums.RoleType;
import com.group2.sp25swp391group2se1889vj.mapper.ZoneMapper;
import com.group2.sp25swp391group2se1889vj.security.CustomUserDetails;
import com.group2.sp25swp391group2se1889vj.security.RecaptchaService;
import com.group2.sp25swp391group2se1889vj.service.ProductPackageService;
import com.group2.sp25swp391group2se1889vj.service.ProductService;
import com.group2.sp25swp391group2se1889vj.service.StorageService;
import com.group2.sp25swp391group2se1889vj.service.ZoneService;
import com.group2.sp25swp391group2se1889vj.util.XSSProtectedUtil;
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

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/product")
@AllArgsConstructor
public class ProductController {

    private final RecaptchaService recaptchaService;
    private final ProductService productService;
    private final XSSProtectedUtil xssProtectedUtil;
    private final StorageService storageService;

    private final ProductPackageService productPackageService;
    private final ZoneService zoneService;

    private User getUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }

    private Map<String, String> createPairs(List<String> fields, List<String> fieldTitles) {
        if (!fields.isEmpty() && fields.size() == fieldTitles.size()) {
            Map<String, String> pairs = new HashMap<>();
            for (int i = 0; i < fields.size(); i++) {
                pairs.put(fields.get(i), fieldTitles.get(i));
            }
            return pairs;
        }
        return Collections.emptyMap();
    }


    private Long getWarehouseId() {
        var currentUser = getUser();
        if(currentUser.getRole() == RoleType.OWNER) return currentUser.getWarehouse().getId();
        else return currentUser.getAssignedWarehouse().getId();
    }



    @GetMapping("/add")
    public String addProduct(Model model) {
        model.addAttribute("product", new ProductDTO());
        return "product/add";
    }

    @PostMapping("/add")
    public String addProduct(
            @ModelAttribute("product") @Validated ProductDTO productDTO,
            BindingResult bindingResult,
            @RequestParam("g-recaptcha-response") String recaptchaResponse,
            RedirectAttributes redirectAttributes
    ) {
        if (productDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            bindingResult.rejectValue("price", "price.error", "Giá bán phải lớn hơn 0");
        }

        if (bindingResult.hasErrors()) {
            return "product/add";
        }

        // Lấy ProductPackage từ ID
        ProductPackage productPackage = productPackageService.findByIdAndWarehouseId(productDTO.getProductPackageId(), getWarehouseId());
        if (productPackage == null) {
            bindingResult.rejectValue("productPackageId", "package.error", "Quy cách đóng gói không hợp lệ");
            return "product/add";
        }

        // Xử lý ảnh và nội dung
        var image = storageService.moveToUploads(productDTO.getImage());
        productDTO.setImage(image);
        productDTO.setDescription(xssProtectedUtil.sanitize(productDTO.getDescription()));
        productDTO.setDescriptionPlainText(xssProtectedUtil.htmlToPlainText(productDTO.getDescription()));


        // Lưu sản phẩm
//        productService.saveProduct(productDTO, productPackage);
        redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm thành công");

        return "redirect:/product/list";
    }

    @GetMapping({"", "/", "/list"})
    public String listProduct(
            Model model,
            @ModelAttribute(value = "productFilterDTO", binding = false) ProductFilterDTO productFilterDTO
    ) {
        if(productFilterDTO == null) {
            productFilterDTO = new ProductFilterDTO();
        }

        Sort sortDirection = "asc".equalsIgnoreCase(productFilterDTO.getDirection())
                ? Sort.by(productFilterDTO.getOrderBy()).ascending()
                : Sort.by(productFilterDTO.getOrderBy()).descending();

        List<String> fields = Arrays.asList("image", "name", "price", "stockQuantity" ,"createdAt");
        Map<String, String> fieldTitles = createPairs(fields, Arrays.asList("Hình ảnh", "Tên", "Giá", "Số lượng", "Ngày tạo"));
        Map<String, String> fieldClasses = createPairs(fields, Arrays.asList("image", "", "price", "", "datetime"));

        model.addAttribute("fields", fields);
        model.addAttribute("fieldTitles", fieldTitles);
        model.addAttribute("fieldClasses", fieldClasses);

        Pageable pageable = PageRequest.of(productFilterDTO.getPage()-1, productFilterDTO.getSize(), sortDirection);

        Long warehouseId = getWarehouseId();
        Page<ProductDTO> products = productService.searchProducts(warehouseId, productFilterDTO, pageable);

        model.addAttribute("products", products);
        model.addAttribute("productFilterDTO", productFilterDTO);

        return "product/list";
    }

    @PostMapping({"/list", "", "/"})
    public String list(
            @ModelAttribute(value = "productFilterDTO") ProductFilterDTO productFilterDTO,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("productFilterDTO", productFilterDTO);
        return "redirect:/product";
    }

    @GetMapping("/detail/{id}")
    public String detailProduct(@PathVariable Long id, Model model) {
        ProductDTO product = productService.findProductByIdAndWarehouseId(id, getWarehouseId());
        if (product == null) {
            return "redirect:/product";
        }

        model.addAttribute("product", product);
        return "product/detail";
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        ProductDTO product = productService.findProductByIdAndWarehouseId(id, getWarehouseId());
        if (product == null) {
            return "redirect:/product";
        }
        List<ZoneDTO> zones = zoneService.findAllByWarehouseId(getWarehouseId());
        model.addAttribute("zones", zones);
        model.addAttribute("productPackage", productPackageService.findProductPackageById(product.getProductPackageId()));
        model.addAttribute("productDTO", product);
        return "product/edit";
    }

    @PostMapping("/edit/{id}")
    public String editProduct(
            @PathVariable Long id,
            @Validated @ModelAttribute("productDTO") ProductDTO productDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        try {
            ProductDTO product = productService.findProductByIdAndWarehouseId(id, getWarehouseId());
            if (product == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm");
                return "redirect:/product/list";
            }

            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            product.setProductPackageId(productDTO.getProductPackageId());
            product.setZoneIds(productDTO.getZoneIds());

            ProductPackage productPackage = productPackageService.findByIdAndWarehouseId(productDTO.getProductPackageId(), getWarehouseId());
            if (productPackage == null) {
                redirectAttributes.addFlashAttribute("error", "Quy cách đóng gói không hợp lệ");
                return "redirect:/product/edit/" + id;
            }

            productService.updateProduct(id, product);
            redirectAttributes.addFlashAttribute("success", "Sửa sản phẩm thành công");
            return "redirect:/product/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi sửa sản phẩm: " + e.getMessage());
            return "redirect:/product/edit/" + id;
        }
    }
}
