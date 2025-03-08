package com.group2.sp25swp391group2se1889vj.mapper;


import com.group2.sp25swp391group2se1889vj.dto.ZoneDTO;
import com.group2.sp25swp391group2se1889vj.entity.Zone;
import com.group2.sp25swp391group2se1889vj.repository.ProductRepository;
import com.group2.sp25swp391group2se1889vj.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ZoneMapper {

    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;


    public ZoneDTO mapToZoneDTO(Zone zone) {
        return modelMapper.map(zone, ZoneDTO.class);
    }

    public Zone mapToZone(ZoneDTO zoneDTO) {
        Zone zone =  modelMapper.map(zoneDTO, Zone.class);
        if (zoneDTO.getProductId() != null) {
            zone.setProduct(productRepository.getProductByIdAndWarehouseId(zoneDTO.getProductId(), zoneDTO.getWarehouseId()));
        }
        if (zoneDTO.getWarehouseId() != null) {
            zone.setWarehouse(warehouseRepository.findById(zoneDTO.getWarehouseId()).orElse(null));
        }
        return zone;
    }


}
