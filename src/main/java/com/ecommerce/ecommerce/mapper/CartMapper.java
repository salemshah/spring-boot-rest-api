package com.ecommerce.ecommerce.mapper;

import com.ecommerce.ecommerce.dto.CartItemResponseDTO;
import com.ecommerce.ecommerce.dto.CartResponseDTO;
import com.ecommerce.ecommerce.entity.Cart;
import com.ecommerce.ecommerce.entity.CartItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CartMapper {

    public CartResponseDTO toResponseDTO(Cart cart) {
        if (cart == null) return null;

        CartResponseDTO dto = new CartResponseDTO();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUser().getId());
        dto.setTotalPrice(cart.getTotalPrice());

        dto.setItems(cart.getItems().stream()
                .map(this::toItemResponseDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    private CartItemResponseDTO toItemResponseDTO(CartItem item) {
        CartItemResponseDTO dto = new CartItemResponseDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setSubTotal(item.getSubTotal());
        return dto;
    }
}
