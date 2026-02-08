package com.salestonetech.salestone.job;

import com.salestonetech.salestone.controller.dto.ObjectionAnalysisResponseDTO;
import com.salestonetech.salestone.infrastructure.repository.ConversationRepository;
import com.salestonetech.salestone.infrastructure.repository.ObjectionRepository;
import com.salestonetech.salestone.model.Conversation;
import com.salestonetech.salestone.model.Objection;
import com.salestonetech.salestone.service.ObjectionAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ObjectionAnalysisJob {

    private final ConversationRepository conversationRepository;
    private final ObjectionAnalysisService objectionAnalysisService;
    private final ObjectionRepository objectionRepository;

    /**
     * Job que roda todo sábado às 08:00 AM.
     * Cron: "0 0 8 * * SAT" (Segundos, Minutos, Horas, Dia do Mês, Mês, Dia da Semana)
     */
    @Scheduled(cron = "0 0 8 * * SAT")
    @Transactional
    public void runWeeklyObjectionAnalysis() {
        log.info("Iniciando Job semanal de análise de objeções - {}", LocalDateTime.now());

        // 1. Busca todas as conversas existentes no sistema [cite: 72]
        List<Conversation> conversations = conversationRepository.findAll();

        if (conversations.isEmpty()) {
            log.info("Nenhuma conversa encontrada para análise.");
            return;
        }

        for (Conversation conversation : conversations) {
            try {
                log.info("Analisando objeções para a conversa: {}", conversation.getId());

                // 2. Chama o serviço de IA (RAG + Gemini) para identificar as objeções
                ObjectionAnalysisResponseDTO analysis = objectionAnalysisService.analyzeConversation(conversation.getId());

                if (analysis.getObjections() != null && !analysis.getObjections().isEmpty()) {

                    // 3. Converte os DTOs retornados pela IA em Entidades de persistência
                    List<Objection> objectionsToSave = analysis.getObjections().stream()
                            .map(dto -> Objection.builder()
                                    .conversation(conversation)
                                    .description(dto.getDescription())
                                    .quote(dto.getQuote())
                                    .approxTimestamp(dto.getApproxTimestamp())
                                    .build())
                            .collect(Collectors.toList());

                    // 4. Salva as objeções encontradas no banco de dados
                    objectionRepository.saveAll(objectionsToSave);
                    log.info("Salvas {} objeções para a conversa {}", objectionsToSave.size(), conversation.getId());
                } else {
                    log.info("Nenhuma objeção encontrada na conversa {}", conversation.getId());
                }

            } catch (Exception e) {
                log.error("Erro ao processar a conversa {}: {}", conversation.getId(), e.getMessage());
                // Continua para a próxima conversa mesmo em caso de erro individual
            }
        }

        log.info("Job semanal de análise de objeções finalizado - {}", LocalDateTime.now());
    }
}