package com.sdms.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;


     // Sends a text prompt plus an image to Gemini and returns Gemini's raw text reply.

    public String generateContent(String promptText, byte[] imageBytes, String mimeType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);

        // Build the "parts" list Gemini expects: text always included,
        // an inline base64 image part added only when an image is supplied.
        List<Map<String, Object>> parts = new java.util.ArrayList<>();
        parts.add(Map.of("text", promptText));

        if (imageBytes != null) {
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            parts.add(Map.of(
                    "inline_data", Map.of(
                            "mime_type", mimeType,
                            "data", base64Image
                    )
            ));
        }

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", parts))
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        Map<String, Object> response = restTemplate.postForObject(apiUrl, request, Map.class);

        return extractTextFromResponse(response);
    }

    @SuppressWarnings("unchecked")
    private String extractTextFromResponse(Map<String, Object> response) {
        // Gemini's response shape: { candidates: [ { content: { parts: [ { text: "..." } ] } } ] }
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> responseParts = (List<Map<String, Object>>) content.get("parts");
        return (String) responseParts.get(0).get("text");
    }
}