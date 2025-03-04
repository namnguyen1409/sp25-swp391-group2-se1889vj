package com.group2.sp25swp391group2se1889vj.mapper;


import com.group2.sp25swp391group2se1889vj.dto.ZoneDTO;
import com.group2.sp25swp391group2se1889vj.entity.Zone;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ZoneMapper {

    private final ModelMapper modelMapper;


    public ZoneDTO mapToZoneDTO(Zone zone) {
        return modelMapper.map(zone, ZoneDTO.class);
    }

    public Zone mapToZone(ZoneDTO zoneDTO) {
        return modelMapper.map(zoneDTO, Zone.class);
    }


}
