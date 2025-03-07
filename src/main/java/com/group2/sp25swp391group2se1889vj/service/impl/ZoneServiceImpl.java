package com.group2.sp25swp391group2se1889vj.service.impl;

import com.group2.sp25swp391group2se1889vj.dto.ZoneDTO;
import com.group2.sp25swp391group2se1889vj.dto.ZoneFilterDTO;
import com.group2.sp25swp391group2se1889vj.entity.User;
import com.group2.sp25swp391group2se1889vj.entity.Zone;
import com.group2.sp25swp391group2se1889vj.mapper.ZoneMapper;
import com.group2.sp25swp391group2se1889vj.repository.ZoneRepository;
import com.group2.sp25swp391group2se1889vj.service.MessageService;
import com.group2.sp25swp391group2se1889vj.service.ZoneService;

import com.group2.sp25swp391group2se1889vj.specification.ZoneSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class ZoneServiceImpl implements ZoneService {

    private ZoneRepository zoneRepository;
    private ZoneMapper zoneMapper;
    private MessageService messageService;


    @Override
    public void saveZone(ZoneDTO zoneDTO) {
        Zone zone = zoneMapper.mapToZone(zoneDTO);
        zoneRepository.save(zone);
    }

    @Override
    public void deleteZone(Long id) {

    }

    @Override
    public ZoneDTO findZoneById(Long id) {
        return null;
    }

    @Override
    public Page<ZoneDTO> findAllZone(User user, Pageable pageable) {
        Page<Zone> page = zoneRepository.findByCreatedBy(user, pageable);
        return page.map(zoneMapper::mapToZoneDTO);
    }

    @Override
    public Page<ZoneDTO> findPaginatedByCreatedByContainingAndNameContaining(User createdBy, String name, Pageable pageable) {
        Page<Zone> page = zoneRepository.findByCreatedByContainingAndNameContaining(createdBy, name, pageable);
        return page.map(zoneMapper::mapToZoneDTO);
    }

    @Override
    public Page<ZoneDTO> findPaginatedByCreatedByContainingAndProductNameContaining(User createdBy, String productName, Pageable pageable) {
        Page<Zone> page = zoneRepository.findByCreatedByContainingAndProductNameContaining(createdBy, productName, pageable);
        return page.map(zoneMapper::mapToZoneDTO);
    }

    @Override
    public Page<ZoneDTO> findPaginatedByCreatedByContainingAndNameContainingAndProductNameContaining(User createdBy, String name, String productName, Pageable pageable) {
        Page<Zone> page = zoneRepository.findByCreatedByContainingAndNameContainingAndProductNameContaining(createdBy, name, productName, pageable);
        return page.map(zoneMapper::mapToZoneDTO);
    }

    @Override
    public Page<ZoneDTO> findPaginatedByCreatedBy(User createdBy, Pageable pageable) {
        Page<Zone> page = zoneRepository.findByCreatedBy(createdBy, pageable);
        return page.map(zoneMapper::mapToZoneDTO);
    }

    @Override
    public Page<ZoneDTO> searchZone(Long createdBy, ZoneFilterDTO zoneFilterDTO, Pageable pageable) {
        Specification<Zone> specification = ZoneSpecification.filterZones(createdBy, zoneFilterDTO);
        Page<Zone> page = zoneRepository.findAll(specification, pageable);
        return page.map(zoneMapper::mapToZoneDTO);
    }

}
