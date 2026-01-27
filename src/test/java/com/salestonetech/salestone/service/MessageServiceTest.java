package com.salestonetech.salestone.service;

import com.salestonetech.salestone.controller.dto.MessageRequestDTO;
import com.salestonetech.salestone.controller.dto.MessageResponseDTO;
import com.salestonetech.salestone.infrastructure.repository.ConversationRepository;
import com.salestonetech.salestone.infrastructure.repository.MessageRepository;
import com.salestonetech.salestone.model.Conversation;
import com.salestonetech.salestone.model.Message;
import com.salestonetech.salestone.model.MessageSenderType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
