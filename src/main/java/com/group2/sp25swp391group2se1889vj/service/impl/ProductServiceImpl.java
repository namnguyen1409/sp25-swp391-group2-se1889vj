package com.group2.sp25swp391group2se1889vj.service.impl;


import com.group2.sp25swp391group2se1889vj.dto.ProductDTO;
import com.group2.sp25swp391group2se1889vj.dto.ProductFilterDTO;
import com.group2.sp25swp391group2se1889vj.entity.Product;
import com.group2.sp25swp391group2se1889vj.entity.ProductPackage;
import com.group2.sp25swp391group2se1889vj.entity.Warehouse;
import com.group2.sp25swp391group2se1889vj.entity.Zone;
import com.group2.sp25swp391group2se1889vj.exception.ProductNoSuchElementException;
import com.group2.sp25swp391group2se1889vj.mapper.ProductMapper;
import com.group2.sp25swp391group2se1889vj.repository.ProductPackageRepository;
import com.group2.sp25swp391group2se1889vj.repository.ProductRepository;
import com.group2.sp25swp391group2se1889vj.repository.WarehouseRepository;
import com.group2.sp25swp391group2se1889vj.repository.ZoneRepository;
import com.group2.sp25swp391group2se1889vj.service.ProductService;
import com.group2.sp25swp391group2se1889vj.service.WarehouseService;
import com.group2.sp25swp391group2se1889vj.specification.ProductSpecification;
import com.group2.sp25swp391group2se1889vj.util.XSSProtectedUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    private final WarehouseService warehouseService;
    private final WarehouseRepository warehouseRepository;
    private ProductRepository productRepository;
    private ProductMapper productMapper;
    private MessageSource messageSource;
    private ProductPackageRepository productPackageRepository;
    private ZoneRepository zoneRepository;
    private XSSProtectedUtil xssProtectedUtil;

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
        return productRepository
                .findTop5ByNameContainingOrDescriptionPlainTextContainingAndWarehouseId(keyword, keyword, warehouseId)
                .stream()
                .map(productMapper::mapToProductDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductDTO> searchProducts(Long warehouseId, ProductFilterDTO productFilterDTO, Pageable pageable) {
        Specification<Product> specification = ProductSpecification.filterProducts(warehouseId, productFilterDTO);
        Page<Product> page = productRepository.findAll(specification, pageable);
        return page.map(productMapper::mapToProductDTO);
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
        product.setDescription(xssProtectedUtil.sanitize(productDTO.getDescription()));
        product.setDescriptionPlainText(xssProtectedUtil.htmlToPlainText(productDTO.getDescription()));
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
    @Transactional
    public void updateProduct(Long id, ProductDTO productDTO) throws Exception {
        Product product = productRepository.findByIdAndWarehouseId(id, productDTO.getWarehouseId());
        if (product == null) {
            throw new Exception("Không tìm thấy sản phẩm");
        }
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setProductPackage(productPackageRepository.findByIdAndWarehouseId(productDTO.getProductPackageId(), productDTO.getWarehouseId()));
        product.setDescription(xssProtectedUtil.sanitize(productDTO.getDescription()));
        product.setDescriptionPlainText(xssProtectedUtil.htmlToPlainText(productDTO.getDescription()));
        productRepository.save(product);

        // hủy liên kết sản phẩm với các khu vực cũ
        List<Zone> zones = zoneRepository.findAllByProductIdAndWarehouseId(id, productDTO.getWarehouseId());
        zones.forEach(zone -> {
            zone.setProduct(null);
            zoneRepository.save(zone);
        });

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
