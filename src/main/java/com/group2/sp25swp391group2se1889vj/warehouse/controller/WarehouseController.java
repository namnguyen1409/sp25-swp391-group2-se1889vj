package com.group2.sp25swp391group2se1889vj.warehouse.controller;

import com.group2.sp25swp391group2se1889vj.warehouse.dto.WarehouseDTO;
import com.group2.sp25swp391group2se1889vj.warehouse.entity.Warehouse;
import com.group2.sp25swp391group2se1889vj.warehouse.service.WarehouseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/warehouse")
public class WarehouseController {
    private WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping
    public String getList() {
        return "warehouse/list";
    }

    @GetMapping("/add")
    public String addWarehouse(
            Model model
    ) {
        model.addAttribute("warehouse", new WarehouseDTO());
        return "warehouse/add";
    }


    @PostMapping("/add")
    public String postCreate(@ModelAttribute WarehouseDTO warehouseDTO) {
        warehouseService.addWarehouse(warehouseDTO);
        return "warehouse/add";
    }

    @GetMapping("/edit")
    public String getEdit() {
        return "warehouse/edit";
    }

    @GetMapping("/delete")
    public String getDelete() {
        return "warehouse/delete";
    }

    @GetMapping("/detail")
    public String getDetail() {
        return "warehouse/detail";
    }

}
