package com.salestonetech.salestone.controller.dto;

import com.salestonetech.salestone.model.MessageSenderType;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MessageResponseDTO {
    private UUID id;
    private UUID conversationId;
    private String content;
    private MessageSenderType senderType;
    private LocalDateTime timestamp;
}
