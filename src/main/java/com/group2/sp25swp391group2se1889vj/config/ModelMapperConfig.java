package com.group2.sp25swp391group2se1889vj.config;


import com.group2.sp25swp391group2se1889vj.dto.*;
import com.group2.sp25swp391group2se1889vj.entity.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
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
                using(ctx -> {
                    Set<Zone> zones = (Set<Zone>) ctx.getSource();
                    return zones != null ? zones.stream()
                            .filter(Objects::nonNull)
                            .map(zone -> modelMapper.map(zone, ZoneDTO.class)) // Tạo DTO từ Entity
                            .collect(Collectors.toSet())
                            : new HashSet<>();
                }).map(source.getZones(), destination.getZones());
                using(ctx -> {
                    Set<Zone> zones = (Set<Zone>) ctx.getSource();
                    return zones != null ? zones.stream()
                            .filter(Objects::nonNull)
                            .map(Zone::getId) // Chỉ lấy ID
                            .collect(Collectors.toSet())
                            : new HashSet<>();
                }).map(source.getZones(), destination.getZoneIds());
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

        modelMapper.addMappings(
                new PropertyMap<InvoiceItem, InvoiceItemDTO>() {
                    @Override
                    protected void configure() {
                        map().setCreatedBy(source.getCreatedBy().getId());
                        map().setCreatedAt(source.getCreatedAt());
                        map().setUpdatedBy(source.getUpdatedBy().getId());
                        map().setUpdatedAt(source.getUpdatedAt());
                        map().setProductId(source.getProduct().getId());
                        using(ctx -> ProductPackageDTO.builder()
                                .name(((InvoiceItem) ctx.getSource()).getProductPackage().getName())
                                .weight(((InvoiceItem) ctx.getSource()).getProductPackage().getWeight())
                                .build()
                        ).map(source, destination.getProductPackage());

                    }
                }
        );

        modelMapper.addMappings(
                new PropertyMap<Invoice, InvoiceDataDTO>() {
                    @Override
                    protected void configure() {
                        map().setCreatedBy(source.getCreatedBy().getId());
                        map().setCreatedAt(source.getCreatedAt());
                        map().setUpdatedBy(source.getUpdatedBy().getId());
                        map().setUpdatedAt(source.getUpdatedAt());
                        map().setCreatedByUsername(source.getCreatedBy().getUsername());
                        map().setCustomerBalance(source.getCustomerBalance());
                    }
                }
        );

        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        modelMapper.getConfiguration().setPropertyCondition(context -> context.getSource() != null);
        return modelMapper;
    }

}
