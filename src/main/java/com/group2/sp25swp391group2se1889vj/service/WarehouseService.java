package com.group2.sp25swp391group2se1889vj.service;

import com.group2.sp25swp391group2se1889vj.dto.WarehouseDTO;
import com.group2.sp25swp391group2se1889vj.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WarehouseService {
    WarehouseDTO findWarehouseByOwnerId(Long ownerId);
    void updateWarehouse(WarehouseDTO warehouseDTO);
}
