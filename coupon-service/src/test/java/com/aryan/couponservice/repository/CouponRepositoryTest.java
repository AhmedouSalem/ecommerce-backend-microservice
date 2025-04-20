package com.aryan.couponservice.repository;

import com.aryan.couponservice.model.Coupon;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Slf4j
class CouponRepositoryTest {
    @Autowired
    private CouponRepository couponRepository;
    private Coupon coupon;

    @BeforeEach
    void setUp() {
        coupon = Coupon.builder()
                .name("demoName")
                .code("FLAT50")
                .discount(50L)
                .expirationDate(new Date())
                .build();
        coupon=couponRepository.save(coupon);
    }

    @AfterEach
    void tearDown() {
        couponRepository.deleteAll();
    }

    @Test
    void existsByCode() {
        assertFalse(couponRepository.existsByCode("FLAT"));
        assertTrue(couponRepository.existsByCode("FLAT50"));


    }

    @Test
    void findByCode() {
        assertEquals(couponRepository.findByCode("FLAT50"), Optional.of(coupon));
    }
}