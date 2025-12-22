package com.salestonetech.salestone.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfGenerationService {

    private static final float MARGIN_X = 50;
    private static final float MARGIN_Y = 750;
    private static final float LEADING = 14.5f;
    private static final float FONT_SIZE = 12f;

    public byte[] convertTextToPdf(String text) throws IOException {

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            // 🔹 Carrega fonte Unicode (DejaVu)
            PDType0Font font = PDType0Font.load(
                    document,
                    getClass().getResourceAsStream("/fonts/dejavu-sans.book.ttf"));

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                contentStream.beginText();
                contentStream.setFont(font, FONT_SIZE);
                contentStream.setLeading(LEADING);
                contentStream.newLineAtOffset(MARGIN_X, MARGIN_Y);

                String sanitizedText = sanitizeText(text);
                String[] lines = sanitizedText.split("\n");

                for (String line : lines) {
                    contentStream.showText(line.isEmpty() ? " " : line);
                    contentStream.newLine();
                }

                contentStream.endText();
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    private String sanitizeText(String text) {
        if (text == null)
            return "";

        return text
                // normaliza quebras de linha
                .replace("\r\n", "\n")
                .replace("\r", "\n")

                // remove caracteres de controle invisíveis
                .replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "")

                // remove emojis e símbolos Unicode avançados (surrogate pairs)
                .replaceAll("[\\p{So}\\p{Cn}]", "")
                .replaceAll("[\\uD800-\\uDBFF][\\uDC00-\\uDFFF]", "")

                // remove caracteres de substituição
                .replace("�", "")

                .trim();
    }

}
