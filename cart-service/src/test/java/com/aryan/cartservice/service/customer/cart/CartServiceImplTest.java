
package com.aryan.cartservice.service.customer.cart;

import com.aryan.cartservice.dto.*;
import com.aryan.cartservice.enums.OrderStatus;
import com.aryan.cartservice.exceptions.ValidationException;
import com.aryan.cartservice.feign.CouponFeignClient;
import com.aryan.cartservice.feign.OrderFeignClient;
import com.aryan.cartservice.feign.ProductFeignClient;
import com.aryan.cartservice.feign.UserFeignClient;
import com.aryan.cartservice.model.CartItems;
import com.aryan.cartservice.repository.CartItemsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CartServiceImplTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private OrderFeignClient orderFeignClient;

    @Mock
    private UserFeignClient userFeignClient;

    @Mock
    private CartItemsRepository cartItemsRepository;

    @Mock
    private ProductFeignClient productFeignClient;

    @Mock
    private CouponFeignClient couponFeignClient;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void addProductToCart_shouldAddSuccessfully() {
        AddProductInCartDto dto = new AddProductInCartDto();
        dto.setUserId(1L);
        dto.setProductId(10L);

        OrderDto order = new OrderDto();
        order.setId(100L);
        order.setTotalAmount(0L);
        order.setAmount(0L);

        ProductDto product = new ProductDto();
        product.setId(10L);
        product.setPrice(100L);

        UserDto user = new UserDto();
        user.setId(1L);

        CartItems savedCart = new CartItems();
        savedCart.setId(1L);
        savedCart.setProductId(product.getId());
        savedCart.setUserId(user.getId());
        savedCart.setOrderId(order.getId());
        savedCart.setQuantity(1L);
        savedCart.setPrice(product.getPrice());
        savedCart.setUser(user); // 👈 ajout important
        savedCart.setProduct(product); // 👈 ajout important


        when(orderFeignClient.findByUserIdAnedOrderStatus(1L, OrderStatus.Pending))
                .thenReturn(new ResponseEntity<>(order, HttpStatus.OK));

        when(cartItemsRepository.findByProductIdAndOrderIdAndUserId(10L, 100L, 1L))
                .thenReturn(Optional.empty());

        when(productFeignClient.getProductById(10L))
                .thenReturn(new ResponseEntity<>(product, HttpStatus.OK));

        when(userFeignClient.getUserById(1L))
                .thenReturn(new ResponseEntity<>(user, HttpStatus.OK));

        when(cartItemsRepository.save(any(CartItems.class)))
                .thenReturn(savedCart);

        ResponseEntity<?> response = cartService.addProductToCart(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(orderFeignClient).putOrder(any(OrderDto.class));
    }

    @Test
    public void addProductToCart_shouldReturnConflictIfProductAlreadyInCart() {
        AddProductInCartDto dto = new AddProductInCartDto();
        dto.setUserId(1L);
        dto.setProductId(10L);

        OrderDto order = new OrderDto();
        order.setId(100L);

        CartItems existingItem = new CartItems();

        when(orderFeignClient.findByUserIdAnedOrderStatus(1L, OrderStatus.Pending))
                .thenReturn(new ResponseEntity<>(order, HttpStatus.OK));

        when(cartItemsRepository.findByProductIdAndOrderIdAndUserId(10L, 100L, 1L))
                .thenReturn(Optional.of(existingItem));

        ResponseEntity<?> response = cartService.addProductToCart(dto);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void addProductToCart_shouldReturnNotFoundIfOrderMissing() {
        AddProductInCartDto dto = new AddProductInCartDto();
        dto.setUserId(1L);

        when(orderFeignClient.findByUserIdAnedOrderStatus(1L, OrderStatus.Pending))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

        ResponseEntity<?> response = cartService.addProductToCart(dto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void addProductToCart_shouldReturnNotFoundIfProductMissing() {
        AddProductInCartDto dto = new AddProductInCartDto();
        dto.setUserId(1L);
        dto.setProductId(10L);

        OrderDto order = new OrderDto();
        order.setId(100L);

        when(orderFeignClient.findByUserIdAnedOrderStatus(1L, OrderStatus.Pending))
                .thenReturn(new ResponseEntity<>(order, HttpStatus.OK));

        when(cartItemsRepository.findByProductIdAndOrderIdAndUserId(10L, 100L, 1L))
                .thenReturn(Optional.empty());

        when(productFeignClient.getProductById(10L))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

        ResponseEntity<?> response = cartService.addProductToCart(dto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void addProductToCart_shouldReturnNotFoundIfUserMissing() {
        AddProductInCartDto dto = new AddProductInCartDto();
        dto.setUserId(1L);
        dto.setProductId(10L);

        OrderDto order = new OrderDto();
        order.setId(100L);

        ProductDto product = new ProductDto();
        product.setId(10L);
        product.setPrice(100L);

        when(orderFeignClient.findByUserIdAnedOrderStatus(1L, OrderStatus.Pending))
                .thenReturn(new ResponseEntity<>(order, HttpStatus.OK));

        when(cartItemsRepository.findByProductIdAndOrderIdAndUserId(10L, 100L, 1L))
                .thenReturn(Optional.empty());

        when(productFeignClient.getProductById(10L))
                .thenReturn(new ResponseEntity<>(product, HttpStatus.OK));

        when(userFeignClient.getUserById(1L))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

        ResponseEntity<?> response = cartService.addProductToCart(dto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getCartByUserId_shouldReturnOrderDtoWithCartItems() {
        Long userId = 1L;
        Long orderId = 100L;

        OrderDto order = OrderDto.builder()
                .id(orderId)
                .userId(userId)
                .totalAmount(100L)
                .amount(100L)
                .orderStatus(OrderStatus.Pending)
                .discount(0L)
                .build();

        CartItems cartItem = new CartItems();
        cartItem.setId(1L);
        cartItem.setOrderId(orderId);
        cartItem.setUserId(userId);
        cartItem.setProductId(10L);

        ProductDto product = ProductDto.builder().id(10L).name("Product").price(100L).build();
        UserDto user = UserDto.builder().id(userId).name("Test User").build();

        when(orderFeignClient.getCartByUserId(userId))
                .thenReturn(new ResponseEntity<>(order, HttpStatus.OK));

        when(cartItemsRepository.getCartItemsByOrderId(orderId))
                .thenReturn(List.of(cartItem));

        when(productFeignClient.getProductById(10L))
                .thenReturn(new ResponseEntity<>(product, HttpStatus.OK));

        when(userFeignClient.getUserById(userId))
                .thenReturn(new ResponseEntity<>(user, HttpStatus.OK));

        OrderDto result = cartService.getCartByUserId(userId);

        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals(1, result.getCartItems().size());
        assertEquals("Product", result.getCartItems().get(0).getProductName());
    }

    @Test
    public void getCartByUserId_shouldReturnNullIfOrderNotFound() {
        Long userId = 1L;

        when(orderFeignClient.getCartByUserId(userId))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

        OrderDto result = cartService.getCartByUserId(userId);

        assertNull(result);
    }

    @Test
    public void applyCoupon_shouldApplyCouponSuccessfully() {
        Long userId = 1L;
        String code = "SAVE10";

        OrderDto order = OrderDto.builder()
                .id(100L)
                .userId(userId)
                .totalAmount(200L)
                .amount(200L)
                .build();

        CouponDto coupon = CouponDto.builder()
                .id(1L)
                .code(code)
                .name("Promo10")
                .discount(10L)
                .expirationDate(new Date(System.currentTimeMillis() + 86400000)) // demain
                .build();

        when(orderFeignClient.findByUserIdAnedOrderStatus(userId, OrderStatus.Pending))
                .thenReturn(new ResponseEntity<>(order, HttpStatus.OK));

        when(couponFeignClient.getCouponByCode(code))
                .thenReturn(new ResponseEntity<>(coupon, HttpStatus.OK));

        OrderDto result = cartService.applyCoupon(userId, code);

        assertNotNull(result);
        assertEquals(180L, result.getAmount()); // 10% de 200 = 20
        assertEquals(20L, result.getDiscount());
        verify(orderFeignClient).putOrder(any(OrderDto.class));
    }

    @Test
    public void applyCoupon_shouldThrowValidationExceptionIfCouponExpired() {
        Long userId = 1L;
        String code = "OLD";

        OrderDto order = OrderDto.builder()
                .id(100L)
                .userId(userId)
                .totalAmount(200L)
                .amount(200L)
                .build();

        CouponDto expiredCoupon = CouponDto.builder()
                .id(2L)
                .code(code)
                .name("OldPromo")
                .discount(20L)
                .expirationDate(new Date(System.currentTimeMillis() - 86400000)) // hier
                .build();

        when(orderFeignClient.findByUserIdAnedOrderStatus(userId, OrderStatus.Pending))
                .thenReturn(new ResponseEntity<>(order, HttpStatus.OK));

        when(couponFeignClient.getCouponByCode(code))
                .thenReturn(new ResponseEntity<>(expiredCoupon, HttpStatus.OK));

        assertThrows(ValidationException.class, () -> {
            cartService.applyCoupon(userId, code);
        });
    }

    @Test
    public void applyCoupon_shouldThrowValidationExceptionIfCouponNotFound() {
        Long userId = 1L;
        String code = "UNKNOWN";

        OrderDto order = OrderDto.builder()
                .id(100L)
                .userId(userId)
                .totalAmount(200L)
                .amount(200L)
                .build();

        when(orderFeignClient.findByUserIdAnedOrderStatus(userId, OrderStatus.Pending))
                .thenReturn(new ResponseEntity<>(order, HttpStatus.OK));

        when(couponFeignClient.getCouponByCode(code))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

        assertThrows(ValidationException.class, () -> {
            cartService.applyCoupon(userId, code);
        });
    }

    @Test
    public void applyCoupon_shouldThrowRuntimeExceptionOnOtherHttpError() {
        Long userId = 1L;
        String code = "API_FAIL";

        OrderDto order = OrderDto.builder()
                .id(100L)
                .userId(userId)
                .totalAmount(200L)
                .amount(200L)
                .build();

        when(orderFeignClient.findByUserIdAnedOrderStatus(userId, OrderStatus.Pending))
                .thenReturn(new ResponseEntity<>(order, HttpStatus.OK));

        when(couponFeignClient.getCouponByCode(code))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(RuntimeException.class, () -> {
            cartService.applyCoupon(userId, code);
        });
    }

    @Test
    public void increaseProductQuantity_shouldUpdateCartAndOrder() {
        AddProductInCartDto dto = new AddProductInCartDto();
        dto.setUserId(1L);
        dto.setProductId(10L);

        OrderDto order = OrderDto.builder()
                .id(100L)
                .userId(1L)
                .totalAmount(100L)
                .amount(100L)
                .discount(0L)
                .build();

        ProductDto product = ProductDto.builder()
                .id(10L)
                .price(50L)
                .build();

        CartItems cartItem = new CartItems();
        cartItem.setId(1L);
        cartItem.setProductId(10L);
        cartItem.setUserId(1L);
        cartItem.setOrderId(100L);
        cartItem.setQuantity(1L);
        cartItem.setPrice(50L);

        when(orderFeignClient.findByUserIdAnedOrderStatus(1L, OrderStatus.Pending))
                .thenReturn(new ResponseEntity<>(order, HttpStatus.OK));

        when(productFeignClient.getProductById(10L))
                .thenReturn(new ResponseEntity<>(product, HttpStatus.OK));

        when(cartItemsRepository.findByProductIdAndOrderIdAndUserId(10L, 100L, 1L))
                .thenReturn(Optional.of(cartItem));

        OrderDto result = cartService.increaseProductQuantity(dto);

        assertNotNull(result);
        assertEquals(150L, result.getTotalAmount());
        assertEquals(150L, result.getAmount());
        verify(cartItemsRepository).save(any(CartItems.class));
        verify(orderFeignClient).putOrder(any(OrderDto.class));
    }

    @Test
    public void decreaseProductQuantity_shouldUpdateCartAndOrder() {
        AddProductInCartDto dto = new AddProductInCartDto();
        dto.setUserId(1L);
        dto.setProductId(10L);

        OrderDto order = OrderDto.builder()
                .id(100L)
                .userId(1L)
                .totalAmount(200L)
                .amount(200L)
                .discount(0L)
                .build();

        ProductDto product = ProductDto.builder()
                .id(10L)
                .price(50L)
                .build();

        CartItems cartItem = new CartItems();
        cartItem.setId(1L);
        cartItem.setProductId(10L);
        cartItem.setUserId(1L);
        cartItem.setOrderId(100L);
        cartItem.setQuantity(2L); // on peut décrémenter
        cartItem.setPrice(50L);

        when(orderFeignClient.findByUserIdAnedOrderStatus(1L, OrderStatus.Pending))
                .thenReturn(new ResponseEntity<>(order, HttpStatus.OK));

        when(productFeignClient.getProductById(10L))
                .thenReturn(new ResponseEntity<>(product, HttpStatus.OK));

        when(cartItemsRepository.findByProductIdAndOrderIdAndUserId(10L, 100L, 1L))
                .thenReturn(Optional.of(cartItem));

        OrderDto result = cartService.decreaseProductQuantity(dto);

        assertNotNull(result);
        assertEquals(150L, result.getTotalAmount());
        assertEquals(150L, result.getAmount());
        verify(cartItemsRepository).save(any(CartItems.class));
        verify(orderFeignClient).putOrder(any(OrderDto.class));
    }

    @Test
    public void placedOrder_shouldUpdateCurrentAndCreateNewPendingOrder() {
        PlaceOrderDto dto = PlaceOrderDto.builder()
                .userId(1L)
                .address("123 Rue de Paris")
                .orderDescription("Commande test")
                .build();

        OrderDto currentOrder = OrderDto.builder()
                .id(100L)
                .userId(1L)
                .amount(200L)
                .totalAmount(200L)
                .orderStatus(OrderStatus.Pending)
                .build();

        UserDto user = UserDto.builder()
                .id(1L)
                .name("Ahmedou")
                .build();

        when(orderFeignClient.findByUserIdAnedOrderStatus(1L, OrderStatus.Pending))
                .thenReturn(new ResponseEntity<>(currentOrder, HttpStatus.OK));

        when(userFeignClient.getUserById(1L))
                .thenReturn(new ResponseEntity<>(user, HttpStatus.OK));

        OrderDto result = cartService.placedOrder(dto);

        assertNotNull(result);
        assertEquals(OrderStatus.Placed, result.getOrderStatus());
        assertEquals("123 Rue de Paris", result.getAddress());
        assertNotNull(result.getTrackingId());

        verify(orderFeignClient).putOrder(any(OrderDto.class));
        verify(orderFeignClient).createOrder(any(OrderRequest.class));
    }

    @Test
    public void getMyPlacedOrders_shouldReturnListOfOrders() {
        Long userId = 1L;

        List<OrderDto> orders = List.of(
                OrderDto.builder().id(101L).orderStatus(OrderStatus.Placed).build(),
                OrderDto.builder().id(102L).orderStatus(OrderStatus.Placed).build()
        );

        when(orderFeignClient.getMyPlacedOrders(userId))
                .thenReturn(new ResponseEntity<>(orders, HttpStatus.OK));

        List<OrderDto> result = cartService.getMyPlacedOrders(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(101L, result.get(0).getId());
        assertEquals(OrderStatus.Placed, result.get(0).getOrderStatus());
    }

    @Test
    public void searchOrderByTrackingId_shouldReturnOrderWithUserName() {
        UUID trackingId = UUID.randomUUID();

        OrderDto order = OrderDto.builder()
                .id(100L)
                .userId(1L)
                .orderStatus(OrderStatus.Placed)
                .trackingId(trackingId)
                .build();

        UserDto user = UserDto.builder()
                .id(1L)
                .name("Ahmedou")
                .build();

        when(orderFeignClient.getByTracking(trackingId))
                .thenReturn(new ResponseEntity<>(order, HttpStatus.OK));

        when(userFeignClient.getUserById(1L))
                .thenReturn(new ResponseEntity<>(user, HttpStatus.OK));

        OrderDto result = cartService.searchOrderByTrackingId(trackingId);

        assertNotNull(result);
        assertEquals(trackingId, result.getTrackingId());
        assertEquals("Ahmedou", result.getUserName());
    }

    @Test
    public void getCartItemsByOrderId_shouldReturnListOfCartItemsDto() {
        Long orderId = 100L;
        Long userId = 1L;
        Long productId = 10L;

        CartItems cartItem = new CartItems();
        cartItem.setId(1L);
        cartItem.setOrderId(orderId);
        cartItem.setUserId(userId);
        cartItem.setProductId(productId);
        cartItem.setQuantity(2L);
        cartItem.setPrice(50L);

        ProductDto product = ProductDto.builder()
                .id(productId)
                .name("Test Product")
                .price(50L)
                .build();

        UserDto user = UserDto.builder()
                .id(userId)
                .name("Ahmedou")
                .build();

        when(cartItemsRepository.getCartItemsByOrderId(orderId))
                .thenReturn(List.of(cartItem));

        when(productFeignClient.getProductById(productId))
                .thenReturn(new ResponseEntity<>(product, HttpStatus.OK));

        when(userFeignClient.getUserById(userId))
                .thenReturn(new ResponseEntity<>(user, HttpStatus.OK));

        List<CartItemsDto> result = cartService.getCartItemsByOrderId(orderId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productId, result.get(0).getProductId());
        assertEquals(userId, result.get(0).getUserId());
        assertEquals("Test Product", result.get(0).getProductName());
    }

}
