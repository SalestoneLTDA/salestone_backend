package com.salestonetech.salestone.controller;

import com.salestonetech.salestone.controller.dto.ObjectionAnalysisResponseDTO;
import com.salestonetech.salestone.job.ObjectionAnalysisJob;
import com.salestonetech.salestone.service.ObjectionAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/objections")
@RequiredArgsConstructor
public class ObjectionController {

    private final ObjectionAnalysisService objectionAnalysisService;
    private final ObjectionAnalysisJob objectionAnalysisJob;

    /**
     * Endpoint para testar a análise de uma conversa específica manualmente.
     * Retorna as objeções encontradas pela IA para visualização imediata.
     */
    @PostMapping("/analyze/{conversationId}")
    public ResponseEntity<ObjectionAnalysisResponseDTO> analyzeSpecificConversation(@PathVariable UUID conversationId) {
        return ResponseEntity.ok(objectionAnalysisService.analyzeConversation(conversationId));
    }

    /**
     * Endpoint para disparar o Job completo manualmente.
     * Processa todas as conversas, identifica objeções e as persiste no banco de dados.
     */
    @PostMapping("/trigger-job")
    public ResponseEntity<String> triggerJobManually() {
        objectionAnalysisJob.runWeeklyObjectionAnalysis();
        return ResponseEntity.ok("Job de análise de objeções disparado com sucesso.");
    }
}