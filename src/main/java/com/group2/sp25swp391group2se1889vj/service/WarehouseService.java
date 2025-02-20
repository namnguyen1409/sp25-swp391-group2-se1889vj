package com.group2.sp25swp391group2se1889vj.service;

import com.group2.sp25swp391group2se1889vj.dto.WarehouseDTO;
import com.group2.sp25swp391group2se1889vj.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WarehouseService {
    Page<WarehouseDTO> findPaginatedWarehouses(int pageNumber, int pageSize);
    Page<WarehouseDTO> findPaginatedWarehousesByOwnerId(Long ownerId, Pageable pageable);
    Page<WarehouseDTO> findPaginatedWarehousesByOwnerIdAndNameContaining(Long ownerId, String name, int pageNumber, int pageSize);
    WarehouseDTO findWarehouseById(Long id);
    void saveWarehouse(WarehouseDTO WarehouseDTO);
    void updateWarehouse(WarehouseDTO warehouseDTO);
    void deleteWarehouseById(Long id);
    boolean isExistWarehouseByOwnerIdAndName(Long ownerId, String name);
    Page<WarehouseDTO> findPaginatedWarehousesByOwnerIdAndNameContaining(Long ownerId, String name, Pageable pageable);
    Page<WarehouseDTO> findPaginatedWarehousesByOwnerIdAndLocationContaining(Long ownerId, String location, Pageable pageable);
    Warehouse searchWarehouseByOwnerIdAndName(Long ownerId, String name);
}
