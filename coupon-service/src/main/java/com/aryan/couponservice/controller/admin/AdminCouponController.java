package com.aryan.couponservice.controller.admin;

import com.aryan.couponservice.dto.CouponDto;
import com.aryan.couponservice.model.Coupon;
import com.aryan.couponservice.services.coupon.AdminCouponService;
import com.aryan.couponservice.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
@Slf4j
public class AdminCouponController {
    @Autowired
    private final AdminCouponService adminCouponService;

    @PostMapping
    public ResponseEntity<?> createCoupon(@RequestBody Coupon coupon) {
        log.info("Received request to create coupon with code: {}", coupon.getCode());
        if (coupon.getCode() == null) {
            log.warn("Coupon code is empty");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("codeEmpty");
        }
        try {
            log.info("Creating coupon: {}", coupon);
            Coupon createdCoupon = adminCouponService.createCoupon(coupon);
            log.info("Coupon created with ID: {}", createdCoupon.getId());
            return ResponseEntity.ok(createdCoupon);
        } catch (ValidationException e) {
            log.error("Validation error while creating coupon: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupon() {
        log.info("Received request to get all coupons");
        List<Coupon> coupons = adminCouponService.getAllCoupon();
        log.info("Returning {} coupons", coupons.size());
        return ResponseEntity.ok(coupons);
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getCouponByCode(@PathVariable String code) {
        log.info("Received request to get coupon by code: {}", code);
        Optional<CouponDto> couponDTO = adminCouponService.getCouponDtoByCode(code);

        if (couponDTO.isPresent()) {
            log.info("Coupon is present");
            return ResponseEntity.ok(couponDTO.get());
        } else {
            log.warn("Coupon with code {} not found", code);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Coupon not found"));
        }
    }


}
