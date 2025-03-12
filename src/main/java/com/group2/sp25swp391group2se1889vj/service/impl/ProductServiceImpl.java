package com.group2.sp25swp391group2se1889vj.service.impl;


import com.group2.sp25swp391group2se1889vj.dto.ProductDTO;
import com.group2.sp25swp391group2se1889vj.entity.Product;
import com.group2.sp25swp391group2se1889vj.entity.ProductPackage;
import com.group2.sp25swp391group2se1889vj.entity.Zone;
import com.group2.sp25swp391group2se1889vj.exception.ProductNoSuchElementException;
import com.group2.sp25swp391group2se1889vj.mapper.ProductMapper;
import com.group2.sp25swp391group2se1889vj.repository.ProductPackageRepository;
import com.group2.sp25swp391group2se1889vj.repository.ProductRepository;
import com.group2.sp25swp391group2se1889vj.repository.ZoneRepository;
import com.group2.sp25swp391group2se1889vj.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;
    private ProductMapper productMapper;
    private MessageSource messageSource;
    private ProductPackageRepository productPackageRepository;
    private ZoneRepository zoneRepository;

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
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Boolean existsByNameAndCreatedById(String name, Long ownerId) {
        return productRepository.existsByNameAndCreatedById(name, ownerId);
    }

    @Override
    public void saveProduct(ProductDTO productDTO, ProductPackage productPackage) {
        Product product = productMapper.mapToProduct(productDTO);
        product.setProductPackage(productPackage);
        productRepository.save(product);

    }

    @Override
    public List<ProductDTO> searchProducts(Long warehouseId, String keyword) {
        return productRepository.findAllByNameContainingAndWarehouseId(keyword, warehouseId).stream().map(productMapper::mapToProductDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addProduct(ProductDTO productDTO) throws Exception {
        if (productRepository.existsByNameAndWarehouseId(productDTO.getName(), productDTO.getWarehouseId())) {
            throw new Exception("Sản phẩm đã tồn tại");
        }
        Product product = productMapper.mapToProduct(productDTO);
        ProductPackage productPackage = productPackageRepository.findByIdAndWarehouseId(productDTO.getProductPackageId(), productDTO.getWarehouseId());
        if (productPackage == null) throw new Exception("Không tìm thấy quy cách đóng gói");
        product.setProductPackage(productPackage);
        productRepository.save(product);
        productDTO.getZoneIds().forEach(zoneId -> {
            Zone zone = zoneRepository.findByIdAndWarehouseId(zoneId, productDTO.getWarehouseId());
            if (zone != null) {
                zone.setProduct(product);
                zoneRepository.save(zone);
            }
        });
    }

    @Override
    public ProductDTO findProductByIdAndWarehouseId(Long id, Long warehouseId) {
        Product product = productRepository.findByIdAndWarehouseId(id, warehouseId);
        return productMapper.mapToProductDTO(product);
    }

}
