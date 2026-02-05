package com.salestonetech.salestone.service;

import com.salestonetech.salestone.controller.dto.SummaryRequestDTO;
import com.salestonetech.salestone.controller.dto.SummaryResponseDTO;
import com.salestonetech.salestone.infrastructure.repository.MessageRepository;
import com.salestonetech.salestone.model.Message;
import com.salestonetech.salestone.model.MessageSenderType;
import com.salestonetech.salestone.model.SummaryType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversationSummaryServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private AiSummarizationService aiSummarizationService;

    @InjectMocks
    private ConversationSummaryService summaryService;

    @Test
    void generateSummary_ShouldReturnSummary_WhenMessagesExist() {
        // Arrange
        SummaryRequestDTO request = new SummaryRequestDTO();
        request.setSummaryType(SummaryType.DAILY);
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now());

        Message msg1 = new Message();
        msg1.setContent("Hello");
        msg1.setSenderType(MessageSenderType.SENT);
        msg1.setTimestamp(LocalDateTime.now().minusHours(1));

        Message msg2 = new Message();
        msg2.setContent("Hi");
        msg2.setSenderType(MessageSenderType.RECEIVED);
        msg2.setTimestamp(LocalDateTime.now());

        when(messageRepository.findByTimestampBetweenOrderByTimestampAsc(any(), any()))
                .thenReturn(List.of(msg1, msg2));

        when(aiSummarizationService.summarize(anyString())).thenReturn("Mock Summary");

        // Act
        SummaryResponseDTO response = summaryService.generateSummary(request);

        // Assert
        assertNotNull(response);
        assertEquals("Mock Summary", response.getSummary());
        assertEquals(2, response.getProcessedMessagesCount());
        verify(messageRepository).findByTimestampBetweenOrderByTimestampAsc(any(), any());
        verify(aiSummarizationService).summarize(anyString());
    }

    @Test
    void generateSummary_ShouldReturnEmptyMessage_WhenNoMessagesFound() {
        // Arrange
        SummaryRequestDTO request = new SummaryRequestDTO();
        request.setSummaryType(SummaryType.DAILY);
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now());

        when(messageRepository.findByTimestampBetweenOrderByTimestampAsc(any(), any()))
                .thenReturn(Collections.emptyList());

        // Act
        SummaryResponseDTO response = summaryService.generateSummary(request);

        // Assert
        assertNotNull(response);
        assertEquals("No conversations found for the selected period.", response.getSummary());
        assertEquals(0, response.getProcessedMessagesCount());
        verify(aiSummarizationService, never()).summarize(anyString());
    }
}
