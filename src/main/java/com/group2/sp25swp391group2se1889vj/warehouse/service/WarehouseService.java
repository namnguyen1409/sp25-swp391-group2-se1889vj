package com.group2.sp25swp391group2se1889vj.warehouse.service;

import com.group2.sp25swp391group2se1889vj.warehouse.dto.WarehouseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WarehouseService {
    Page<WarehouseDTO> findPaginatedWarehouses(int pageNumber, int pageSize);
    Page<WarehouseDTO> findPaginatedWarehousesByOwnerId(Long ownerId, Pageable pageable);
    Page<WarehouseDTO> findPaginatedWarehousesByOwnerIdAndNameContaining(Long ownerId, String name, int pageNumber, int pageSize);
    WarehouseDTO findWarehouseById(Long id);
    void saveWarehouse(WarehouseDTO WarehouseDTO);
    void deleteWarehouseById(Long id);
    boolean isExistWarehouseByOwnerIdAndName(Long ownerId, String name);
    Page<WarehouseDTO> findPaginatedWarehousesByOwnerIdAndNameContaining(Long ownerId, String name, Pageable pageable);
    Page<WarehouseDTO> findPaginatedWarehousesByOwnerIdAndLocationContaining(Long ownerId, String location, Pageable pageable);
}
