package com.group2.sp25swp391group2se1889vj.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.sp25swp391group2se1889vj.dto.ProductDTO;
import com.group2.sp25swp391group2se1889vj.dto.ProductSnapshotDTO;
import com.group2.sp25swp391group2se1889vj.exception.Http500;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;


@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invoice_items")
public class InvoiceItem extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_package_id", nullable = false)
    private ProductPackage productPackage;

    @EqualsAndHashCode.Include
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @EqualsAndHashCode.Include
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @EqualsAndHashCode.Include
    @Column(name = "discount", nullable = false)
    private BigDecimal discount;

    @EqualsAndHashCode.Include
    @Column(name="payable", nullable = false)
    private BigDecimal payable;

    // product snapshot
    @Column(name = "product_snapshot", nullable = false, columnDefinition = "nvarchar(max)")
    private String productSnapshot;

    public ProductSnapshotDTO getProductSnapshotDTO() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return objectMapper.readValue(productSnapshot, ProductSnapshotDTO.class);
        } catch (JsonProcessingException e) {
            throw new Http500("Can't parse product snapshot");
        }
    }

    public void setProductSnapshotDTO(ProductSnapshotDTO productSnapshotDTO) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            productSnapshot = objectMapper.writeValueAsString(productSnapshotDTO);
        } catch (JsonProcessingException e) {
            throw new Http500("Can't serialize product snapshot");
        }
    }



}
