package com.aryan.cartservice.feign;

import com.aryan.cartservice.config.FeignClientInterceptor;
import com.aryan.cartservice.dto.OrderDto;
import com.aryan.cartservice.dto.OrderRequest;
import com.aryan.cartservice.enums.OrderStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order-service", configuration = FeignClientInterceptor.class)
public interface OrderFeignClient {
    @GetMapping("/api/microservice/{userId}/{status}")
    ResponseEntity<OrderDto> findByUserIdAnedOrderStatus(@PathVariable Long userId, @PathVariable OrderStatus status);
    @PutMapping("/api/microservice/addorder/orders")
    ResponseEntity<?> putOrder(@RequestBody OrderDto orderDto);
    @PostMapping("/api/microservice/orders")
    public ResponseEntity<Void> createOrder(@RequestBody OrderRequest orderRequest);
    @GetMapping("/api/microservice/getmyplacedorder/orders/{userId}")
    ResponseEntity<OrderDto>  getCartByUserId(@PathVariable("userId") Long userId);
    @GetMapping("/api/microservice/myOrders/{userId}")
    public ResponseEntity<List<OrderDto>> getMyPlacedOrders(@PathVariable Long userId);
    @GetMapping("/api/microservice/getmyplacedorder/tracking/{trackingId}")
    ResponseEntity<OrderDto>  getByTracking(@PathVariable("trackingId") UUID trackingId);
}
