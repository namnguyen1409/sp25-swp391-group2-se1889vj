package com.group2.sp25swp391group2se1889vj.mapper;


import com.group2.sp25swp391group2se1889vj.dto.WarehouseDTO;
import com.group2.sp25swp391group2se1889vj.entity.Warehouse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WarehouseMapper {
    private final ModelMapper modelMapper;

    public WarehouseDTO mapToWarehouseDTO(Warehouse warehouse) {
        return modelMapper.map(warehouse, WarehouseDTO.class);
    }

    public Warehouse mapToWarehouse(WarehouseDTO warehouseDTO) {
        return modelMapper.map(warehouseDTO, Warehouse.class);
    }
}
