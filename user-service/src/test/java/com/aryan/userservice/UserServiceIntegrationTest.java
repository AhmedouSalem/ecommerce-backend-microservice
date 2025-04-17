package com.aryan.userservice;

import com.aryan.userservice.dto.SignupRequest;
import com.aryan.userservice.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void signUp_shouldCreateUserAndReturnUserDto() throws JsonProcessingException {
        // Given
        SignupRequest request = SignupRequest.builder()
                .email("integration@example.com")
                .password("secret123")
                .name("Integration User")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SignupRequest> httpEntity = new HttpEntity<>(request, headers);

        String url = "http://localhost:" + port + "/sign-up";

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(url, httpEntity, String.class);

        // Then
        if (response.getStatusCode() == HttpStatus.OK) {
            // C’est une vraie création de compte → tu peux désérialiser
            UserDto dto = new ObjectMapper().readValue(response.getBody(), UserDto.class);
            assertThat(dto.getEmail()).isEqualTo("integration@example.com");
        } else {
            // C’est une erreur (ex: email déjà existant)
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
            assertThat(response.getBody()).contains("already exists");
        }

    }

    @Test
    void authenticate_shouldReturnJwtAndUserInfo() {
        // Préparer utilisateur
        SignupRequest signupRequest = SignupRequest.builder()
                .email("auth-test@example.com")
                .password("authpass")
                .name("Auth Tester")
                .build();
        restTemplate.postForEntity("http://localhost:" + port + "/sign-up", signupRequest, String.class);

        // Préparer authentification
        var request = new com.aryan.userservice.dto.AuthenticationRequest("auth-test@example.com", "authpass");

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var entity = new HttpEntity<>(request, headers);

        var response = restTemplate.postForEntity("http://localhost:" + port + "/authenticate", entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().containsKey("Authorization")).isTrue();

        System.out.println("JWT Token reçu : " + response.getHeaders().getFirst("Authorization"));
        System.out.println("Corps JSON reçu : " + response.getBody());
    }

    @Test
    void getUserById_shouldReturnUserDto() {
        // Créer l’utilisateur
        SignupRequest signupRequest = SignupRequest.builder()
                .email("get-test@example.com")
                .password("getpass")
                .name("Get Tester")
                .build();
        var signUpResponse = restTemplate.postForEntity("http://localhost:" + port + "/sign-up", signupRequest, String.class);

        // Extraire l’ID de la réponse JSON (optionnel si JSON renvoyé)
        Long expectedId = 12L; // Remplace par une extraction réelle si nécessaire

        // Appel au endpoint
        var response = restTemplate.getForEntity("http://localhost:" + port + "/api/users/" + expectedId, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("get-test@example.com");
    }


}
