package com.aryan.cartservice.dto;

import com.aryan.cartservice.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private Long userId;
    private Long amount;
    private Long totalAmount;
    private Long discount;
    private String orderStatus; // ex : "Pending"
}
