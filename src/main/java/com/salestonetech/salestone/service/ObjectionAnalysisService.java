package com.salestonetech.salestone.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.salestonetech.salestone.controller.dto.ObjectionAnalysisResponseDTO;
import com.salestonetech.salestone.controller.dto.ObjectionDTO;
import com.salestonetech.salestone.infrastructure.repository.MessageEmbeddingRepository;
import com.salestonetech.salestone.infrastructure.repository.MessageRepository;
import com.salestonetech.salestone.model.Message;
import com.salestonetech.salestone.model.MessageEmbedding;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ObjectionAnalysisService {

    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;
    private final ChatModel chatModel;
    private final MessageEmbeddingRepository messageEmbeddingRepository;

    private static final int TOP_K = 30;

    /**
     * Analisa uma conversa específica usando RAG puro.
     */
    public ObjectionAnalysisResponseDTO analyzeConversation(UUID conversationId) {
        String query = "objeções explícitas do cliente em conversa de vendas WhatsApp preço prazo concorrência confiança necessidade";

        List<org.springframework.ai.document.Document> retrievedDocuments = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(TOP_K)
                        .similarityThreshold(0.65)
                        .filterExpression("conversation_id == '" + conversationId + "'")
                        .build()
        );

        if (retrievedDocuments.isEmpty()) {
            return ObjectionAnalysisResponseDTO.builder().objections(List.of()).build();
        }

        StringBuilder retrievedText = new StringBuilder();
        retrievedDocuments.stream()
                .sorted((a, b) -> {
                    String tsA = (String) a.getMetadata().getOrDefault("timestamp", LocalDateTime.now().toString());
                    String tsB = (String) b.getMetadata().getOrDefault("timestamp", LocalDateTime.now().toString());
                    return tsA.compareTo(tsB);
                })
                .forEach(doc -> {
                    String sender = (String) doc.getMetadata().get("sender_type");
                    String ts = (String) doc.getMetadata().get("timestamp");
                    retrievedText.append(String.format("[%s] %s: %s\n", ts, sender, doc.getText()));
                });

        String formatInstructions = """
                Sua resposta deve ser estritamente um objeto JSON com a seguinte estrutura:
                {
                  "objections": [
                    {
                      "description": "descrição curta da objeção",
                      "quote": "trecho exato da fala do cliente",
                      "approxTimestamp": "ISO8601 timestamp aproximado"
                    }
                  ]
                }
                Se não houver objeções, retorne {"objections": []}.
                """;

        String promptTemplateString = """
                Você é um analista de vendas especializado em conversas WhatsApp.
                Analise APENAS as mensagens abaixo e identifique SOMENTE objeções EXPLÍCITAS do cliente (frases diretas).
                Ignore mensagens do vendedor e objeções implícitas.

                Mensagens recuperadas (as mais relevantes da conversa):
                {retrievedText}

                {formatInstructions}
                """;

        PromptTemplate template = new PromptTemplate(promptTemplateString);

        VertexAiGeminiChatOptions options = VertexAiGeminiChatOptions.builder()
                .responseMimeType("application/json")
                .build();

        Map<String, Object> modelParams = Map.of(
                "retrievedText", retrievedText.toString(),
                "formatInstructions", formatInstructions
        );

        Prompt prompt = new Prompt(template.render(modelParams), options);

        ChatResponse response = chatModel.call(prompt);
        String jsonResponse = response.getResult().getOutput().getText();

        List<ObjectionDTO> objections = parseJsonToObjections(jsonResponse);

        return ObjectionAnalysisResponseDTO.builder()
                .objections(objections)
                .build();
    }

    private List<ObjectionDTO> parseJsonToObjections(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            ObjectionAnalysisResponseDTO dto = mapper.readValue(json, ObjectionAnalysisResponseDTO.class);
            return dto.getObjections() != null ? dto.getObjections() : List.of();
        } catch (Exception e) {
            log.error("Erro ao parsear JSON de objeções: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Gera e persiste o embedding para uma mensagem[cite: 102, 103, 104].
     */
    @Transactional
    public void generateEmbeddingForMessage(Message message) {
        try {
            log.info("Gerando embedding para mensagem ID: {}", message.getId());
            float[] embeddingArray = embeddingModel.embed(message.getContent());

            MessageEmbedding messageEmbedding = MessageEmbedding.builder()
                    .message(message)
                    .embedding(arrayToPgVectorString(embeddingArray))
                    .senderType(message.getSenderType())
                    .timestamp(message.getTimestamp())
                    .build();

            messageEmbeddingRepository.save(messageEmbedding);
            log.info("Embedding salvo com sucesso para mensagem ID: {}", message.getId());
        } catch (Exception e) {
            log.error("Erro ao gerar/salvar embedding: {}", e.getMessage());
        }
    }

    private String arrayToPgVectorString(float[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}