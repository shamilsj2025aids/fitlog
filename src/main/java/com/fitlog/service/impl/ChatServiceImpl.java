package com.fitlog.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitlog.dto.ChatRequest;
import com.fitlog.dto.ChatResponse;
import com.fitlog.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);

    @Value("${groq.api.key:}")
    private String configuredGroqKey;

    @Value("${groq.model:openai/gpt-oss-120b}")
    private String groqModel;

    @Value("${gemini.api.key:}")
    private String configuredGeminiKey;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    private static final String SYSTEM_PROMPT = 
            "You are FitLog AI Coach, a supportive, certified fitness trainer and sports nutritionist. " +
            "Provide concise, clear, and actionable advice on workouts, exercises, calories, macros, or recovery. " +
            "Format responses neatly with bullet points or brief numbered steps when applicable.";

    public ChatServiceImpl(ObjectMapper objectMapper) {
        this.restClient = RestClient.create();
        this.objectMapper = objectMapper;
    }

    @Override
    public ChatResponse askFitnessCoach(ChatRequest request) {
        String userMsg = request.getMessage() != null ? request.getMessage().trim() : "";
        if (userMsg.isEmpty()) {
            return new ChatResponse("Please ask a question regarding workouts, nutrition, or calorie goals!");
        }

        String userSuppliedKey = request.getApiKey() != null ? request.getApiKey().trim() : "";

        // 1. Determine which provider to use
        // Priority: Groq if key starts with gsk_ or configuredGroqKey is set
        if (userSuppliedKey.startsWith("gsk_") || (!userSuppliedKey.startsWith("AIza") && hasValidKey(configuredGroqKey))) {
            String keyToUse = userSuppliedKey.startsWith("gsk_") ? userSuppliedKey : configuredGroqKey;
            try {
                return callGroqApi(userMsg, keyToUse, groqModel);
            } catch (Exception ex) {
                log.warn("Groq primary model failed: {}. Retrying with fallback model openai/gpt-oss-20b...", ex.getMessage());
                try {
                    return callGroqApi(userMsg, keyToUse, "openai/gpt-oss-20b");
                } catch (Exception ex2) {
                    log.error("Groq API call failed: {}", ex2.getMessage());
                    return fallbackOrError("Groq AI Error: " + ex2.getMessage(), userMsg);
                }
            }
        }

        // 2. Try Gemini if key is provided or configured
        if (userSuppliedKey.startsWith("AIza") || hasValidKey(configuredGeminiKey)) {
            String keyToUse = userSuppliedKey.startsWith("AIza") ? userSuppliedKey : configuredGeminiKey;
            try {
                return callGeminiApi(userMsg, keyToUse);
            } catch (Exception ex) {
                log.error("Gemini API call failed: {}", ex.getMessage());
                return fallbackOrError("Gemini AI Error: " + ex.getMessage(), userMsg);
            }
        }

        // 3. Fallback when no API keys are provided
        return getOfflineCoachingResponse(userMsg);
    }

    private boolean hasValidKey(String key) {
        return key != null && !key.isBlank() && !key.contains("YOUR_") && !key.equalsIgnoreCase("none");
    }

    private ChatResponse callGroqApi(String userMessage, String apiKey, String model) throws Exception {
        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", userMessage)
                ),
                "temperature", 0.7,
                "max_tokens", 800
        );

        String responseBody = restClient.post()
                .uri("https://api.groq.com/openai/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode contentNode = root.at("/choices/0/message/content");

        if (!contentNode.isMissingNode()) {
            return new ChatResponse(contentNode.asText());
        }
        return new ChatResponse("Received empty response from Groq AI. Please try again!");
    }

    private ChatResponse callGeminiApi(String userMessage, String apiKey) throws Exception {
        String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", SYSTEM_PROMPT + "\n\nUser Question: " + userMessage)
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
        }
        return new ChatResponse("Received empty response from Gemini AI. Please try again!");
    }

    private ChatResponse fallbackOrError(String errorMsg, String userMessage) {
        ChatResponse fallback = getOfflineCoachingResponse(userMessage);
        return new ChatResponse("Note: " + errorMsg + "\n\n" + fallback.getReply());
    }

    private ChatResponse getOfflineCoachingResponse(String message) {
        String lower = message.toLowerCase();
        if (lower.contains("deficit") || lower.contains("fat loss") || lower.contains("lose weight")) {
            return new ChatResponse(
                    "**FitLog Coach — Calorie Deficit Guide:**\n" +
                    "• **Target:** Aim for a 300 to 500 kcal deficit below your Total Daily Energy Expenditure (TDEE).\n" +
                    "• **Safe Rate:** 0.5 to 1.0 kg of fat loss per week.\n" +
                    "• **Protein:** Keep protein high (1.6 - 2.2g per kg of body weight) to preserve muscle mass.\n" +
                    "• **Tip:** Check your FitLog Daily Summary tab to track your net calories in real-time!"
            );
        } else if (lower.contains("protein") || lower.contains("meal") || lower.contains("diet") || lower.contains("vegetarian")) {
            return new ChatResponse(
                    "**FitLog Coach — High-Protein Meal Plan:**\n" +
                    "• **Breakfast:** Oats with whey/plant protein powder, chia seeds, and berries (30g protein).\n" +
                    "• **Lunch:** Grilled paneer / tofu / chicken breast with brown rice and mixed greens (35g protein).\n" +
                    "• **Snack:** Greek yogurt or roasted chickpeas with a handful of almonds (15g protein).\n" +
                    "• **Dinner:** Lentil dal / soya chunks with quinoa and sautéed vegetables (30g protein)."
            );
        } else if (lower.contains("split") || lower.contains("routine") || lower.contains("workout")) {
            return new ChatResponse(
                    "**FitLog Coach — Recommended 4-Day Upper/Lower Split:**\n" +
                    "• **Mon (Upper A):** Bench Press (4x8), Barbell Rows (4x8), Overhead Press (3x10), Bicep Curls (3x12).\n" +
                    "• **Tue (Lower A):** Squats (4x8), Romanian Deadlifts (3x10), Leg Press (3x12), Calf Raises (4x15).\n" +
                    "• **Thu (Upper B):** Incline Dumbbell Press (4x10), Lat Pulldowns (4x10), Lateral Raises (4x12), Tricep Pushdowns (3x12).\n" +
                    "• **Fri (Lower B):** Deadlifts (3x5), Bulgarian Split Squats (3x10/leg), Leg Curls (3x12), Planks (3x60s)."
            );
        } else if (lower.contains("water") || lower.contains("hydration")) {
            return new ChatResponse(
                    "**FitLog Coach — Hydration Guide:**\n" +
                    "• **Standard:** 35ml per kg of body weight daily (approx. 2.5 to 3.5 liters).\n" +
                    "• **During Workout:** Add 500-750ml for every 60 minutes of intense physical training."
            );
        }
        return new ChatResponse(
                "Hello! I am your FitLog AI Fitness & Nutrition Coach. " +
                "To get live personalized answers using your Groq API key, ensure your key is saved in 'application.properties' " +
                "or configured in the AI Coach settings. You can ask me anything about workouts, nutrition, or calorie tracking!"
        );
    }
}
