package com.salestonetech.salestone.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SummaryResponseDTO {
    private String summary;
    private int processedMessagesCount;
}
