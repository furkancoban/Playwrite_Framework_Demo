package com.example.orangehrm.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.orangehrm.utils.TestLogger;

/**
 * API Testing Helper - Supports REST API testing alongside UI tests.
 * Provides clean interface for API requests with assertions.
 * Can be integrated with same reporting system as UI tests.
 */
public class APITester {
    
    private HttpClient httpClient;
    private String baseUrl;
    private Map<String, String> defaultHeaders;
    @SuppressWarnings("unused")
	private ObjectMapper objectMapper;
    
    public APITester(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.defaultHeaders = new HashMap<>();
        this.defaultHeaders.put("Content-Type", "application/json");
        this.defaultHeaders.put("Accept", "application/json");
    }
    
    public APITester addHeader(String key, String value) {
        this.defaultHeaders.put(key, value);
        return this;
    }
    
    public APITester setBearerToken(String token) {
        this.defaultHeaders.put("Authorization", "Bearer " + token);
        return this;
    }
    
    /**
     * GET request
     */
    public APIResponse get(String endpoint) {
        return get(endpoint, null);
    }
    
    public APIResponse get(String endpoint, Map<String, String> queryParams) {
        try {
            String url = baseUrl + endpoint;
            if (queryParams != null && !queryParams.isEmpty()) {
                url += "?" + buildQueryString(queryParams);
            }
            
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .GET()
                .uri(new URI(url));
            
            defaultHeaders.forEach(requestBuilder::header);
            
            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            TestLogger.info("✓ GET " + endpoint + " : " + response.statusCode());
            return new APIResponse(response);
        } catch (Exception e) {
            TestLogger.error("✗ GET request failed: " + endpoint, e);
            throw new RuntimeException("API GET request failed: " + endpoint, e);
        }
    }
    
    /**
     * POST request
     */
    public APIResponse post(String endpoint, String body) {
        try {
            String url = baseUrl + endpoint;
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .uri(new URI(url));
            
            defaultHeaders.forEach(requestBuilder::header);
            
            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            TestLogger.info("✓ POST " + endpoint + " : " + response.statusCode());
            return new APIResponse(response);
        } catch (Exception e) {
            TestLogger.error("✗ POST request failed: " + endpoint, e);
            throw new RuntimeException("API POST request failed: " + endpoint, e);
        }
    }
    
    /**
     * PUT request
     */
    public APIResponse put(String endpoint, String body) {
        try {
            String url = baseUrl + endpoint;
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .uri(new URI(url));
            
            defaultHeaders.forEach(requestBuilder::header);
            
            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            TestLogger.info("✓ PUT " + endpoint + " : " + response.statusCode());
            return new APIResponse(response);
        } catch (Exception e) {
            TestLogger.error("✗ PUT request failed: " + endpoint, e);
            throw new RuntimeException("API PUT request failed: " + endpoint, e);
        }
    }
    
    /**
     * DELETE request
     */
    public APIResponse delete(String endpoint) {
        try {
            String url = baseUrl + endpoint;
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .DELETE()
                .uri(new URI(url));
            
            defaultHeaders.forEach(requestBuilder::header);
            
            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            TestLogger.info("✓ DELETE " + endpoint + " : " + response.statusCode());
            return new APIResponse(response);
        } catch (Exception e) {
            TestLogger.error("✗ DELETE request failed: " + endpoint, e);
            throw new RuntimeException("API DELETE request failed: " + endpoint, e);
        }
    }
    
    private String buildQueryString(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        params.forEach((k, v) -> {
            if (sb.length() > 0) sb.append("&");
            sb.append(k).append("=").append(v);
        });
        return sb.toString();
    }
    
    /**
     * API Response wrapper with assertions
     */
    public static class APIResponse {
        private int statusCode;
        private String body;
        @SuppressWarnings("unused")
		private HttpResponse<String> rawResponse;
        
        public APIResponse(HttpResponse<String> response) {
            this.rawResponse = response;
            this.statusCode = response.statusCode();
            this.body = response.body();
        }
        
        public int getStatusCode() {
            return statusCode;
        }
        
        public String getBody() {
            return body;
        }
        
        public APIResponse assertStatusCode(int expectedCode) {
            if (statusCode != expectedCode) {
                throw new AssertionError("Status code mismatch. Expected: " + expectedCode + ", Got: " + statusCode);
            }
            TestLogger.success("✓ Status code verified: " + statusCode);
            return this;
        }
        
        public APIResponse assertStatusSuccess() {
            if (statusCode < 200 || statusCode >= 300) {
                throw new AssertionError("Expected success status code (2xx), got: " + statusCode);
            }
            TestLogger.success("✓ Response successful: " + statusCode);
            return this;
        }
        
        public APIResponse assertBodyContains(String expectedText) {
            if (!body.contains(expectedText)) {
                throw new AssertionError("Response body does not contain: " + expectedText);
            }
            TestLogger.success("✓ Response body contains: " + expectedText);
            return this;
        }
        
        @SuppressWarnings("unchecked")
		public APIResponse assertJsonField(String fieldName, String expectedValue) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> json = mapper.readValue(body, Map.class);
                Object actualValue = json.get(fieldName);
                
                if (!String.valueOf(actualValue).equals(expectedValue)) {
                    throw new AssertionError("JSON field '" + fieldName + "' mismatch. Expected: " + expectedValue + ", Got: " + actualValue);
                }
                TestLogger.success("✓ JSON field '" + fieldName + "' verified: " + expectedValue);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse or verify JSON", e);
            }
            return this;
        }
    }
}
