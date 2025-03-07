package com.group2.sp25swp391group2se1889vj.controller;

import com.group2.sp25swp391group2se1889vj.dto.ZoneDTO;
import com.group2.sp25swp391group2se1889vj.dto.ZoneFilterDTO;
import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.entity.Zone;
import com.group2.sp25swp391group2se1889vj.security.CustomUserDetails;
import com.group2.sp25swp391group2se1889vj.service.WarehouseService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        zoneService.saveZone(zone);
        return "redirect:/zone";
    }

    @GetMapping("/edit")
    public String editZone(Model model) {
        model.addAttribute("zone", new ZoneDTO());
        return "warehouse/zone/edit";
    }

    @PostMapping("/edit")
    public String editZone(@ModelAttribute ZoneDTO zone) {
        zoneService.saveZone(zone);
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

        List<String> fields = Arrays.asList("name", "productName", "productImage", "quantity", "createdAt");
        Map<String, String> fieldTitles = createPairs(fields, Arrays.asList("Tên khu vực", "Tên sản phẩm", "Hình ảnh", "Tồn kho", "Ngày tạo"));
        Map<String, String> fieldClasses = createPairs(fields, Arrays.asList("", "", "image", "", "dateTime"));

        model.addAttribute("fields", fields);
        model.addAttribute("fieldTitles", fieldTitles);
        model.addAttribute("fieldClasses", fieldClasses);

        Pageable pageable = PageRequest.of(zoneFilterDTO.getPage()-1, zoneFilterDTO.getSize(), sortDirection);

        Long ownerId = getUser().getId();

        Page<ZoneDTO> zones = zoneService.searchZone(ownerId, zoneFilterDTO, pageable);

        model.addAttribute("zones", zones);
        model.addAttribute("zoneFilterDTO", zoneFilterDTO);

        return "warehouse/zone/list";
    }

    @PostMapping({"/list", "", "/"})
    public String list(
            Model model,
            @ModelAttribute(value = "zoneFilterDTO", binding = false) ZoneFilterDTO zoneFilterDTO,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("zoneFilterDTO", zoneFilterDTO);
        return "redirect:/zone";
    }


}
