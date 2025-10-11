package com.ecommerce.ecommerce.controller;

import com.ecommerce.ecommerce.dto.AddToCartRequestDTO;
import com.ecommerce.ecommerce.dto.CartResponseDTO;
import com.ecommerce.ecommerce.entity.Cart;
import com.ecommerce.ecommerce.entity.User;
import com.ecommerce.ecommerce.mapper.CartMapper;
import com.ecommerce.ecommerce.service.CartService;
import com.ecommerce.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;
    private final CartMapper cartMapper;
    private final UserService userService;

    @PostMapping("/add")
    public ResponseEntity<CartResponseDTO> addToCart(@Valid @RequestBody AddToCartRequestDTO request) {
        User currentUser = userService.getCurrentUser();
        Cart cart = cartService.addToCart(currentUser, request.getProductId(), request.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(cartMapper.toResponseDTO(cart));
    }

    @GetMapping
    public ResponseEntity<CartResponseDTO> getMyCart() {
        User currentUser = userService.getCurrentUser();
        Cart cart = cartService.getCartOf(currentUser);
        return ResponseEntity.ok(cartMapper.toResponseDTO(cart));
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<String> removeItem(@PathVariable Long itemId) {
        User currentUser = userService.getCurrentUser();
        cartService.removeItemFromCart(currentUser, itemId);
        return ResponseEntity.ok("Item removed successfully");
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart() {
        User currentUser = userService.getCurrentUser();
        cartService.clearCart(currentUser);
        return ResponseEntity.ok("Cart cleared successfully");
    }
}
