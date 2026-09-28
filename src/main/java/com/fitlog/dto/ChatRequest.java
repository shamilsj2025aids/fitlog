package com.fitlog.dto;

import jakarta.validation.constraints.NotBlank;

public class ChatRequest {

    @NotBlank(message = "Message cannot be empty")
    private String message;

    private String apiKey; // Optional: user can supply key via request or application.properties

    public ChatRequest() {
    }

    public ChatRequest(String message, String apiKey) {
        this.message = message;
        this.apiKey = apiKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
