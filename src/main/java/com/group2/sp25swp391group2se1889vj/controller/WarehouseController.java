package com.group2.sp25swp391group2se1889vj.controller;

import com.group2.sp25swp391group2se1889vj.exception.Http404;
import com.group2.sp25swp391group2se1889vj.security.CustomUserDetails;
import com.group2.sp25swp391group2se1889vj.util.XSSProtectedUtil;
import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.dto.WarehouseDTO;
import com.group2.sp25swp391group2se1889vj.service.WarehouseService;
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


    @GetMapping("/add")
    public String addWarehouse(Model model) {
        model.addAttribute("warehouse", new WarehouseDTO());
        return "warehouse/add";
    }

    @PostMapping("/add")
    public String addWarehouse(
            @ModelAttribute("warehouse")  @Validated WarehouseDTO warehouseDTO,
            BindingResult bindingResult
    ) {
        if (warehouseService.isExistWarehouseByOwnerIdAndName(getUser().getId(), warehouseDTO.getName())) {
            bindingResult.rejectValue("name", "error.warehouse", "Tên kho đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            return "warehouse/add";
        }
        warehouseDTO.setLocation(xssProtectedUtil.encodeAllHTMLElement(warehouseDTO.getLocation()));
        warehouseDTO.setDescription(xssProtectedUtil.sanitize(warehouseDTO.getDescription()));
        warehouseDTO.setCreatedAt(LocalDateTime.now());
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

        List<String> fields = Arrays.asList("name", "location", "createdAt");
        if (!fields.contains(searchBy)) {
            searchBy = "name";
        }
        if (!fields.contains(orderBy)) {
            orderBy = "createdAt";
        }

        Map<String, String> fieldTitles = createPairs(fields, Arrays.asList("Tên kho", "Địa chỉ", "Ngày tạo"));
        Map<String, String> fieldClasses = createPairs(fields, Arrays.asList("", "", "dateTime"));
        List<String> searchAbleFields = Arrays.asList("name", "location");


        model.addAttribute("fields", fields);
        model.addAttribute("fieldTitles", fieldTitles);
        model.addAttribute("fieldClasses", fieldClasses);
        model.addAttribute("searchAbleFields", searchAbleFields);

        Sort sortDirection = "asc".equalsIgnoreCase(direction)
                ? Sort.by(orderBy).ascending()
                : Sort.by(orderBy).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sortDirection);

        Page<WarehouseDTO> warehouses;
        if (search != null && !search.isEmpty()) {
            warehouses = switch (searchBy) {
                case "name" -> warehouseService.findPaginatedWarehousesByOwnerIdAndNameContaining(
                        getUser().getId(), search, pageable);
                case "location" -> warehouseService.findPaginatedWarehousesByOwnerIdAndLocationContaining(
                        getUser().getId(), search, pageable);
                default -> warehouseService.findPaginatedWarehousesByOwnerId(getUser().getId(), pageable);
            };
        } else {
            warehouses = warehouseService.findPaginatedWarehousesByOwnerId(getUser().getId(), pageable);
        }

        model.addAttribute("warehouses", warehouses);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("search", search);
        model.addAttribute("orderBy", orderBy);
        model.addAttribute("searchBy", searchBy);
        model.addAttribute("direction", direction);

        return "warehouse/list";
    }

    @GetMapping("/edit/{id}")
    public String editWarehouse(
            Model model,
            @PathVariable Long id
    ) {
        WarehouseDTO warehouseDTO  = warehouseService.findWarehouseById(id);

        if (warehouseDTO == null) {
            throw new Http404("Không tìm thấy kho hàng mà bạn yêu cầu");
        }

        if (!warehouseDTO.getCreatedBy().equals(getUser().getId())) {
            throw new Http404("Bạn không có quyền truy cập kho hàng này");
        }

        model.addAttribute("warehouse", warehouseDTO);
        return "warehouse/edit";
    }

    @PostMapping("/edit")
    public String editWarehouse(
            @ModelAttribute("warehouse") @Validated WarehouseDTO warehouseDTO,
            BindingResult bindingResult
    ) {
        WarehouseDTO oldWarehouse = warehouseService.findWarehouseById(warehouseDTO.getId());
        String newName = xssProtectedUtil.encodeAllHTMLElement(warehouseDTO.getName());

        if(!oldWarehouse.getName().equals(newName) && warehouseService.searchWarehouseByOwnerIdAndName(getUser().getId(), newName) != null) {
            bindingResult.rejectValue("name", "error.warehouse", "Tên kho đã tồn tại");
            return "warehouse/edit";
        }
        else{
            warehouseDTO.setName(newName);
        }
        warehouseDTO.setLocation(xssProtectedUtil.encodeAllHTMLElement(warehouseDTO.getLocation()));
        warehouseDTO.setDescription(xssProtectedUtil.sanitize(warehouseDTO.getDescription()));
        warehouseService.updateWarehouse(warehouseDTO);
        return "redirect:/warehouse";
    }

    @GetMapping("/detail/{id}")
    public String detailWarehouse(
            Model model,
            @PathVariable Long id
    ) {
        WarehouseDTO warehouseDTO = warehouseService.findWarehouseById(id);
        model.addAttribute("warehouse", warehouseDTO);
        return "warehouse/detail";
    }

    @GetMapping("/delete/{id}")
    public String deleteWarehouse(
            @PathVariable Long id
    ) {
        WarehouseDTO warehouseDTO = warehouseService.findWarehouseById(id);
        if (warehouseDTO == null) {
            throw new Http404("Không tìm thấy kho hàng mà bạn yêu cầu");
        }

        if (!warehouseDTO.getCreatedBy().equals(getUser().getId())) {
            throw new Http404("Bạn không có quyền truy cập kho hàng này");
        }

        warehouseService.deleteWarehouseById(id);
        return "redirect:/warehouse";
    }




}

