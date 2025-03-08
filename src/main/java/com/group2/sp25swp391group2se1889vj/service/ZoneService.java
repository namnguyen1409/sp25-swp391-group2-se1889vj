package com.group2.sp25swp391group2se1889vj.service;

import com.group2.sp25swp391group2se1889vj.dto.ZoneDTO;
import com.group2.sp25swp391group2se1889vj.dto.ZoneFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ZoneService {
    ZoneDTO findZoneById(Long id);
    void saveZone(ZoneDTO zoneDTO);
    Page<ZoneDTO> searchZones(Long warehouseId, ZoneFilterDTO zoneFilterDTO, Pageable pageable);

    List<ZoneDTO> searchZones(Long warehouseId, String keyword);
}
