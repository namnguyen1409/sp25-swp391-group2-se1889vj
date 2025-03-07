package com.group2.sp25swp391group2se1889vj.service.impl;

import com.group2.sp25swp391group2se1889vj.dto.ZoneDTO;
import com.group2.sp25swp391group2se1889vj.mapper.ZoneMapper;
import com.group2.sp25swp391group2se1889vj.repository.ZoneRepository;
import com.group2.sp25swp391group2se1889vj.service.MessageService;
import com.group2.sp25swp391group2se1889vj.service.ZoneService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ZoneServiceImpl implements ZoneService {

    private ZoneRepository zoneRepository;
    private ZoneMapper zoneMapper;
    private MessageService messageService;


    @Override
    public List<ZoneDTO> searchZones(Long warehouseId, String keyword) {
        return zoneRepository.findAllByNameContainingAndWarehouseId(keyword, warehouseId).stream().map(zoneMapper::mapToZoneDTO).collect(Collectors.toList());
    }
}
