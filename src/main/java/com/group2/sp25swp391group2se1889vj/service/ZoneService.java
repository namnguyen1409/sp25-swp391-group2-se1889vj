package com.group2.sp25swp391group2se1889vj.service;

import com.group2.sp25swp391group2se1889vj.dto.ZoneDTO;
import com.group2.sp25swp391group2se1889vj.dto.ZoneFilterDTO;
import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.entity.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ZoneService {
    void saveZone(ZoneDTO zoneDTO);

    void deleteZone(Long id);

    ZoneDTO findZoneById(Long id);

    Page<ZoneDTO> findAllZone(User user, Pageable pageable);

    Page<ZoneDTO> findPaginatedByCreatedByContainingAndNameContaining(User createdBy, String name, Pageable pageable);
    Page<ZoneDTO> findPaginatedByCreatedByContainingAndProductNameContaining(User createdBy, String productName,Pageable pageable);
    Page<ZoneDTO> findPaginatedByCreatedByContainingAndNameContainingAndProductNameContaining(User createdBy, String name, String productName, Pageable pageable);
    Page<ZoneDTO> findPaginatedByCreatedBy(User createdBy, Pageable pageable);

    Page<ZoneDTO> searchZone(Long createdBy, ZoneFilterDTO zoneFilterDTO, Pageable pageable);

}

