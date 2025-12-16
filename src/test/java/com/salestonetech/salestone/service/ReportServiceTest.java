package com.salestonetech.salestone.service;

import com.salestonetech.salestone.infrastructure.repository.ReportRepository;
import com.salestonetech.salestone.model.Report;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private PdfGenerationService pdfGenerationService;

    @InjectMocks
    private ReportService reportService;

    private Report report;

    @BeforeEach
    void setUp() {
        report = new Report();
        report.setId("rep-1");
        report.setUserId("user-1");
        report.setS3Key("reports/rep-1.txt");
    }

    @Test
    void exportReportAsPdf_Success() throws IOException {
        when(reportRepository.findById("rep-1")).thenReturn(Optional.of(report));
        
        String content = "Test Report Content";
        when(fileStorageService.getFileContent("reports/rep-1.txt"))
                .thenReturn(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
        
        byte[] pdfBytes = new byte[]{1, 2, 3}; // Mock PDF content
        when(pdfGenerationService.convertTextToPdf(content)).thenReturn(pdfBytes);

        byte[] result = reportService.exportReportAsPdf("user-1", "rep-1");

        assertNotNull(result);
        assertArrayEquals(pdfBytes, result);
        verify(reportRepository).findById("rep-1");
        verify(fileStorageService).getFileContent("reports/rep-1.txt");
        verify(pdfGenerationService).convertTextToPdf(content);
    }

    @Test
    void exportReportAsPdf_AccessDenied() {
        when(reportRepository.findById("rep-1")).thenReturn(Optional.of(report));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            reportService.exportReportAsPdf("user-2", "rep-1")
        );

        assertEquals("Access denied", exception.getMessage());
        verify(fileStorageService, never()).getFileContent(anyString());
    }

    @Test
    void exportReportAsPdf_ReportNotFound() {
        when(reportRepository.findById("rep-1")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            reportService.exportReportAsPdf("user-1", "rep-1")
        );

        assertEquals("Report not found", exception.getMessage());
    }
}
