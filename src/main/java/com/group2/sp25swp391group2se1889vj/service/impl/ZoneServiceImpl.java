package com.group2.sp25swp391group2se1889vj.service.impl;

import com.group2.sp25swp391group2se1889vj.dto.ZoneDTO;
import com.group2.sp25swp391group2se1889vj.entity.Zone;
import com.group2.sp25swp391group2se1889vj.exception.ZoneNoSuchElementException;
import com.group2.sp25swp391group2se1889vj.mapper.ZoneMapper;
import com.group2.sp25swp391group2se1889vj.repository.ZoneRepository;
import com.group2.sp25swp391group2se1889vj.service.ZoneService;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalInt;

@AllArgsConstructor
@Service
public class ZoneServiceImpl implements ZoneService {

    private ZoneRepository zoneRepository;
    private ZoneMapper zoneMapper;
    private MessageSource messageSource;

    @Override
    public Page<ZoneDTO> findPaginatedZones(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Zone> page = zoneRepository.findAll(pageable);
        return page.map(zoneMapper::mapToZoneDTO);
    }

    @Override
    public Page<ZoneDTO> findPaginatedZonesByWarehouseId(Long warehouseId, Pageable pageable) {
        Page<Zone> page = zoneRepository.findByWarehouseId(warehouseId, pageable);
        return page.map(zoneMapper::mapToZoneDTO);
    }

    @Override
    public Page<ZoneDTO> findPaginatedZonesByWarehouseIdAndNameContaining(Long warehouseId, String name, Pageable pageable) {
        Page<Zone> page = zoneRepository.findByWarehouseIdAndNameContaining(warehouseId, name, pageable);
        return page.map(zoneMapper::mapToZoneDTO);
    }

    @Override
    public Page<ZoneDTO> findPaginatedZonesByWarehouseIdAndProductNameContaining(Long WarehouseId, String productName, Pageable pageable) {
        Page<Zone> page = zoneRepository.findByWarehouseIdAndProductNameContaining(WarehouseId, productName, pageable);
        return page.map(zoneMapper::mapToZoneDTO);
    }

    @Override
    public List<ZoneDTO> findZonesByWarehouseId(Long warehouseId) {
        List<Zone> zones = zoneRepository.findByWarehouseId(warehouseId);
        return zones.stream().map(zoneMapper::mapToZoneDTO).toList();
    }

    @Override
    public ZoneDTO findZoneById(Long id) {
        try {
            Optional<Zone> zone = zoneRepository.findById(id);
            return zone.map(zoneMapper::mapToZoneDTO).orElse(null);
        } catch (Exception e) {
            String message = messageSource.getMessage("error.find.zone.by.id", null, Locale.getDefault());
            throw new ZoneNoSuchElementException(message, id);
        }
    }

    @Override
    public Boolean isExistZoneByWarehouseIdAndName(Long warehouseId, String name) {
        return zoneRepository.existsByWarehouseIdAndName(warehouseId, name);
    }

    @Override
    public void saveZone(ZoneDTO zoneDTO) {
        Zone zone = zoneMapper.mapToZone(zoneDTO);
        zoneRepository.save(zone);
    }
}
