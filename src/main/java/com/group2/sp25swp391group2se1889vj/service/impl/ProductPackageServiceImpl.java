package com.group2.sp25swp391group2se1889vj.service.impl;

import com.group2.sp25swp391group2se1889vj.dto.ProductPackageDTO;
import com.group2.sp25swp391group2se1889vj.dto.ProductPackageFilterDTO;
import com.group2.sp25swp391group2se1889vj.entity.ProductPackage;
import com.group2.sp25swp391group2se1889vj.mapper.ProductPackageMapper;
import com.group2.sp25swp391group2se1889vj.repository.ProductPackageRepository;
import com.group2.sp25swp391group2se1889vj.repository.WarehouseRepository;
import com.group2.sp25swp391group2se1889vj.service.ProductPackageService;
import com.group2.sp25swp391group2se1889vj.specification.ProductPackageSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductPackageServiceImpl implements ProductPackageService {
    private final ProductPackageRepository productPackageRepository;
    private final ProductPackageMapper productPackageMapper;
    private final WarehouseRepository warehouseRepository;

    @Override
    public ProductPackageDTO findProductPackageById(Long id) {
        return productPackageRepository.findById(id)
                .map(productPackageMapper::mapToProductPackageDTO)
                .orElse(null);
    }

    @Override
    public void saveProductPackage(ProductPackageDTO productPackageDTO) {
        productPackageRepository.save(productPackageMapper.mapToProductPackage(productPackageDTO));
    }

    @Override
    public Boolean existsByNameAndCreatedById(String name, Long createdById) {
        return productPackageRepository.existsByNameAndCreatedById(name, createdById);
    }


    @Override
    public void addProductPackage(ProductPackageDTO productPackageDTO) {
        ProductPackage productPackage = productPackageMapper.mapToProductPackage(productPackageDTO);
        productPackage.setWarehouse(warehouseRepository.findById(productPackageDTO.getWarehouseId()).orElse(null));
        productPackageRepository.save(productPackage);
    }

    @Override
    public Page<ProductPackageDTO> searchProductPackages(Long warehouseId, ProductPackageFilterDTO productPackageFilterDTO, Pageable pageable) {
        Specification<ProductPackage> spec = ProductPackageSpecification.filterProductPackages(warehouseId, productPackageFilterDTO);
        return productPackageRepository.findAll(spec, pageable).map(productPackageMapper::mapToProductPackageDTO);
    }

    @Override
    public List<ProductPackageDTO> searchProductPackages(Long warehouseId, String name) {
        return productPackageRepository.findAllByNameContainingAndWarehouseId(name, warehouseId).stream().map(productPackageMapper::mapToProductPackageDTO).collect(Collectors.toList());
    }

    @Override
    public ProductPackage findByIdAndWarehouseId(Long productPackageId, Long warehouseId) {
        return productPackageRepository.findByIdAndWarehouseId(productPackageId, warehouseId);
    }

}
