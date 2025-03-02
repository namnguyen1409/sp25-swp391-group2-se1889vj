package com.group2.sp25swp391group2se1889vj.service.impl;

import com.group2.sp25swp391group2se1889vj.dto.WarehouseDTO;
import com.group2.sp25swp391group2se1889vj.entity.Warehouse;
import com.group2.sp25swp391group2se1889vj.exception.NoWarehouseElementException;
import com.group2.sp25swp391group2se1889vj.mapper.WarehouseMapper;
import com.group2.sp25swp391group2se1889vj.repository.WarehouseRepository;
import com.group2.sp25swp391group2se1889vj.service.WarehouseService;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
@AllArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private WarehouseRepository warehouseRepository;
    private WarehouseMapper warehouseMapper;
    private MessageSource messageSource;

    @Override
    public Page<WarehouseDTO> findPaginatedWarehouses(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return warehouseRepository.findAll(pageable).map(warehouseMapper::mapToWarehouseDTO);
    }

    @Override
    public Page<WarehouseDTO> findPaginatedWarehousesByOwnerId(Long ownerId, Pageable pageable) {
        return warehouseRepository.findByCreatedById(ownerId, pageable).map(warehouseMapper::mapToWarehouseDTO);
    }

    @Override
    public Page<WarehouseDTO> findPaginatedWarehousesByOwnerIdAndNameContaining(Long ownerId, String name, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return warehouseRepository.findByCreatedByIdAndNameContaining(ownerId, name, pageable).map(warehouseMapper::mapToWarehouseDTO);
    }

    @Override
    public WarehouseDTO findWarehouseById(Long id) {
        try{
            Optional<Warehouse> warehouse = warehouseRepository.findById(id);
            return warehouse.map(warehouse1 -> warehouseMapper.mapToWarehouseDTO(warehouse1)).orElse(null);
        }catch (NoWarehouseElementException e){
            String message = messageSource.getMessage("warehouse.notfound", new Object[]{id}, Locale.getDefault());
            throw new NoWarehouseElementException(message, id);
        }
    }

    @Override
    public void saveWarehouse(WarehouseDTO warehouseDTO) {
        Warehouse warehouse = warehouseMapper.mapToWarehouse(warehouseDTO);
        warehouseRepository.save(warehouse);
    }

    @Override
    public void updateWarehouse(WarehouseDTO warehouseDTO) {
        Warehouse warehouse = warehouseRepository.findById(warehouseDTO.getId()).orElse(null);
        if(warehouse != null){
            warehouse.setName(warehouseDTO.getName());
            warehouse.setLocation(warehouseDTO.getLocation());
            warehouse.setDescription(warehouseDTO.getDescription());
            warehouseRepository.save(warehouse);
        }
    }


    @Override
    public void deleteWarehouseById(Long id) {
        warehouseRepository.deleteById(id);
    }

    @Override
    public boolean isExistWarehouseByOwnerIdAndName(Long ownerId, String name) {
        return warehouseRepository.findByCreatedByIdAndName(ownerId, name) != null;
    }

    @Override
    public Page<WarehouseDTO> findPaginatedWarehousesByOwnerIdAndNameContaining(Long ownerId, String name, Pageable pageable) {
        return warehouseRepository.findByCreatedByIdAndNameContaining(ownerId, name, pageable).map(warehouseMapper::mapToWarehouseDTO);
    }

    @Override
    public Page<WarehouseDTO> findPaginatedWarehousesByOwnerIdAndLocationContaining(Long ownerId, String location, Pageable pageable) {
        return warehouseRepository.findByCreatedByIdAndLocationContaining(ownerId, location, pageable).map(warehouseMapper::mapToWarehouseDTO);
    }

    @Override
    public Warehouse searchWarehouseByOwnerIdAndName(Long ownerId, String name) {
        Warehouse warehouse = warehouseRepository.findByCreatedByIdAndName(ownerId, name);
        return warehouse;
    }


}
