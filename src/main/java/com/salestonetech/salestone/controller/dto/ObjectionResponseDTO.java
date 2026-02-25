package com.salestonetech.salestone.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ObjectionResponseDTO {
    private UUID id;
    private UUID conversationId;
    private String description;
    private String quote;
    private LocalDateTime approxTimestamp;
    private LocalDateTime analyzedAt;
}
