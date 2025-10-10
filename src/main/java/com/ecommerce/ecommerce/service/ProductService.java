package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.entity.Product;
import com.ecommerce.ecommerce.exception.DatabaseOperationException;
import com.ecommerce.ecommerce.exception.EntityNotFoundException;
import com.ecommerce.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<Product> filterProducts(String name, String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Specification<Product> spec = Specification.unrestricted();

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%"));
        }

        if (category != null && !category.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("category")), category.trim().toLowerCase()));
        }

        if (minPrice != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }

        if (maxPrice != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        return productRepository.findAll(spec, pageable);
    }


    public Product fetchProductById(Long productId) {
        return productRepository.findById(productId).
                orElseThrow(() -> new EntityNotFoundException("Product", productId));
    }

    public Product createProduct(Product product) {
        try {
            return productRepository.save(product);
        } catch (DataAccessException exception) {
            log.error("Error saving product", exception);
            throw new DatabaseOperationException("Failed to create product", exception);
        }
    }

    public Product updateProduct(Product updatedProduct, Long productId) {
        Product existingProduct = fetchProductById(productId);

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setStockQuantity(updatedProduct.getStockQuantity());

        try {
            return productRepository.save(existingProduct);
        } catch (DataAccessException exception) {
            log.error("Error updating product {}", productId, exception);
            throw new DatabaseOperationException("Failed to create product", exception);
        }
    }

    public String deleteProductById(Long productId) {
        fetchProductById(productId);
        try {
            productRepository.deleteById(productId);
            return "Product deleted successfully (ID: " + productId + ")";
        } catch (DataAccessException exception) {
            log.error("Error deleting product {}", productId, exception);
            throw new DatabaseOperationException("Failed to delete product", exception);
        }
    }

}
