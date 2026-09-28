package com.fitlog.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitlog.dto.ChatRequest;
import com.fitlog.dto.ChatResponse;
import com.fitlog.service.ChatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GeminiChatServiceImpl implements ChatService {

    @Value("${gemini.api.key:}")
    private String configuredApiKey;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public GeminiChatServiceImpl(ObjectMapper objectMapper) {
        this.restClient = RestClient.create();
        this.objectMapper = objectMapper;
    }

    @Override
    public ChatResponse askFitnessCoach(ChatRequest request) {
        String apiKey = (request.getApiKey() != null && !request.getApiKey().isBlank())
                ? request.getApiKey().trim()
                : configuredApiKey;

        if (apiKey == null || apiKey.isBlank() || apiKey.equalsIgnoreCase("YOUR_GEMINI_API_KEY_HERE")) {
            return new ChatResponse(
                    "Hello! I am your FitLog AI Fitness & Nutrition Coach. " +
                    "To enable live Google Gemini responses, please set your Gemini API key in 'application.properties' (property: gemini.api.key) " +
                    "or enter it in the AI Coach key field. Meanwhile, remember: maintaining a consistent daily calorie balance and adequate hydration is key to reaching your fitness goals!"
            );
        }

        try {
            String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

            String prompt = "You are FitLog AI, a knowledgeable, motivating personal fitness trainer and nutritionist. " +
                    "Provide clear, concise, actionable advice regarding workouts, exercise form, calories, macro balance, recovery, or weight management. " +
                    "User question: " + request.getMessage();

            Map<String, Object> body = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
                            ))
                    )
            );

            String responseBody = restClient.post()
                    .uri(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode textNode = root.at("/candidates/0/content/parts/0/text");

            if (!textNode.isMissingNode()) {
                return new ChatResponse(textNode.asText());
            } else {
                return new ChatResponse("I received your question, but could not generate a response. Please try rephrasing!");
            }
        } catch (Exception ex) {
            return new ChatResponse("FitLog AI Service Note: " + (ex.getMessage() != null ? ex.getMessage() : "Unable to reach Gemini API. Please check your API key."));
        }
    }
}
