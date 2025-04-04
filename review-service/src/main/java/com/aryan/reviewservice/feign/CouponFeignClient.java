package com.aryan.reviewservice.feign;


import com.aryan.reviewservice.config.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "coupon-service", configuration = FeignClientInterceptor.class)
public interface CouponFeignClient {
    @GetMapping("/api/admin/coupons/{id}") // ici, nous utilisons 'id' pour correspondre à la méthode du contrôleur
    ResponseEntity<Object> getCouponByCode(@PathVariable String id);  // 'id' doit correspondre au paramètre de la méthode
}
