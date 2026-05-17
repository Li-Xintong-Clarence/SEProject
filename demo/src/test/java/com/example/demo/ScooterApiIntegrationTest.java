package com.example.demo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ScooterApiIntegrationTest {

    private static final String BASE_URL = "http://47.108.188.221:8081";
    private static RestTemplate restTemplate;

    @BeforeAll
    static void setUp() {
        restTemplate = new RestTemplate();
    }

    @Test
    void testGetAllScooters_ShouldReturnScooterList() {
        ResponseEntity<Map> response = restTemplate.getForEntity(BASE_URL + "/api/scooters", Map.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
        Object dataObj = body.get("data");
        assertNotNull(dataObj);
        assertInstanceOf(List.class, dataObj);
        List<Object> scooterList = (List<Object>) dataObj;
        assertTrue(scooterList.size() > 0);
        Map<String, Object> firstScooter = (Map<String, Object>) scooterList.get(0);
        assertNotNull(firstScooter.get("id"));
        assertNotNull(firstScooter.get("scooterNumber"));
        assertNotNull(firstScooter.get("status"));
        assertNotNull(firstScooter.get("batteryLevel"));
    }

    @Test
    void testUserLogin_ShouldReturnToken() {
        Map<String, String> loginRequest = Map.of("username", "testuser", "password", "user123");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(loginRequest, headers);
        ResponseEntity<Map> response = restTemplate.exchange(BASE_URL + "/auth/login", HttpMethod.POST, request, Map.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        assertNotNull(data);
        assertNotNull(data.get("token"));
        assertNotNull(data.get("user"));
        String token = (String) data.get("token");
        assertTrue(token.contains("."));
    }

    @Test
    void testGetUserStats_WithValidToken_ShouldReturnStats() {
        Map<String, String> loginRequest = Map.of("username", "testuser", "password", "user123");
        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> loginEntity = new HttpEntity<>(loginRequest, loginHeaders);
        ResponseEntity<Map> loginResponse = restTemplate.exchange(BASE_URL + "/auth/login", HttpMethod.POST, loginEntity, Map.class);
        Map<String, Object> loginData = (Map<String, Object>) loginResponse.getBody().get("data");
        String token = (String) loginData.get("token");

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(token);
        HttpEntity<Void> authEntity = new HttpEntity<>(authHeaders);
        ResponseEntity<Map> response = restTemplate.exchange(BASE_URL + "/api/users/me/stats", HttpMethod.GET, authEntity, Map.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(200, body.get("code"));
        Map<String, Object> stats = (Map<String, Object>) body.get("data");
        assertNotNull(stats);
    }
}
