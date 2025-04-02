package com.aryan.cartservice.dto;

import com.aryan.cartservice.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserDto {
	private Long id;
	private String email;
	private String name;
	private UserRole userRole;
}
