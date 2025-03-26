package com.aryan.orderservice.controller;

import com.aryan.orderservice.dto.OrderRequest;
import com.aryan.orderservice.enums.OrderStatus;
import com.aryan.orderservice.model.Order;
import com.aryan.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ServiceInterfaceController {
    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/api/orders_user_register")
    public ResponseEntity<Void> createOrder(@RequestBody OrderRequest orderRequest) {
        Order order = Order.builder()
                .user_id(orderRequest.getUserId())
                .amount(orderRequest.getAmount())
                .totalAmount(orderRequest.getTotalAmount())
                .discount(orderRequest.getDiscount())
                .orderStatus(OrderStatus.valueOf(orderRequest.getOrderStatus()))
                .build();

        orderRepository.save(order);


        orderRepository.save(order);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/api/orders/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderRepository.findById(id).orElseThrow());
    }

}
