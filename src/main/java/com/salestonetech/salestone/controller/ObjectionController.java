package com.salestonetech.salestone.controller;

import com.salestonetech.salestone.controller.dto.ObjectionAnalysisResponseDTO;
import com.salestonetech.salestone.controller.dto.ObjectionResponseDTO;
import com.salestonetech.salestone.job.ObjectionAnalysisJob;
import com.salestonetech.salestone.model.Objection;
import com.salestonetech.salestone.service.ObjectionAnalysisService;
import com.salestonetech.salestone.service.ObjectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/objections")
@RequiredArgsConstructor
public class ObjectionController {

    private final ObjectionAnalysisService objectionAnalysisService;
    private final ObjectionAnalysisJob objectionAnalysisJob;
    private final ObjectionService objectionService;

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

    /**
     * Busca todas as objeções associadas a um vendedor específico.
     *
     * @param userId ID do vendedor (salespersonId).
     * @return Lista de objeções do vendedor.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ObjectionResponseDTO>> getObjectionsByUser(@PathVariable String userId) {
        List<Objection> objections = objectionService.getObjectionsBySalespersonId(userId);
        
        List<ObjectionResponseDTO> response = objections.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(response);
    }

    private ObjectionResponseDTO toResponseDTO(Objection objection) {
        return ObjectionResponseDTO.builder()
                .id(objection.getId())
                .conversationId(objection.getConversation().getId())
                .description(objection.getDescription())
                .quote(objection.getQuote())
                .approxTimestamp(objection.getApproxTimestamp())
                .analyzedAt(objection.getAnalyzedAt())
                .build();
    }
}
