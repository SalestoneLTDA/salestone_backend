package com.salestonetech.salestone.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ObjectionAnalysisResponseDTO {
    private List<ObjectionDTO> objections;
}