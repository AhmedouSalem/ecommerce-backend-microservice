package com.aryan.couponservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponDto {
    private Long id;

    private String name;

    private String code;

    private Long discount;

    private Date expirationDate;
}