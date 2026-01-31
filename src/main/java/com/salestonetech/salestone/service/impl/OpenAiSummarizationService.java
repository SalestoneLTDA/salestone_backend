package com.salestonetech.salestone.service.impl;

import com.salestonetech.salestone.service.AiSummarizationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@Primary
@Service
public class OpenAiSummarizationService implements AiSummarizationService {

    private final RestTemplate restTemplate;
    private final String model;
    private final String apiUrl;
    private final String apiKey;

    private static final String SYSTEM_PROMPT = """
            You are an AI assistant specialized in analyzing WhatsApp sales conversations.
            
            Your role is to generate professional, clear, and actionable summaries for sales managers and team leaders.
            """;

    private static final String USER_PROMPT_TEMPLATE = """
            Based on the conversations below, generate a structured summary.
            
            The summary must include:
            - Overall context of the conversations
            - Main topics discussed
            - Customer interests and intentions
            - Important questions or requests from customers
            - Sales opportunities or relevant signals
            
            Rules:
            - Do not invent or assume information
            - Base the summary strictly on the provided messages
            - Keep a professional and business-oriented tone
            - Be concise but informative
            
            Conversations:
            %s
            """;

    public OpenAiSummarizationService(
            @Value("${openai.api.key}") String apiKey,
            @Value("${openai.model}") String model,
            @Value("${openai.api.url}") String apiUrl) {
        this.apiKey = apiKey;
        this.model = model;
        this.apiUrl = apiUrl;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public String summarize(String conversationText) {
        try {
            log.info("Requesting summary from OpenAI for conversation length: {}", conversationText.length());
            
            String userPrompt = String.format(USER_PROMPT_TEMPLATE, conversationText);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role", "system", "content", SYSTEM_PROMPT),
                            Map.of("role", "user", "content", userPrompt)
                    ),
                    "temperature", 0.7
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            OpenAiResponse response = restTemplate.postForObject(apiUrl, entity, OpenAiResponse.class);

            if (response != null && response.choices() != null && !response.choices().isEmpty()) {
                return response.choices().get(0).message().content();
            } else {
                log.warn("OpenAI returned empty choices");
                return "Failed to generate summary: Empty response from AI.";
            }
        } catch (Exception e) {
            log.error("Error calling OpenAI API", e);
            return "Error generating summary: " + e.getMessage();
        }
    }

    private record OpenAiResponse(List<Choice> choices) {}
    private record Choice(Message message) {}
    private record Message(String role, String content) {}
}
