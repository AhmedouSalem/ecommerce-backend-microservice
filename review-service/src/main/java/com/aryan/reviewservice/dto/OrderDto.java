package com.aryan.reviewservice.dto;

import com.aryan.reviewservice.enums.OrderStatus;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
@Setter
public class OrderDto {
	private Long id;

	private String orderDescription;

	private Date date;

	private Long amount;

	private String address;

	private String payment;

	private OrderStatus orderStatus;

	private Long totalAmount;

	private Long discount;

	private UUID trackingId;

	private String userName;

	private Long userId;

	private List<CartItemsDto> cartItems;
	
	private String couponName;

	private Long couponId;

	private String couponCode;

	@Override
	public String toString() {
		return "OrderDto{" +
				"id=" + id +
				", orderDescription='" + orderDescription + '\'' +
				", date=" + date +
				", amount=" + amount +
				", address='" + address + '\'' +
				", payment='" + payment + '\'' +
				", orderStatus=" + orderStatus +
				", totalAmount=" + totalAmount +
				", discount=" + discount +
				", trackingId=" + trackingId +
				", userName='" + userName + '\'' +
				", userId=" + userId +
				", cartItems=" + cartItems +
				", couponName='" + couponName + '\'' +
				", couponId=" + couponId +
				", couponCode='" + couponCode + '\'' +
				'}';
	}
}
