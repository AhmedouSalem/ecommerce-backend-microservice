package com.aryan.cartservice.service.customer.cart;

import java.util.*;
import java.util.stream.Collectors;

import com.aryan.cartservice.dto.*;
import com.aryan.cartservice.enums.OrderStatus;
import com.aryan.cartservice.exceptions.ValidationException;
import com.aryan.cartservice.feign.CouponFeignClient;
import com.aryan.cartservice.feign.OrderFeignClient;
import com.aryan.cartservice.feign.ProductFeignClient;
import com.aryan.cartservice.feign.UserFeignClient;
import com.aryan.cartservice.model.CartItems;
import com.aryan.cartservice.repository.CartItemsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

	@Autowired
	private final OrderFeignClient orderFeignClient;

	@Autowired
	private final UserFeignClient userFeignClient;

	@Autowired
	private final CartItemsRepository cartItemsRepository;

	@Autowired
	private final ProductFeignClient productFeignClient;

	@Autowired
	private final CouponFeignClient couponFeignClient;

	public ResponseEntity<?> addProductToCart(AddProductInCartDto addProductInCartDto) {

		log.info("Received request to add product to cart: {}", addProductInCartDto);

		// Vérifier si l'order est existant
		ResponseEntity<OrderDto> response = orderFeignClient.findByUserIdAnedOrderStatus(addProductInCartDto.getUserId(), OrderStatus.Pending);
		if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
			log.info("Active order not found for user with ID: {}", addProductInCartDto.getUserId());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order is empty");
		}

		OrderDto activeOrder = response.getBody();

		// Vérifier si le produit est déjà dans le panier
		Optional<CartItems> optionalCartItems = cartItemsRepository.findByProductIdAndOrderIdAndUserId(
				addProductInCartDto.getProductId(), activeOrder.getId(), addProductInCartDto.getUserId());

		if (optionalCartItems.isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Product already in cart");
		}

		// Vérifier si le produit existe
		ResponseEntity<ProductDto> productResponse = productFeignClient.getProductById(addProductInCartDto.getProductId());
		if (!productResponse.getStatusCode().is2xxSuccessful() || productResponse.getBody() == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
		}
		ProductDto productDto = productResponse.getBody();

		// Vérifier si l'utilisateur existe
		log.info("Active order found for user with ID>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>: {}", addProductInCartDto.getUserId());
		ResponseEntity<UserDto> userResponse = userFeignClient.getUserById(addProductInCartDto.getUserId());
		if (!userResponse.getStatusCode().is2xxSuccessful() || userResponse.getBody() == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}
		UserDto userDto = userResponse.getBody();

		// Ajouter le produit au panier
		CartItems cartItems = new CartItems();
		cartItems.setProduct(productDto);
		cartItems.setPrice(productDto.getPrice());
		cartItems.setQuantity(1L);
		cartItems.setUser(userDto);
		cartItems.setOrder(activeOrder);
		cartItems.setUserId(userDto.getId());
		cartItems.setOrderId(activeOrder.getId());
		cartItems.setProductId(productDto.getId());

		CartItems savedCartItem = cartItemsRepository.save(cartItems);
		List<CartItemsDto> cartItemsDtoList= Arrays.asList(savedCartItem.getCartDto());

		// Mettre à jour le total de la commande
		activeOrder.setTotalAmount(activeOrder.getTotalAmount() + savedCartItem.getPrice());
		activeOrder.setAmount(activeOrder.getAmount() + savedCartItem.getPrice());
		activeOrder.setCartItems(cartItemsDtoList);
		activeOrder.setUserId(userDto.getId());


		// Sauvegarder la commande mise à jour
		orderFeignClient.putOrder(activeOrder);

		return ResponseEntity.status(HttpStatus.CREATED).body(savedCartItem);
	}


	public OrderDto getCartByUserId(Long userId) {
		// Récupérer la commande active pour l'utilisateur
		ResponseEntity<OrderDto> orderResponse = orderFeignClient.getCartByUserId(userId);

		// Vérifier la réponse du Feign client
		if (!orderResponse.getStatusCode().is2xxSuccessful() || orderResponse.getBody() == null) {
			log.error("No active order found for user with ID: {}", userId);
			return null;  // Ou retourne un message d'erreur approprié si nécessaire
		}

		// Récupérer l'objet OrderDto
		OrderDto activeOrder = orderResponse.getBody();

		// Récupérer les éléments du panier (cart items)
		List<CartItems> cartItemsList = cartItemsRepository.getCartItemsByOrderId(orderResponse.getBody().getId());
		List<CartItemsDto> cartItemsDtoList = new ArrayList<>();
		for (CartItems cartItems : cartItemsList) {
			ProductDto p = productFeignClient.getProductById(cartItems.getProductId()).getBody();
			cartItems.setProduct(p);
			cartItems.setUser(userFeignClient.getUserById(cartItems.getUserId()).getBody());
			cartItemsDtoList.add(cartItems.getCartDto());
		}


		// Créer un nouvel objet OrderDto pour encapsuler la réponse
		OrderDto orderDto = new OrderDto();
		orderDto.setId(activeOrder.getId());
		orderDto.setAmount(activeOrder.getAmount());
		orderDto.setOrderStatus(activeOrder.getOrderStatus());
		orderDto.setDiscount(activeOrder.getDiscount());
		orderDto.setTotalAmount(activeOrder.getTotalAmount());
		orderDto.setCartItems(cartItemsDtoList);
		orderDto.setUserId(activeOrder.getUserId());

		// Si un coupon est associé à la commande, l'ajouter à la réponse
		if (activeOrder.getCouponId() != null) {
			orderDto.setCouponId(activeOrder.getCouponId());
			orderDto.setCouponCode(activeOrder.getCouponCode());
			orderDto.setCouponName(activeOrder.getCouponName());
		}

		return orderDto;
	}


	public OrderDto applyCoupon(Long userId, String code) {
		// Récupérer la commande active pour l'utilisateur
		ResponseEntity<OrderDto> orderResponse = orderFeignClient.findByUserIdAnedOrderStatus(userId, OrderStatus.Pending);

		// Vérifier la réponse du Feign client
		if (!orderResponse.getStatusCode().is2xxSuccessful() || orderResponse.getBody() == null) {
			log.error("No active order found for user with ID: {}", userId);
			return null;  // Ou retourne un message d'erreur approprié si nécessaire
		}

		// Récupérer l'objet OrderDto
		OrderDto activeOrder = orderResponse.getBody();
		// Récupérer le coupon via le client Feign
		ResponseEntity<Object> responseEntity = couponFeignClient.getCouponByCode(code);

		// Vérifier si le coupon existe
		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			// Utiliser ObjectMapper pour transformer le LinkedHashMap en CouponDto
			ObjectMapper objectMapper = new ObjectMapper();
			CouponDto coupon = objectMapper.convertValue(responseEntity.getBody(), CouponDto.class);

			// Vérifier si le coupon est expiré
			if (couponIsExpired(coupon)) {
				throw new ValidationException("coupon is expired");
			}

			// Appliquer le coupon
			double discountAmount = (coupon.getDiscount() / 100.0) * activeOrder.getTotalAmount();
			double netAmount = activeOrder.getTotalAmount() - discountAmount;

			activeOrder.setAmount((long) netAmount);
			activeOrder.setDiscount((long) discountAmount);
			activeOrder.setCouponId(coupon.getId());
			activeOrder.setCouponCode(coupon.getCode());
			activeOrder.setCouponName(coupon.getName());

			orderFeignClient.putOrder(activeOrder);
			return activeOrder;
		} else {
			// Gérer l'erreur si le coupon n'est pas trouvé
			if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new ValidationException("Coupon not found with code: " + code);
			} else {
				throw new RuntimeException("Error while fetching coupon: " + responseEntity.getStatusCode());
			}
		}

	}

	public boolean couponIsExpired(CouponDto coupon) {
		Date currentDate = new Date();
		Date expirationDate = coupon.getExpirationDate();
		return expirationDate != null && currentDate.after(expirationDate);
	}

	public OrderDto increaseProductQuantity(AddProductInCartDto addProductInCartDto) {
		// Récupérer la commande active pour l'utilisateur
		ResponseEntity<OrderDto> orderResponse = orderFeignClient.findByUserIdAnedOrderStatus(addProductInCartDto.getUserId(), OrderStatus.Pending);

		// Vérifier la réponse du Feign client
		if (!orderResponse.getStatusCode().is2xxSuccessful() || orderResponse.getBody() == null) {
			log.error("No active order found for user with ID: {}", addProductInCartDto.getUserId());
			return null;  // Ou retourne un message d'erreur approprié si nécessaire
		}

		// Récupérer l'objet OrderDto
		OrderDto activeOrder = orderResponse.getBody();
		ResponseEntity<ProductDto> optionalProduct = productFeignClient.getProductById(addProductInCartDto.getProductId());
		Optional<CartItems> optionalCartItem = cartItemsRepository.findByProductIdAndOrderIdAndUserId(
				optionalProduct.getBody().getId(), activeOrder.getId(), addProductInCartDto.getUserId());

		if (optionalCartItem.isPresent()) {
			CartItems cartItems = optionalCartItem.get();
			ProductDto product = optionalProduct.getBody();

			activeOrder.setAmount(activeOrder.getAmount() + product.getPrice());
			activeOrder.setTotalAmount(activeOrder.getTotalAmount() + product.getPrice());

			cartItems.setQuantity(cartItems.getQuantity() + 1);

			if (activeOrder.getCouponId() != null) {
				double discountAmount = ((activeOrder.getDiscount() / 100.0)
						* activeOrder.getTotalAmount());
				double netAmount = activeOrder.getTotalAmount() - discountAmount;

				activeOrder.setAmount((long) netAmount);
				activeOrder.setDiscount((long) discountAmount);
			}

			cartItemsRepository.save(cartItems);
			orderFeignClient.putOrder(activeOrder);
			return activeOrder;
		}
		return null;
	}

	public OrderDto decreaseProductQuantity(AddProductInCartDto addProductInCartDto) {
		// Récupérer la commande active pour l'utilisateur
		ResponseEntity<OrderDto> orderResponse = orderFeignClient.findByUserIdAnedOrderStatus(addProductInCartDto.getUserId(), OrderStatus.Pending);

		// Vérifier la réponse du Feign client
		if (!orderResponse.getStatusCode().is2xxSuccessful() || orderResponse.getBody() == null) {
			log.error("No active order found for user with ID: {}", addProductInCartDto.getProductId());
			return null;  // Ou retourne un message d'erreur approprié si nécessaire
		}

		// Récupérer l'objet OrderDto
		OrderDto activeOrder = orderResponse.getBody();
		ResponseEntity<ProductDto> optionalProduct = productFeignClient.getProductById(addProductInCartDto.getProductId());
		Optional<CartItems> optionalCartItem = cartItemsRepository.findByProductIdAndOrderIdAndUserId(
				optionalProduct.getBody().getId(), activeOrder.getId(), addProductInCartDto.getUserId());

		if (optionalCartItem.isPresent()) {
			CartItems cartItems = optionalCartItem.get();
			ProductDto product = optionalProduct.getBody();

			// update amount
			activeOrder.setAmount(activeOrder.getAmount() - product.getPrice());
			activeOrder.setTotalAmount(activeOrder.getTotalAmount() - product.getPrice());

			// decrease quantity
			cartItems.setQuantity(cartItems.getQuantity() - 1);

			if (activeOrder.getCouponId() != null) {
				double discountAmount = ((activeOrder.getDiscount() / 100.0)
						* activeOrder.getTotalAmount());
				double netAmount = activeOrder.getTotalAmount() - discountAmount;

				activeOrder.setAmount((long) netAmount);
				activeOrder.setDiscount((long) discountAmount);
			}

			cartItemsRepository.save(cartItems);
			orderFeignClient.putOrder(activeOrder);
			return activeOrder;
		}
		return null;
	}

	public OrderDto placedOrder(PlaceOrderDto placeOrderDto) {
		// Récupérer la commande active pour l'utilisateur
		ResponseEntity<OrderDto> orderResponse = orderFeignClient.findByUserIdAnedOrderStatus(placeOrderDto.getUserId(), OrderStatus.Pending);

		// Vérifier la réponse du Feign client
		if (!orderResponse.getStatusCode().is2xxSuccessful() || orderResponse.getBody() == null) {
			log.error("No active order found for user with ID: {}", placeOrderDto.getUserId());
			return null;  // Ou retourne un message d'erreur approprié si nécessaire
		}

		// Récupérer l'objet OrderDto
		OrderDto activeOrder = orderResponse.getBody();
		log.info("Active order found for user with ID: {}>>>>>>>>>>>>>>>>>>>>>>>>>>>", placeOrderDto.getUserId());
		ResponseEntity<UserDto> optionalUser = userFeignClient.getUserById(placeOrderDto.getUserId());
		if (optionalUser != null && optionalUser.getStatusCode().is2xxSuccessful()) {
			activeOrder.setOrderDescription(placeOrderDto.getOrderDescription());

			// Update the active order with the new details
			activeOrder.setAddress(placeOrderDto.getAddress());
			activeOrder.setDate(new Date());
			activeOrder.setOrderStatus(OrderStatus.Placed);
			activeOrder.setTrackingId(UUID.randomUUID());

			// Save the updated order
			orderFeignClient.putOrder(activeOrder);
			log.info("Updated order saved with ID: {}", activeOrder.getId());
			log.info("Updated order date: {}", activeOrder.getDate());


			// Create a new empty pending order for the user
			OrderRequest orderRequest = new OrderRequest();
			orderRequest.setAmount(0L);
			orderRequest.setTotalAmount(0L);
			orderRequest.setDiscount(0L);
			orderRequest.setUserId(optionalUser.getBody().getId());
			orderRequest.setOrderStatus(String.valueOf(OrderStatus.Pending));

			// Save the new pending order
			orderFeignClient.createOrder(orderRequest);
			//
			log.info("new pending order created : {}",orderRequest.toString());

			// Return the DTO of the active order
			return activeOrder;
		}
		return null;
	}

	public List<OrderDto> getMyPlacedOrders(Long userId) {
		ResponseEntity<List<OrderDto>> orderResponse = orderFeignClient.getMyPlacedOrders(userId);
		if (!orderResponse.getStatusCode().is2xxSuccessful() || orderResponse.getBody() == null) {
			log.error("No active order found for user with ID: {}", userId);
			return null;
		}
		return orderResponse.getBody();
	}

	public OrderDto searchOrderByTrackingId(UUID trackingId) {
		ResponseEntity<OrderDto> orderDtoResponseEntity = orderFeignClient.getByTracking(trackingId);
		if (!orderDtoResponseEntity.getStatusCode().is2xxSuccessful() || orderDtoResponseEntity.getBody() == null) {
			log.error("No active order found for user with ID: {}", trackingId);
			return null;
		}
		OrderDto orderDto = orderDtoResponseEntity.getBody();
		return orderDto;
	}

}
