package com.group2.sp25swp391group2se1889vj.service.impl;

import com.group2.sp25swp391group2se1889vj.dto.ZoneDTO;
import com.group2.sp25swp391group2se1889vj.entity.Zone;
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

@Service
@AllArgsConstructor
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
    public Page<ZoneDTO> findPaginatedZonesByInventoryId(Long inventoryId, Pageable pageable) {
        Page<Zone> page = zoneRepository.findByInventoryId(inventoryId, pageable);
        return page.map(zoneMapper::mapToZoneDTO);
    }

    @Override
    public Page<ZoneDTO> findPaginatedZonesByInventoryIdAndNameContaining(Long inventoryId, String name, Pageable pageable) {
        Page<Zone> page = zoneRepository.findByInventoryIdAndNameContaining(inventoryId, name, pageable);
        return page.map(zoneMapper::mapToZoneDTO);
    }

    @Override
    public Page<ZoneDTO> findPaginatedZonesByInventoryIdAndProductNameContaining(Long inventoryId, String productName, Pageable pageable) {
        Page<Zone> page = zoneRepository.findByInventoryIdAndProductNameContaining(inventoryId, productName, pageable);
        return page.map(zoneMapper::mapToZoneDTO);
    }

    @Override
    public List<ZoneDTO> findZonesByInventoryId(Long inventoryId) {
        List<Zone> zones = zoneRepository.findByInventoryId(inventoryId);
        return zones.stream().map(zoneMapper::mapToZoneDTO).toList();
    }

    @Override
    public ZoneDTO findZoneById(Long id) {
        try {
            Optional<Zone> zoneOptional = zoneRepository.findById(id);
            return zoneOptional.map(zone -> zoneMapper.mapToZoneDTO(zone)).orElse(null);
        } catch (Exception exception) {
            String message = messageSource.getMessage("entity.notfound", new Object[]{id}, Locale.getDefault());
            throw new RuntimeException(message);
        }
    }

    @Override
    public Boolean isExistZoneByInventoryIdAndName(Long inventoryId, String name) {
        return zoneRepository.existsByInventoryIdAndName(inventoryId, name);
    }

    @Override
    public void saveZone(ZoneDTO zoneDTO) {
        Zone zone = zoneMapper.mapToZone(zoneDTO);
        zoneRepository.save(zone);
    }

}
