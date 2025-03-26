package com.aryan.orderservice.model;

import com.aryan.orderservice.dto.UserDto;
import com.aryan.orderservice.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;

    private Long totalAmount;

    private Long discount;

    private OrderStatus orderStatus;

//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user; // Assure-toi que l'entité `User` existe

    private Long user_id;

    @Transient
    private UserDto user;
}
