package com.group2.sp25swp391group2se1889vj.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.sp25swp391group2se1889vj.dto.ProductDTO;
import com.group2.sp25swp391group2se1889vj.exception.Http500;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invoice_items")
public class InvoiceItem extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "discount", nullable = false)
    private BigDecimal discount;

    @Column(name="payable", nullable = false)
    private BigDecimal payable;

    // product snapshot
    @Column(name = "product_snapshot", nullable = false, columnDefinition = "nvarchar(max)")
    private String productSnapshot;

    public void setProductSnapshot(ProductDTO productDTO) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            this.productSnapshot = objectMapper.writeValueAsString(productDTO);
        } catch (JsonProcessingException e) {
            throw new Http500("Error when serializing product snapshot");
        }
    }

    public ProductDTO getProductSnapshot() {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(this.productSnapshot, ProductDTO.class);
        } catch (JsonProcessingException e) {
            throw new Http500("Error when deserializing product snapshot");
        }
    }

}
