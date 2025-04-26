package com.aryan.productservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(properties = {
		"spring.profiles.active=test"
})
class ProductServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
