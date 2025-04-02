package com.aryan.couponservice.services.coupon;

import com.aryan.couponservice.dto.CouponDto;
import com.aryan.couponservice.model.Coupon;

import java.util.List;
import java.util.Optional;

public interface AdminCouponService {
    Coupon createCoupon(Coupon coupon);
    List<Coupon> getAllCoupon();

    Optional<CouponDto> getCouponDtoByCode(String code) ;
}
