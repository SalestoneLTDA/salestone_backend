package com.salestonetech.salestone.controller.dto;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ObjectionDTO {
    private String description;
    private String quote;
    private LocalDateTime approxTimestamp;
}