package com.aryan.cartservice.feign;

import com.aryan.cartservice.config.FeignClientInterceptor;
import com.aryan.cartservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "user-service", configuration = FeignClientInterceptor.class)
public interface UserFeignClient {
    @GetMapping("/api/users/{userID}")
    ResponseEntity<UserDto> getUserById(@PathVariable("userID") Long userID);
}

