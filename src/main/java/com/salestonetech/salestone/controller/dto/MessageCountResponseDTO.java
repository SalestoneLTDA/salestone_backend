package com.salestonetech.salestone.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageCountResponseDTO {
    private LocalDate date;
    private Long count;
}
