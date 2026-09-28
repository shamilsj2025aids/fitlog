package com.fitlog.service;

import com.fitlog.dto.ChatRequest;
import com.fitlog.dto.ChatResponse;

public interface ChatService {
    ChatResponse askFitnessCoach(ChatRequest request);
}
