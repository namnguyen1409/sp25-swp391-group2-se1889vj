package com.group2.sp25swp391group2se1889vj.service.impl;

import com.group2.sp25swp391group2se1889vj.dto.ZoneDTO;
import com.group2.sp25swp391group2se1889vj.dto.ZoneFilterDTO;
import com.group2.sp25swp391group2se1889vj.entity.Product;
import com.group2.sp25swp391group2se1889vj.entity.Zone;
import com.group2.sp25swp391group2se1889vj.exception.Http400;
import com.group2.sp25swp391group2se1889vj.exception.NoZoneElementException;
import com.group2.sp25swp391group2se1889vj.exception.ZoneNoSuchElementException;
import com.group2.sp25swp391group2se1889vj.mapper.ZoneMapper;
import com.group2.sp25swp391group2se1889vj.repository.ProductRepository;
import com.group2.sp25swp391group2se1889vj.repository.ZoneRepository;
import com.group2.sp25swp391group2se1889vj.service.MessageService;
import com.group2.sp25swp391group2se1889vj.service.ZoneService;

import com.group2.sp25swp391group2se1889vj.specification.ZoneSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ZoneServiceImpl implements ZoneService {

    private final ProductRepository productRepository;
    private ZoneRepository zoneRepository;
    private ZoneMapper zoneMapper;
    private MessageService messageService;


    @Override
    public ZoneDTO findZoneById(Long id) {
        Optional<Zone> zoneOptional = zoneRepository.findById(id);
        if (zoneOptional.isPresent()) {
            return zoneMapper.mapToZoneDTO(zoneOptional.get());
        } else {
            throw new ZoneNoSuchElementException(messageService.getMessage("entity.notfound", id), id);
        }
    }

    @Override
    public void saveZone(ZoneDTO zoneDTO) {
        Zone zone = zoneMapper.mapToZone(zoneDTO);
        zoneRepository.save(zone);
    }

    @Override
    public Page<ZoneDTO> searchZones(Long warehouseId, ZoneFilterDTO zoneFilterDTO, Pageable pageable) {
        Specification<Zone> specification = ZoneSpecification.filterZones(warehouseId, zoneFilterDTO);
        return zoneRepository.findAll(specification, pageable).map(zoneMapper::mapToZoneDTO);
    }

    @Override
    public List<ZoneDTO> searchZones(Long warehouseId, String keyword) {
        return zoneRepository.findAllByNameContainingAndWarehouseId(keyword, warehouseId).stream().map(zoneMapper::mapToZoneDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateZone(ZoneDTO zoneDTO) {
        Zone zone = zoneRepository.findByIdAndWarehouseId(zoneDTO.getId(), zoneDTO.getWarehouseId());
        if (zone == null) {
            throw new Http400("Zone not found");
        }
        zone.setName(zoneDTO.getName());
        Product product = productRepository.getProductByIdAndWarehouseId(zoneDTO.getProductId(), zoneDTO.getWarehouseId());
        zone.setProduct(product);
        zone.setDescription(zoneDTO.getDescription());
    }

    @Override
    public List<ZoneDTO> findAll() {
        return zoneRepository.findAll().stream().map(zoneMapper::mapToZoneDTO).collect(Collectors.toList());
    }
}
