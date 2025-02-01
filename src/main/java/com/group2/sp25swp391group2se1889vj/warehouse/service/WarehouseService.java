package com.group2.sp25swp391group2se1889vj.warehouse.service;

import com.group2.sp25swp391group2se1889vj.warehouse.dto.WarehouseDTO;
import com.group2.sp25swp391group2se1889vj.warehouse.entity.Warehouse;
import com.group2.sp25swp391group2se1889vj.warehouse.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public List<Warehouse> getWarehouseList() {
        return warehouseRepository.findAll();
    }

    public void addWarehouse(WarehouseDTO warehouse) {
        Warehouse newWarehouse = new Warehouse();
        newWarehouse.setName(warehouse.getName());
        newWarehouse.setLocation(warehouse.getLocation());
        newWarehouse.setDescription(warehouse.getDescription());
        warehouseRepository.save(newWarehouse);
    }

    public void updateWarehouse(Warehouse warehouse) {
        Warehouse newWarehouse = warehouseRepository.findById(warehouse.getId()).orElseThrow();
        newWarehouse.setName(warehouse.getName());
        newWarehouse.setLocation(warehouse.getLocation());
        newWarehouse.setDescription(warehouse.getDescription());
        warehouseRepository.save(newWarehouse);
    }

    public void delete(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse ID " + id + " không tồn tại."));

        warehouse.setDeleted(true);
        warehouseRepository.save(warehouse);
    }
}
