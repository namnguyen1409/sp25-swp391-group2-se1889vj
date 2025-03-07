package com.group2.sp25swp391group2se1889vj.service;

import com.group2.sp25swp391group2se1889vj.dto.ZoneDTO;

import java.util.List;

public interface ZoneService {

    List<ZoneDTO> searchZones(Long warehouseId, String keyword);
}
