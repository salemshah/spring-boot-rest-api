package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.entity.*;
import com.ecommerce.ecommerce.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Cart addToCart(User currentUser, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        Cart cart = cartRepository.findByUser(currentUser)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUser(currentUser);
                    return cartRepository.save(c);
                });

        // find existing item
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.calculateSubTotal();
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .build();
            newItem.calculateSubTotal();
            cart.getItems().add(newItem);
        }

        cart.calculateTotalPrice();
        return cartRepository.save(cart);
    }

    public Cart getCartOf(User currentUser) {
        return cartRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
    }

    @Transactional
    public void removeItemFromCart(User currentUser, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        // ownership check
        if (!item.getCart().getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't modify another user's cart");
        }

        Cart cart = item.getCart();
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        cart.calculateTotalPrice();
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(User currentUser) {
        Cart cart = getCartOf(currentUser);
        cart.getItems().clear();
        cart.setTotalPrice(java.math.BigDecimal.ZERO);
        cartRepository.save(cart);
    }
}
