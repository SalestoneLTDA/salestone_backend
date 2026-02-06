package com.salestonetech.salestone.controller;

import com.salestonetech.salestone.controller.dto.SummaryRequestDTO;
import com.salestonetech.salestone.controller.dto.SummaryResponseDTO;
import com.salestonetech.salestone.service.ConversationSummaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/summaries")
@RequiredArgsConstructor
public class SummaryController {

    private final ConversationSummaryService conversationSummaryService;

    @PostMapping
    public ResponseEntity<SummaryResponseDTO> generateSummary(@Valid @RequestBody SummaryRequestDTO request) {
        return ResponseEntity.ok(conversationSummaryService.generateSummary(request));
    }
}
