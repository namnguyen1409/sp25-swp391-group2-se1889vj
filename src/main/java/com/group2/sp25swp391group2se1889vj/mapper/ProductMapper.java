package com.group2.sp25swp391group2se1889vj.mapper;

import com.group2.sp25swp391group2se1889vj.dto.ProductDTO;
import com.group2.sp25swp391group2se1889vj.dto.ProductSnapshotDTO;
import com.group2.sp25swp391group2se1889vj.entity.Product;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final ModelMapper modelMapper;

    public ProductDTO mapToProductDTO(Product product) {
        return modelMapper.map(product, ProductDTO.class);
    }

    public ProductSnapshotDTO mapToProductSnapshotDTO(Product product) {
        return modelMapper.map(product, ProductSnapshotDTO.class);
    }

    public Product mapToProduct(ProductDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }

}
