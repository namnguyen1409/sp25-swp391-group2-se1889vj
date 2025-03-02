package com.group2.sp25swp391group2se1889vj.mapper;


import com.group2.sp25swp391group2se1889vj.dto.ZoneDTO;
import com.group2.sp25swp391group2se1889vj.entity.Zone;
import com.group2.sp25swp391group2se1889vj.repository.WarehouseRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ZoneMapper {
    private WarehouseRepository warehouseRepository;
    private ModelMapper modelMapper;

    public ZoneDTO mapToZoneDTO(Zone zone) {
        return modelMapper.map(zone, ZoneDTO.class);
    }

    public Zone mapToZone(ZoneDTO zoneDTO) {
        Zone zone = modelMapper.map(zoneDTO, Zone.class);
        if (zoneDTO.getWarehouseId() != null) {
            zone.setWarehouse(warehouseRepository.findById(zoneDTO.getWarehouseId()).orElse(null));
        }
        return zone;
    }
}
