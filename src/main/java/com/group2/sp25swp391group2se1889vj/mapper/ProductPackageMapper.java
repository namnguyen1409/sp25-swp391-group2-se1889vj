package com.group2.sp25swp391group2se1889vj.mapper;

import com.group2.sp25swp391group2se1889vj.dto.ProductPackageDTO;
import com.group2.sp25swp391group2se1889vj.entity.ProductPackage;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductPackageMapper {
    private final ModelMapper modelMapper;

    public ProductPackageDTO mapToProductPackageDTO(ProductPackage productPackage) {
        return modelMapper.map(productPackage, ProductPackageDTO.class);
    }

    public ProductPackage mapToProductPackage(ProductPackageDTO productPackageDTO) {
        return modelMapper.map(productPackageDTO, ProductPackage.class);
    }
}
