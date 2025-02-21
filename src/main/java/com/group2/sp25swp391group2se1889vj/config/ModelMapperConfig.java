package com.group2.sp25swp391group2se1889vj.config;


import com.group2.sp25swp391group2se1889vj.dto.*;
import com.group2.sp25swp391group2se1889vj.entity.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                map().setWarehouseId(source.getWarehouse().getId());
                if(source.getProduct() != null) {
                    map().setProductId(source.getProduct().getId());
                    map().setProductName(source.getProduct().getName());
                    map().setProductImage(source.getProduct().getImage());
                } else {
                    map().setProductId(null);
                    map().setProductName(null);
                    map().setProductImage(null);
                }
            }
        });


        modelMapper.addMappings(new PropertyMap<Customer, CustomerDTO>() {
            @Override
            protected void configure() {
                map().setCreatedBy(source.getCreatedBy().getId());
                map().setCreatedAt(source.getCreatedAt());
                map().setUpdatedBy(source.getUpdatedBy().getId());
                map().setUpdatedAt(source.getUpdatedAt());
                map().setOwnerId(source.getOwner().getId());
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


        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return modelMapper;
    }

}
