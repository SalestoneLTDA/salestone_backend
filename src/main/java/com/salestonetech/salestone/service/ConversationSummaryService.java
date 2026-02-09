package com.salestonetech.salestone.service;

import com.salestonetech.salestone.controller.dto.SummaryRequestDTO;
import com.salestonetech.salestone.controller.dto.SummaryResponseDTO;
import com.salestonetech.salestone.infrastructure.repository.MessageRepository;
import com.salestonetech.salestone.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationSummaryService {

    private final MessageRepository messageRepository;
    private final AiSummarizationService aiSummarizationService;
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public SummaryResponseDTO generateSummary(SummaryRequestDTO request) {
        LocalDateTime start = request.getStartDate().atStartOfDay();
        LocalDateTime end = request.getEndDate().atTime(23, 59, 59);

        List<Message> messages;
        if (request.getConversationId() != null) {
            messages = messageRepository.findByConversationIdAndTimestampBetweenOrderByTimestampAsc(
                    request.getConversationId(), start, end);
        } else {
            messages = messageRepository.findByTimestampBetweenOrderByTimestampAsc(start, end);
        }

        if (messages.isEmpty()) {
            return SummaryResponseDTO.builder()
                    .summary("No conversations found for the selected period.")
                    .processedMessagesCount(0)
                    .build();
        }

        String formattedConversation = formatMessagesForAi(messages);
        String summary = aiSummarizationService.summarize(formattedConversation);

        return SummaryResponseDTO.builder()
                .summary(summary)
                .processedMessagesCount(messages.size())
                .build();
    }

    private String formatMessagesForAi(List<Message> messages) {
        return messages.stream()
                .map(msg -> String.format("[%s] %s: %s",
                        msg.getTimestamp().format(DATE_TIME_FORMATTER),
                        mapSenderType(msg.getSenderType()),
                        msg.getContent()))
                .collect(Collectors.joining("\n"));
    }

    private String mapSenderType(com.salestonetech.salestone.model.MessageSenderType senderType) {
        return switch (senderType) {
            case SENT -> "Salesperson";
            case RECEIVED -> "Customer";
        };
    }
}
