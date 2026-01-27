package com.salestonetech.salestone.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageRequestDTO {
    @NotBlank(message = "Salesperson ID is required")
    private String salespersonId;

    @NotBlank(message = "Customer phone is required")
    private String customerPhone;

    @NotBlank(message = "Content is required")
    private String content;

    private LocalDateTime timestamp;
}
