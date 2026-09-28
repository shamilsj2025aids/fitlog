package com.fitlog.service;

import com.fitlog.dto.ChatRequest;
import com.fitlog.dto.ChatResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public interface ChatService {
    ChatResponse askFitnessCoach(ChatRequest request);
    void streamFitnessCoach(ChatRequest request, ResponseBodyEmitter emitter);
}

