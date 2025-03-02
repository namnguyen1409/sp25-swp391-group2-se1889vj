package com.group2.sp25swp391group2se1889vj.service;

import com.group2.sp25swp391group2se1889vj.dto.ZoneDTO;
import com.group2.sp25swp391group2se1889vj.entity.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ZoneService {
    Page<ZoneDTO> findPaginatedZones(int pageNumber, int pageSize);
    Page<ZoneDTO> findPaginatedZonesByWarehouseId(Long WarehouseId, org.springframework.data.domain.Pageable pageable);
    Page<ZoneDTO> findPaginatedZonesByWarehouseIdAndNameContaining(Long WarehouseId, String name, org.springframework.data.domain.Pageable pageable);
    Page<ZoneDTO> findPaginatedZonesByWarehouseIdAndProductNameContaining(Long WarehouseId, String productName, Pageable pageable);

    List<ZoneDTO> findZonesByWarehouseId(Long WarehouseId);


    ZoneDTO findZoneById(Long id);
    Boolean isExistZoneByWarehouseIdAndName(Long WarehouseId, String name);
    void saveZone(ZoneDTO zoneDTO);
}
