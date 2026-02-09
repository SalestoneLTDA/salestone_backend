package com.salestonetech.salestone.controller.dto;

import com.salestonetech.salestone.model.SummaryType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SummaryRequestDTO {
    @NotNull(message = "Summary type is required")
    private SummaryType summaryType;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private java.util.UUID conversationId;
}
