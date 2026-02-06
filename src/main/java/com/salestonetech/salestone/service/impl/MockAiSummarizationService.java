package com.salestonetech.salestone.service.impl;

import com.salestonetech.salestone.service.AiSummarizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MockAiSummarizationService implements AiSummarizationService {

    private static final String PROMPT_TEMPLATE = """
            System Prompt:
            You are an AI assistant specialized in analyzing sales conversations from WhatsApp.
            
            Your task is to generate a clear, concise, and structured summary of the conversations provided.
            
            The summary must include:
            - Overall context of the conversations
            - Main topics discussed
            - Customer intentions and interests
            - Notable questions or requests from customers
            - Any relevant sales signals or opportunities
            
            Guidelines:
            - Do not invent information
            - Base the summary strictly on the provided messages
            - Write in a professional and business-oriented tone
            - Keep the summary easy to read and actionable for sales managers
            
            Here are the conversations for the selected period:
            %s
            """;

    @Override
    public String summarize(String conversationText) {
        String fullPrompt = String.format(PROMPT_TEMPLATE, conversationText);
        
        log.info("Sending prompt to AI (Mock): \n{}", fullPrompt);
        
        // Simulating AI response
        return """
                **Executive Summary**
                
                **Overall Context:**
                Analysis of sales conversations reveals high engagement regarding the new product line.
                
                **Main Topics:**
                - Pricing inquiries
                - Feature comparisons
                - Delivery timelines
                
                **Customer Intentions:**
                Customers are showing strong purchase intent but require clarification on contract terms.
                
                **Notable Questions:**
                - "Does this integrate with Salesforce?"
                - "What is the cancellation policy?"
                
                **Sales Opportunities:**
                - 3 potential closings identified for next week.
                """;
    }
}
