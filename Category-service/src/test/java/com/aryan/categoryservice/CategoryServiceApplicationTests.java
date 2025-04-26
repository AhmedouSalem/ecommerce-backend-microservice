package com.aryan.categoryservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "ecom.token=test-token")
class CategoryServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
