package com.salestonetech.salestone.controller;

import com.salestonetech.salestone.controller.dto.MessageRequestDTO;
import com.salestonetech.salestone.controller.dto.MessageResponseDTO;
import com.salestonetech.salestone.model.MessageSenderType;
import com.salestonetech.salestone.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/sent")
    public ResponseEntity<MessageResponseDTO> receiveSentMessage(@Valid @RequestBody MessageRequestDTO request) {
        MessageResponseDTO response = messageService.processMessage(request, MessageSenderType.SENT);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/received")
    public ResponseEntity<MessageResponseDTO> receiveReceivedMessage(@Valid @RequestBody MessageRequestDTO request) {
        MessageResponseDTO response = messageService.processMessage(request, MessageSenderType.RECEIVED);
        return ResponseEntity.ok(response);
    }
}
