package com.group2.sp25swp391group2se1889vj.service.impl;

import com.group2.sp25swp391group2se1889vj.dto.WarehouseDTO;
import com.group2.sp25swp391group2se1889vj.entity.Warehouse;
import com.group2.sp25swp391group2se1889vj.mapper.WarehouseMapper;
import com.group2.sp25swp391group2se1889vj.repository.WarehouseRepository;
import com.group2.sp25swp391group2se1889vj.service.MessageService;
import com.group2.sp25swp391group2se1889vj.service.WarehouseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
@AllArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private WarehouseRepository warehouseRepository;
    private WarehouseMapper warehouseMapper;
    private MessageService messageService;

    @Override
    public WarehouseDTO findWarehouseByOwnerId(Long ownerId) {
        return warehouseMapper.mapToWarehouseDTO(warehouseRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new RuntimeException(messageService.getMessage("warehouse.not.found"))));
    }

    @Override
    public void updateWarehouse(WarehouseDTO warehouseDTO) {
        Warehouse warehouse = warehouseRepository.findByOwnerId(warehouseDTO.getOwnerId())
                .orElseThrow(() -> new RuntimeException(messageService.getMessage("warehouse.not.found")));
        warehouse.setName(warehouseDTO.getName());
        warehouse.setLocation(warehouseDTO.getLocation());
        warehouse.setDescription(warehouseDTO.getDescription());
        warehouseRepository.save(warehouse);
    }

    @Override
    public WarehouseDTO findById(Long warehouseId) {
        return warehouseMapper.mapToWarehouseDTO(warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException(messageService.getMessage("warehouse.not.found"))));
    }
}
