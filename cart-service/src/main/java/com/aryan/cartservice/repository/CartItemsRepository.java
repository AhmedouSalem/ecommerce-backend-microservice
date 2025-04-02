package com.aryan.cartservice.repository;

import com.aryan.cartservice.dto.CartItemsDto;
import com.aryan.cartservice.model.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemsRepository extends JpaRepository<CartItems, Long>{
	
	Optional<CartItems> findByProductIdAndOrderIdAndUserId(Long productId,Long orderId,Long userId);

    List<CartItems> getCartItemsByOrderId(Long id);
}
