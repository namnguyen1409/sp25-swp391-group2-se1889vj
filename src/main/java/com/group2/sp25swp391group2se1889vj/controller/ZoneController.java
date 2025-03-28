package com.group2.sp25swp391group2se1889vj.controller;

import com.group2.sp25swp391group2se1889vj.dto.CustomerDTO;
import com.group2.sp25swp391group2se1889vj.dto.ZoneDTO;
import com.group2.sp25swp391group2se1889vj.dto.ZoneFilterDTO;
import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.entity.Zone;
import com.group2.sp25swp391group2se1889vj.enums.RoleType;
import com.group2.sp25swp391group2se1889vj.security.CustomUserDetails;
import com.group2.sp25swp391group2se1889vj.service.WarehouseService;
import com.group2.sp25swp391group2se1889vj.service.ZoneService;
import com.group2.sp25swp391group2se1889vj.util.XSSProtectedUtil;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
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

@AllArgsConstructor
@Controller
@RequestMapping("/zone")
public class ZoneController {

    private final ZoneService zoneService;
    private final XSSProtectedUtil xssProtectedUtil;

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
    public String addZone(Model model) {
        model.addAttribute("zone", new ZoneDTO());
        return "warehouse/zone/add";
    }

    @PostMapping("/add")
    public String addZone(@ModelAttribute ZoneDTO zone) {
        zone.setWarehouseId(getWarehouseId());
        zoneService.saveZone(zone);
        return "redirect:/zone";
    }

    @GetMapping("/edit/{id}")
    public String editZone(Model model, @PathVariable("id") Long id) {
        ZoneDTO zone = zoneService.findZoneById(id);
        if(!Objects.equals(zone.getWarehouseId(), getWarehouseId())) {
            return "redirect:/zone";
        }
        model.addAttribute("zone", zone);
        return "warehouse/zone/edit";
    }
    @PostMapping("/edit")
    public String editZone(
            @Validated @ModelAttribute("zone") ZoneDTO zoneDTO,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            System.err.println("DDax co loi");
            return "warehouse/zone/edit";
        }

        // Kiểm tra nếu zoneDTO không có ID hợp lệ
        if (zoneDTO.getId() == null) {
            return "redirect:/zone/list";
        }

        var check = zoneService.findZoneById(zoneDTO.getId());
        if (check == null || !Objects.equals(check.getWarehouseId(), getWarehouseId())) {
            return "redirect:/zone/list";
        }

        zoneDTO.setWarehouseId(getWarehouseId());
        zoneService.saveZone(zoneDTO);
        return "redirect:/zone";
    }


    @GetMapping({"/list", "", "/"})
    public String list(
            Model model,
            @ModelAttribute(value = "zoneFilterDTO", binding = false) ZoneFilterDTO zoneFilterDTO
    ) {
        if(zoneFilterDTO == null) {
            zoneFilterDTO = new ZoneFilterDTO();
        }

        Sort sortDirection = "asc".equalsIgnoreCase(zoneFilterDTO.getDirection())
                ? Sort.by(zoneFilterDTO.getOrderBy()).ascending()
                : Sort.by(zoneFilterDTO.getOrderBy()).descending();

        List<String> fields = Arrays.asList("name", "productName", "productImage", "createdAt", "updatedAt");
        Map<String, String> fieldTitles = createPairs(fields, Arrays.asList("Tên khu vực", "Tên sản phẩm", "Hình ảnh sản phẩm", "Ngày tạo", "Ngày cập nhật"));
        Map<String, String> fieldClasses = createPairs(fields, Arrays.asList("", "", "image", "dateTime", "dateTime"));

        model.addAttribute("fields", fields);
        model.addAttribute("fieldTitles", fieldTitles);
        model.addAttribute("fieldClasses", fieldClasses);

        Pageable pageable = PageRequest.of(zoneFilterDTO.getPage()-1, zoneFilterDTO.getSize(), sortDirection);

        Long warehouseId = getWarehouseId();
        Page<ZoneDTO> zones = zoneService.searchZones(warehouseId, zoneFilterDTO, pageable);

        model.addAttribute("zones", zones);
        model.addAttribute("zoneFilterDTO", zoneFilterDTO);

        return "warehouse/zone/list";
    }

    @PostMapping({"/list", "", "/"})
    public String list(
            @ModelAttribute(value = "zoneFilterDTO") ZoneFilterDTO zoneFilterDTO,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("zoneFilterDTO", zoneFilterDTO);
        return "redirect:/zone";
    }


    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        ZoneDTO zoneDTO = zoneService.findZoneById(id);

        if (!Objects.equals(zoneDTO.getWarehouseId(), getWarehouseId())) {
            return "redirect:/zone";
        }
        model.addAttribute("zone", zoneDTO);
        return "warehouse/zone/detail";
    }
}
