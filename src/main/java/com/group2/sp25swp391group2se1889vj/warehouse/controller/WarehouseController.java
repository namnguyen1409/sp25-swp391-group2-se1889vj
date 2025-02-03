package com.group2.sp25swp391group2se1889vj.warehouse.controller;

import com.group2.sp25swp391group2se1889vj.auth.security.CustomUserDetails;
import com.group2.sp25swp391group2se1889vj.common.util.XSSProtectedUtil;
import com.group2.sp25swp391group2se1889vj.user.entity.User;
import com.group2.sp25swp391group2se1889vj.warehouse.dto.WarehouseDTO;
import com.group2.sp25swp391group2se1889vj.warehouse.entity.Warehouse;
import com.group2.sp25swp391group2se1889vj.warehouse.service.WarehouseService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor

@Controller
@RequestMapping("/warehouse")
public class WarehouseController {
    private final WarehouseService warehouseService;
    private final XSSProtectedUtil xssProtectedUtil;

    private User getUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }


    private Map<String, String> createPairs(List<String> fields, List<String> fieldTitles) {
        Map<String, String> pairs = new HashMap<>();
        for (int i = 0; i < fields.size(); i++) {
            pairs.put(fields.get(i), fieldTitles.get(i));
        }
        return pairs;
    }

//    @GetMapping
//    public String getList() {
//        return "warehouse/list";
//    }

    @GetMapping("/add")
    public String addWarehouse(
            Model model
    ) {
        model.addAttribute("warehouse", new WarehouseDTO());
        return "warehouse/add";
    }

    @PostMapping("/add")
    public String addWarehouse(
            @ModelAttribute("warehouse")  @Validated WarehouseDTO warehouseDTO,
            BindingResult bindingResult
    ) {
        // Kiểm tra xem kho hàng đã tồn tại chưa
        if (warehouseService.isExistWarehouseByOwnerIdAndName(getUser().getId(), warehouseDTO.getName())) {
            // Nếu kho hàng đã tồn tại, thêm lỗi vào bindingResult
            bindingResult.rejectValue("name", "error.warehouse", "Tên kho đã tồn tại");
        }
        // Nếu có lỗi, trả về trang thêm kho hàng
        if (bindingResult.hasErrors()) {
            return "warehouse/add";
        }

        warehouseDTO.setLocation(xssProtectedUtil.encodeAllHTMLElement(warehouseDTO.getLocation()));
        warehouseDTO.setDescription(xssProtectedUtil.sanitize(warehouseDTO.getDescription()));
        warehouseDTO.setCreatedAt(LocalDateTime.now());
        warehouseDTO.setOwnerId(getUser().getId());
        warehouseService.saveWarehouse(warehouseDTO);
        return "redirect:/warehouse";
    }

    @GetMapping({"", "/", "/list"})
    public String listWarehouse(
            Model model,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "searchBy", required = false, defaultValue = "name") String searchBy,
            @RequestParam(value = "orderBy", required = false, defaultValue = "createdAt") String orderBy,
            @RequestParam(value = "direction", required = false, defaultValue = "desc") String direction
    ) {
        // Cấu hình các trường hiển thị
        List<String> fields = Arrays.asList("name", "location", "createdAt");
        // Kiểm tra xem searchBy có tồn tại trong fields không, nếu không thì gán searchBy = "name"
        if (!fields.contains(searchBy)) {
            searchBy = "name";
        }
        // Kiểm tra xem orderBy có tồn tại trong fields không, nếu không thì gán orderBy = "createdAt"
        if (!fields.contains(orderBy)) {
            orderBy = "createdAt";
        }
        // Tạo các cặp fieldTitles và fieldClasses
        Map<String, String> fieldTitles = createPairs(fields, Arrays.asList("Tên kho", "Địa chỉ", "Ngày tạo"));
        Map<String, String> fieldClasses = createPairs(fields, Arrays.asList("", "", "dateTime"));
        // Các trường có thể tìm kiếm được
        List<String> searchAbleFields = Arrays.asList("name", "location");


        model.addAttribute("fields", fields);
        model.addAttribute("fieldTitles", fieldTitles);
        model.addAttribute("fieldClasses", fieldClasses);
        model.addAttribute("searchAbleFields", searchAbleFields);

        // Tạo đối tượng Pageable để phân trang và sắp xếp
        Sort sortDirection = "asc".equalsIgnoreCase(direction)
                ? Sort.by(orderBy).ascending()
                : Sort.by(orderBy).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sortDirection);

        // Lấy danh sách kho hàng theo ownerId
        Page<WarehouseDTO> warehouses;
        if (search != null && !search.isEmpty()) {
            warehouses = switch (searchBy) {
                // Tìm kiếm theo tên
                case "name" -> warehouseService.findPaginatedWarehousesByOwnerIdAndNameContaining(
                        getUser().getId(), search, pageable);
                // Tìm kiếm theo địa chỉ
                case "location" -> warehouseService.findPaginatedWarehousesByOwnerIdAndLocationContaining(
                        getUser().getId(), search, pageable);
                // Mặc định
                default -> warehouseService.findPaginatedWarehousesByOwnerId(getUser().getId(), pageable);
            };
        } else {
            warehouses = warehouseService.findPaginatedWarehousesByOwnerId(getUser().getId(), pageable);
        }

        // Add additional attributes to the model
        model.addAttribute("warehouses", warehouses);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("search", search);
        model.addAttribute("orderBy", orderBy);
        model.addAttribute("searchBy", searchBy);
        model.addAttribute("direction", direction);

        return "warehouse/list";
    }







}

