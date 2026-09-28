package com.fitlog.controller;

import com.fitlog.dto.ChatRequest;
import com.fitlog.dto.ChatResponse;
import com.fitlog.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "AI Chatbot", description = "FitLog AI Fitness and Nutrition Coach powered by Google Gemini")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    @Operation(summary = "Ask the FitLog AI Fitness & Nutrition Coach a question")
    public ResponseEntity<ChatResponse> askCoach(@Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.askFitnessCoach(request));
    }
}
