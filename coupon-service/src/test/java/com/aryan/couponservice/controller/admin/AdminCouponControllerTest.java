package com.aryan.couponservice.controller.admin;

import com.aryan.couponservice.dto.CouponDto;
import com.aryan.couponservice.model.Coupon;
import com.aryan.couponservice.services.coupon.AdminCouponService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCouponController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@Slf4j
class AdminCouponControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AdminCouponService adminCouponService;

    private Coupon coupon;
    private Coupon createdCoupon;
    private CouponDto couponDto;



    @BeforeEach
    void setUp() {
        coupon = Coupon.builder().code("FLAT15").build();
        createdCoupon = Coupon.builder()
                .id(1L)
                .name("nomCoupon")
                .discount(1L)
                .expirationDate(new Date())
                .code("FLAT15").build();
        couponDto = new CouponDto();
        couponDto.setCode("FLAT15");

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createCoupon() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,false);
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJSON = objectWriter.writeValueAsString(coupon);
        log.info(requestJSON);

        when(adminCouponService.createCoupon(any(Coupon.class))).thenReturn(createdCoupon);

        mockMvc.perform(post("/api/admin/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJSON))
                .andDo(print()).andExpect(status().isOk());

    }

    @Test
    void getAllCoupon() throws Exception {
        when(adminCouponService.getAllCoupon()).thenReturn(List.of(createdCoupon));
        mockMvc.perform(get("/api/admin/coupons")).andExpect(status().isOk());

    }

    @Test
    void getCouponByCode() throws Exception {
        String code="FLAT15";
        when(adminCouponService.getCouponDtoByCode(code)).thenReturn(Optional.ofNullable(couponDto));
        mockMvc.perform(get("/api/admin/coupons/"+code))
                .andExpect(status().isOk());

    }
}