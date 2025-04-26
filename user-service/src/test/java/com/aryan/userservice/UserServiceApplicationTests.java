package com.aryan.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "ecom.token=test-token",
        "spring.config.import=optional:classpath:/application.properties"
})
@ComponentScan(basePackages = "com.aryan.userservice")
class UserServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
