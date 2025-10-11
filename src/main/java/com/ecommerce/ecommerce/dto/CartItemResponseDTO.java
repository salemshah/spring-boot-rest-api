package com.ecommerce.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemResponseDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal subTotal;
}
