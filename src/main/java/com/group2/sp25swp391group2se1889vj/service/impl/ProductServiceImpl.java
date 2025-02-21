package com.group2.sp25swp391group2se1889vj.service.impl;


import com.group2.sp25swp391group2se1889vj.dto.ProductDTO;
import com.group2.sp25swp391group2se1889vj.entity.Product;
import com.group2.sp25swp391group2se1889vj.exception.ProductNoSuchElementException;
import com.group2.sp25swp391group2se1889vj.mapper.ProductMapper;
import com.group2.sp25swp391group2se1889vj.repository.ProductRepository;
import com.group2.sp25swp391group2se1889vj.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;
    private ProductMapper productMapper;
    private MessageSource messageSource;

    @Override
    public Page<ProductDTO> findPaginatedProducts(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Product> page = productRepository.findAll(pageable);
        return page.map(productMapper::mapToProductDTO);
    }

    @Override
    public Page<ProductDTO> findPaginatedProductsByOwnerId(Long ownerId, Pageable pageable) {
        Page<Product> page = productRepository.findByCreatedById(ownerId, pageable);
        return page.map(productMapper::mapToProductDTO);
    }

    @Override
    public Page<ProductDTO> findPaginatedProductsByOwnerIdAndNameContaining(Long ownerId, String name, Pageable pageable) {
        Page<Product> page = productRepository.findByCreatedByIdAndNameContainingIgnoreCase(ownerId, name, pageable);
        return page.map(productMapper::mapToProductDTO);
    }

    @Override
    public Page<ProductDTO> findPaginatedProductsByOwnerIdAndDescriptionContaining(Long ownerId, String description, Pageable pageable) {
        Page<Product> page = productRepository.findByCreatedByIdAndDescriptionPlainTextContainingIgnoreCase(ownerId, description, pageable);
        return page.map(productMapper::mapToProductDTO);
    }


    @Override
    public Page<ProductDTO> findPaginatedProductsByNameContaining(String name, Pageable pageable) {
        return null;
    }

    @Override
    public ProductDTO findProductById(Long id) {
        try {
            Optional<Product> productOptional = productRepository.findById(id);
            return productOptional.map(product -> productMapper.mapToProductDTO(product)).orElse(null);
        } catch (NoSuchElementException exception) {
            String message = messageSource.getMessage("entity.notfound", new Object[]{id}, Locale.getDefault());
            throw new ProductNoSuchElementException(message, id);
        }
    }

    @Override
    public void saveProduct(ProductDTO productDTO) {
        Product product = productMapper.mapToProduct(productDTO);
        productRepository.save(product);
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Boolean existsByNameAndCreatedById(String name, Long ownerId) {
        return productRepository.existsByNameAndCreatedById(name, ownerId);
    }
}
