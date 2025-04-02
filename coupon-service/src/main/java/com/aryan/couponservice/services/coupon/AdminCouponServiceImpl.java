package com.aryan.couponservice.services.coupon;

import com.aryan.couponservice.dto.CouponDto;
import com.aryan.couponservice.model.Coupon;
import com.aryan.couponservice.repository.CouponRepository;
import com.aryan.couponservice.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCouponServiceImpl implements AdminCouponService {
    private final CouponRepository couponRepository;

    public Coupon createCoupon(Coupon coupon) {
        if(couponRepository.existsByCode(coupon.getCode())) {
            throw new ValidationException("Coupon code already exists");
        } else {
            log.info("New Coupon Code Added: {}", coupon.getCode());
            return couponRepository.save(coupon);
        }
    }

    public List<Coupon> getAllCoupon() {
        log.info("Fetching all coupons.");
        return couponRepository.findAll();
    }

    public Optional<CouponDto> getCouponDtoByCode(String code) {
        return couponRepository.findByCode(code)
                .map(this::mapToDTO);
    }

    private CouponDto mapToDTO(Coupon coupon) {
        return CouponDto.builder()
                .id(coupon.getId())
                .name(coupon.getName())
                .code(coupon.getCode())
                .discount(coupon.getDiscount())
                .expirationDate(coupon.getExpirationDate())
                .build();
    }

}
