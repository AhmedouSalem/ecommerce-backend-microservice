	package com.aryan.cartservice.model;

    import com.aryan.cartservice.dto.CartItemsDto;
    import com.aryan.cartservice.dto.OrderDto;
    import com.aryan.cartservice.dto.ProductDto;
    import com.aryan.cartservice.dto.UserDto;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import org.hibernate.annotations.OnDelete;
    import org.hibernate.annotations.OnDeleteAction;

    @Entity
    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public class CartItems {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private Long price;

        private Long quantity;

//        @ManyToOne(fetch = FetchType.LAZY, optional = false)
//        @JoinColumn(name = "product_id", nullable = false)
//        @OnDelete(action = OnDeleteAction.CASCADE)

        private Long productId;

        @Transient
        private ProductDto product;

//        @ManyToOne(fetch = FetchType.LAZY, optional = false)
//        @JoinColumn(name = "user_id", nullable = false)
//        @OnDelete(action = OnDeleteAction.CASCADE)
        private Long userId;

        @Transient
        private UserDto user;

//        @ManyToOne(fetch = FetchType.LAZY)
//        @JoinColumn(name = "order_id")

        private Long orderId;
        @Transient
        private OrderDto order;

        public CartItemsDto getCartDto() {
            return CartItemsDto.builder()
                    .id(id)
                    .price(price)
                    .productId(product != null ? product.getId() : null)
                    .quantity(quantity)
                    .userId(user.getId())
                    .productName(product.getName())
                    .returnedImage(product.getByteImg())
                    .build();
        }
    }
