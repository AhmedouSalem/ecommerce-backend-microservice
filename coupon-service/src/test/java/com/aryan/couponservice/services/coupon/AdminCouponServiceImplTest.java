package com.aryan.couponservice.services.coupon;

import com.aryan.couponservice.dto.CouponDto;
import com.aryan.couponservice.model.Coupon;
import com.aryan.couponservice.repository.CouponRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AdminCouponServiceImplTest {

    AutoCloseable autoCloseable;
    Coupon coupon;
    CouponDto couponDto;
    @Mock
    private CouponRepository couponRepository;

    private AdminCouponService adminCouponService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        adminCouponService = new AdminCouponServiceImpl(couponRepository);

        LocalDate now = LocalDate.now();
        LocalDate expirationDateLocal = now.plusDays(15);
        Date expirationDate = Date.from(expirationDateLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());

        coupon = Coupon.builder()
                .id(1L)
                .name("Early Offers")
                .code("FLAT15")
                .discount(15L)
                .expirationDate(expirationDate)
                .build();

    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void createCoupon() {
        when(couponRepository.existsByCode("FLAT15")).thenReturn(false);
        when(couponRepository.save(any())).thenReturn(coupon);
        Coupon savedCoupon = adminCouponService.createCoupon(coupon);
        assertEquals(coupon.getCode(),savedCoupon.getCode());
    }

    @Test
    void getAllCoupon() {
        List<Coupon> coupons = List.of(coupon,coupon);
        when(couponRepository.findAll()).thenReturn(coupons);
        assertEquals(coupons.size(),adminCouponService.getAllCoupon().size());
    }

    @Test
    void getCouponDtoByCode() {
        String code = "FLAT15";
        when(couponRepository.findByCode(code)).thenReturn(Optional.of(coupon));
        Optional<CouponDto> result = adminCouponService.getCouponDtoByCode(code);
        assertTrue(result.isPresent());
        CouponDto dto = result.get();
        assertEquals(coupon.getId(), dto.getId());
        assertEquals(coupon.getName(), dto.getName());
        assertEquals(coupon.getCode(), dto.getCode());
        assertEquals(coupon.getExpirationDate(), dto.getExpirationDate());
        assertEquals(coupon.getDiscount(), dto.getDiscount());
    }

}