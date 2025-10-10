package com.ecommerce.ecommerce.dto;

import lombok.Data;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String category;
    private String imageUrl;
    private boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
