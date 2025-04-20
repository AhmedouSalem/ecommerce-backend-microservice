package com.aryan.cartservice.repository;

import com.aryan.cartservice.model.CartItems;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE) // Pour forcer l'utilisation de H2 en mémoire
public class CartItemsRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Test
    void testFindByProductIdAndOrderIdAndUserId_shouldReturnItem() {
        // setup
        CartItems item = new CartItems();
        item.setProductId(10L);
        item.setOrderId(100L);
        item.setUserId(1L);
        item.setPrice(50L);
        item.setQuantity(2L);
        entityManager.persist(item);
        entityManager.flush();

        // test
        Optional<CartItems> result = cartItemsRepository.findByProductIdAndOrderIdAndUserId(10L, 100L, 1L);

        // assert
        assertTrue(result.isPresent());
        assertEquals(50L, result.get().getPrice());
        assertEquals(2L, result.get().getQuantity());
    }

    @Test
    void testFindByProductIdAndOrderIdAndUserId_shouldReturnEmpty() {
        Optional<CartItems> result = cartItemsRepository.findByProductIdAndOrderIdAndUserId(999L, 999L, 999L);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetCartItemsByOrderId_shouldReturnCorrectItems() {
        CartItems item1 = new CartItems();
        item1.setProductId(10L);
        item1.setOrderId(200L);
        item1.setUserId(1L);
        item1.setPrice(30L);
        item1.setQuantity(1L);

        CartItems item2 = new CartItems();
        item2.setProductId(11L);
        item2.setOrderId(200L);
        item2.setUserId(2L);
        item2.setPrice(40L);
        item2.setQuantity(3L);

        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.flush();

        List<CartItems> result = cartItemsRepository.getCartItemsByOrderId(200L);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(i -> i.getProductId().equals(10L)));
        assertTrue(result.stream().anyMatch(i -> i.getProductId().equals(11L)));
    }

    @Test
    void testGetCartItemsByOrderId_shouldReturnEmptyList() {
        List<CartItems> result = cartItemsRepository.getCartItemsByOrderId(999L);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
