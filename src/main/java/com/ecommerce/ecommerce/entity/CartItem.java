package com.ecommerce.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to the product
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    // Link back to the cart
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal subTotal;

    // Helper method to recalc subtotal
    public void calculateSubTotal() {
        if (product != null && product.getPrice() != null && quantity != null) {
            this.subTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
    }
}
