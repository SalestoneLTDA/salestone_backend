package com.salestonetech.salestone.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class OpenAiSummarizationServiceTest {

    @Test
    void summarize_ShouldReturnSummary_WhenApiCallIsSuccessful() {
        OpenAiSummarizationService service = new OpenAiSummarizationService("test-key", "gpt-4", "https://api.openai.com/v1/chat/completions");
        
        // Access the internal RestTemplate to bind MockRestServiceServer
        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(service, "restTemplate");
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);

        String mockResponse = """
                {
                  "choices": [
                    {
                      "message": {
                        "role": "assistant",
                        "content": "This is a structured summary."
                      }
                    }
                  ]
                }
                """;

        server.expect(requestTo("https://api.openai.com/v1/chat/completions"))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        String result = service.summarize("Salesperson: Hello");

        assertThat(result).isEqualTo("This is a structured summary.");
    }

    @Test
    void summarize_ShouldHandleError_WhenApiCallFails() {
        OpenAiSummarizationService service = new OpenAiSummarizationService("test-key", "gpt-4", "https://api.openai.com/v1/chat/completions");
        
        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(service, "restTemplate");
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);

        server.expect(requestTo("https://api.openai.com/v1/chat/completions"))
                .andRespond(withServerError());

        String result = service.summarize("Salesperson: Hello");

        assertThat(result).startsWith("Error generating summary");
    }
}
