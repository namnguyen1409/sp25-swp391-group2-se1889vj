package com.group2.sp25swp391group2se1889vj.config;


import com.group2.sp25swp391group2se1889vj.dto.*;
import com.group2.sp25swp391group2se1889vj.entity.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.stream.Collectors;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.addMappings(new PropertyMap<User, UserDTO>() {
            @Override
            protected void configure() {
                map().setCreatedBy(source.getCreatedBy().getId());
                map().setCreatedAt(source.getCreatedAt());
                map().setUpdatedBy(source.getUpdatedBy().getId());
                map().setUpdatedAt(source.getUpdatedAt());

            }
        });


        modelMapper.addMappings(new PropertyMap<Product, ProductDTO>() {
            @Override
            protected void configure() {
                map().setCreatedBy(source.getCreatedBy().getId());
                map().setCreatedAt(source.getCreatedAt());
                map().setUpdatedBy(source.getUpdatedBy().getId());
                map().setUpdatedAt(source.getUpdatedAt());
                map().setWarehouseId(source.getWarehouse().getId());
                map().setProductPackageId(source.getProductPackage().getId());
            }
        });

        modelMapper.addMappings(new PropertyMap<Warehouse, WarehouseDTO>() {
            @Override
            protected void configure() {
                map().setCreatedBy(source.getCreatedBy().getId());
                map().setCreatedAt(source.getCreatedAt());
                map().setUpdatedBy(source.getUpdatedBy().getId());
                map().setUpdatedAt(source.getUpdatedAt());
            }
        });

        modelMapper.addMappings(new PropertyMap<Zone, ZoneDTO>() {
            @Override
            protected void configure() {
                map().setCreatedBy(source.getCreatedBy().getId());
                map().setCreatedAt(source.getCreatedAt());
                map().setUpdatedBy(source.getUpdatedBy().getId());
                map().setUpdatedAt(source.getUpdatedAt());
            }
        });


        modelMapper.addMappings(new PropertyMap<Customer, CustomerDTO>() {
            @Override
            protected void configure() {
                map().setCreatedBy(source.getCreatedBy().getId());
                map().setCreatedAt(source.getCreatedAt());
                map().setUpdatedBy(source.getUpdatedBy().getId());
                map().setUpdatedAt(source.getUpdatedAt());
                map().setWarehouseId(source.getWarehouse().getId());
            }
        });

        modelMapper.addMappings(new PropertyMap<Debt, DebtDTO>() {
            @Override
            protected void configure() {
                map().setCreatedBy(source.getCreatedBy().getId());
                map().setCreatedAt(source.getCreatedAt());
                map().setUpdatedBy(source.getUpdatedBy().getId());
                map().setUpdatedAt(source.getUpdatedAt());
                map().setCustomerId(source.getCustomer().getId());
            }
        });

        modelMapper.addMappings(new PropertyMap<ProductPackage, ProductPackageDTO>() {
            @Override
            protected void configure() {
                map().setCreatedBy(source.getCreatedBy().getId());
                map().setCreatedAt(source.getCreatedAt());
                map().setUpdatedBy(source.getUpdatedBy().getId());
                map().setUpdatedAt(source.getUpdatedAt());
                map().setWarehouseId(source.getWarehouse().getId());
            }
        });

        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return modelMapper;
    }

}
