package com.aryan.userservice.web;

import com.aryan.userservice.config.FeignClientInterceptor;
import com.aryan.userservice.dto.OrderRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-service", configuration = FeignClientInterceptor.class)
public interface OrderClient {
    @PostMapping("/api/orders_user_register")
    void createOrder(@RequestBody OrderRequest orderRequest);
}
