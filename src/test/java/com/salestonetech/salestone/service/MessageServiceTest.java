package com.salestonetech.salestone.service;

import com.salestonetech.salestone.controller.dto.MessageCountResponseDTO;
import com.salestonetech.salestone.controller.dto.MessageRequestDTO;
import com.salestonetech.salestone.controller.dto.MessageResponseDTO;
import com.salestonetech.salestone.infrastructure.repository.ConversationRepository;
import com.salestonetech.salestone.infrastructure.repository.MessageRepository;
import com.salestonetech.salestone.infrastructure.repository.projection.DateCountProjection;
import com.salestonetech.salestone.model.Conversation;
import com.salestonetech.salestone.model.Message;
import com.salestonetech.salestone.model.MessageSenderType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    @Test
    void processMessage_ShouldCreateConversation_WhenItDoesNotExist() {
        // Arrange
        MessageRequestDTO request = new MessageRequestDTO();
        request.setSalespersonId("sales-1");
        request.setCustomerPhone("1234567890");
        request.setContent("Hello");

        when(conversationRepository.findBySalespersonIdAndCustomerPhone("sales-1", "1234567890"))
                .thenReturn(Optional.empty());

        when(conversationRepository.save(any(Conversation.class))).thenAnswer(invocation -> {
            Conversation c = invocation.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message m = invocation.getArgument(0);
            m.setId(UUID.randomUUID());
            return m;
        });

        // Act
        MessageResponseDTO response = messageService.processMessage(request, MessageSenderType.SENT);

        // Assert
        assertNotNull(response);
        assertEquals("Hello", response.getContent());
        assertEquals(MessageSenderType.SENT, response.getSenderType());
        verify(conversationRepository).save(any(Conversation.class));
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void processMessage_ShouldUseExistingConversation_WhenItExists() {
        // Arrange
        Conversation existingConversation = new Conversation();
        existingConversation.setId(UUID.randomUUID());
        existingConversation.setSalespersonId("sales-1");
        existingConversation.setCustomerPhone("1234567890");

        MessageRequestDTO request = new MessageRequestDTO();
        request.setSalespersonId("sales-1");
        request.setCustomerPhone("1234567890");
        request.setContent("Reply");

        when(conversationRepository.findBySalespersonIdAndCustomerPhone("sales-1", "1234567890"))
                .thenReturn(Optional.of(existingConversation));

        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message m = invocation.getArgument(0);
            m.setId(UUID.randomUUID());
            return m;
        });

        // Act
        MessageResponseDTO response = messageService.processMessage(request, MessageSenderType.RECEIVED);

        // Assert
        assertNotNull(response);
        assertEquals(existingConversation.getId(), response.getConversationId());
        verify(conversationRepository, never()).save(any(Conversation.class));
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void getMessageCountsByDate_ShouldReturnCounts_WhenNoSalespersonId() {
        // Arrange
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 1, 2);

        DateCountProjection p1 = mock(DateCountProjection.class);
        when(p1.getDate()).thenReturn(start);
        when(p1.getCount()).thenReturn(10L);

        DateCountProjection p2 = mock(DateCountProjection.class);
        when(p2.getDate()).thenReturn(end);
        when(p2.getCount()).thenReturn(5L);

        when(messageRepository.countMessagesByDate(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(p1, p2));

        // Act
        List<MessageCountResponseDTO> result = messageService.getMessageCountsByDate(start, end, null);

        // Assert
        assertEquals(2, result.size());
        assertEquals(start, result.get(0).getDate());
        assertEquals(10L, result.get(0).getCount());
        assertEquals(end, result.get(1).getDate());
        assertEquals(5L, result.get(1).getCount());
        
        verify(messageRepository).countMessagesByDate(any(LocalDateTime.class), any(LocalDateTime.class));
        verify(messageRepository, never()).countMessagesByDateAndSalesperson(any(), any(), any());
    }

    @Test
    void getMessageCountsByDate_ShouldReturnCounts_WhenSalespersonIdProvided() {
        // Arrange
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 1, 2);
        String salespersonId = "sales-123";

        DateCountProjection p1 = mock(DateCountProjection.class);
        when(p1.getDate()).thenReturn(start);
        when(p1.getCount()).thenReturn(8L);

        when(messageRepository.countMessagesByDateAndSalesperson(any(LocalDateTime.class), any(LocalDateTime.class), eq(salespersonId)))
                .thenReturn(List.of(p1));

        // Act
        List<MessageCountResponseDTO> result = messageService.getMessageCountsByDate(start, end, salespersonId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(start, result.get(0).getDate());
        assertEquals(8L, result.get(0).getCount());
        
        verify(messageRepository).countMessagesByDateAndSalesperson(any(LocalDateTime.class), any(LocalDateTime.class), eq(salespersonId));
        verify(messageRepository, never()).countMessagesByDate(any(), any());
    }
}
