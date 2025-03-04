package com.group2.sp25swp391group2se1889vj.controller;



import com.group2.sp25swp391group2se1889vj.security.CustomUserDetails;
import com.group2.sp25swp391group2se1889vj.util.XSSProtectedUtil;
import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.dto.WarehouseDTO;
import com.group2.sp25swp391group2se1889vj.service.WarehouseService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;




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

    @GetMapping({"/", ""})
    public String detail(
            Model model
    ) {
        WarehouseDTO warehouseDTO = warehouseService.findWarehouseByOwnerId(getUser().getId());
        model.addAttribute("warehouse", warehouseDTO);
        return "warehouse/detail";
    }

    @GetMapping("/edit")
    public String edit(
            Model model
    ) {
        WarehouseDTO warehouseDTO = warehouseService.findWarehouseByOwnerId(getUser().getId());
        model.addAttribute("warehouse", warehouseDTO);
        return "warehouse/edit";
    }

    @PostMapping("/edit")
    public String edit(
            @ModelAttribute WarehouseDTO warehouseDTO
    ) {
        warehouseDTO.setOwnerId(getUser().getId());
        warehouseService.updateWarehouse(warehouseDTO);
        return "redirect:/warehouse";
    }

}

