package com.ecommerce.ecommerce.controller;

import com.ecommerce.ecommerce.dto.ProductRequestDTO;
import com.ecommerce.ecommerce.dto.ProductResponseDTO;
import com.ecommerce.ecommerce.entity.Product;
import com.ecommerce.ecommerce.mapper.ProductMapper;
import com.ecommerce.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product")
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping
    @Operation(summary = "Get all products")
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String keyword
    ) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.filterProducts(name, category, minPrice, maxPrice, keyword, pageable);
        Page<ProductResponseDTO> dtoPage = products.map(productMapper::toResponseDTO);

        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long productId) {
        Product product = productService.fetchProductById(productId);
        return ResponseEntity.ok(productMapper.toResponseDTO(product));
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO requestDTO) {

        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        System.out.println("AUTH PRINCIPAL: " + auth.getPrincipal());
        System.out.println("AUTH AUTHORITIES: " + auth.getAuthorities());


        Product product = productMapper.toEntity(requestDTO);
        Product newProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(productMapper.toResponseDTO(newProduct));
    }

    @PutMapping("/{productId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductRequestDTO requestDTO) {
        Product product = productMapper.toEntity(requestDTO);
        Product updatedProduct = productService.updateProduct(product, productId);
        return ResponseEntity.ok(productMapper.toResponseDTO(updatedProduct));
    }

    @DeleteMapping("/{productId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long productId) {
        String message = productService.deleteProductById(productId);
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }
}
