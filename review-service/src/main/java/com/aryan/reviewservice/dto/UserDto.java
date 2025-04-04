package com.aryan.reviewservice.dto;

import com.aryan.reviewservice.enums.UserRole;
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
