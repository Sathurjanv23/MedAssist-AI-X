package com.medassist.ocr.service;

import com.medassist.ocr.model.OcrModels;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * OCR Service â€” extracts text from medical documents.
 *
 * <p>Primary: Calls external Tesseract OCR microservice (Python/Flask).
 * <p>Fallback: Returns structured placeholder when OCR service is unavailable.
 *
 * <p>Supports multilingual (English, Tamil, Sinhala) for Sri Lankan healthcare context.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OcrService {

    @Qualifier("ocrWebClient")
    private final WebClient ocrWebClient;

    @Value("${ocr.service.url:http://localhost:5001}")
    private String ocrServiceUrl;

    @Value("${ocr.fallback.enabled:true}")
    private boolean fallbackEnabled;

    /**
     * Extract text from a PDF or image file asynchronously.
     */
    @Async("aiExecutor")
    public CompletableFuture<OcrModels.OcrResult> extractText(MultipartFile file, String language) {
        try {
            byte[] fileBytes = file.getBytes();
            String base64 = Base64.getEncoder().encodeToString(fileBytes);

            String docType = detectDocumentType(file.getOriginalFilename(),
                                                 file.getContentType());

            OcrModels.OcrRequest request = OcrModels.OcrRequest.builder()
                    .imageBase64(base64)
                    .language(mapLanguageCode(language))
                    .documentType(docType)
                    .build();

            OcrModels.OcrResult result = ocrWebClient.post()
                    .uri("/extract")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OcrModels.OcrResult.class)
                    .block();

            if (result != null && result.isSuccess()) {
                log.info("OCR completed with confidence: {}", result.getConfidence());
                return CompletableFuture.completedFuture(result);
            }

            return CompletableFuture.completedFuture(getFallbackResult(file.getOriginalFilename()));

        } catch (WebClientRequestException e) {
            log.warn("OCR service unavailable at {} â€” using fallback", ocrServiceUrl);
            return CompletableFuture.completedFuture(getFallbackResult(file.getOriginalFilename()));
        } catch (Exception e) {
            log.error("OCR extraction error: {}", e.getMessage());
            return CompletableFuture.completedFuture(getErrorResult(e.getMessage()));
        }
    }

    /**
     * Extract text from an S3 URL directly (for already-uploaded files).
     */
    @Async("aiExecutor")
    public CompletableFuture<OcrModels.OcrResult> extractTextFromUrl(String fileUrl, String language) {
        try {
            OcrModels.OcrRequest request = OcrModels.OcrRequest.builder()
                    .imageUrl(fileUrl)
                    .language(mapLanguageCode(language))
                    .build();

            OcrModels.OcrResult result = ocrWebClient.post()
                    .uri("/extract-url")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OcrModels.OcrResult.class)
                    .block();

            return CompletableFuture.completedFuture(
                    result != null ? result : getFallbackResult("document"));

        } catch (Exception e) {
            log.warn("OCR from URL failed: {} â€” {}", fileUrl, e.getMessage());
            return CompletableFuture.completedFuture(getFallbackResult("document"));
        }
    }

    // â”€â”€ Helpers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private String detectDocumentType(String fileName, String contentType) {
        if (fileName == null) return "GENERAL";
        String lower = fileName.toLowerCase();
        if (lower.contains("blood") || lower.contains("cbc") || lower.contains("lab")) return "BLOOD_TEST";
        if (lower.contains("prescription") || lower.contains("rx")) return "PRESCRIPTION";
        if (lower.contains("scan") || lower.contains("xray") || lower.contains("mri")) return "SCAN";
        return "GENERAL";
    }

    private String mapLanguageCode(String language) {
        if (language == null) return "eng";
        return switch (language) {
            case "ta" -> "tam";
            case "si" -> "sin";
            default -> "eng";
        };
    }

    private OcrModels.OcrResult getFallbackResult(String fileName) {
        return OcrModels.OcrResult.builder()
                .rawText("")
                .confidence(0.0)
                .entities(Map.of("status", "unavailable"))
                .warnings(List.of("OCR service was not available. Text extraction could not be completed."))
                .success(false)
                .error("OCR service unavailable")
                .build();
    }

    private OcrModels.OcrResult getErrorResult(String error) {
        return OcrModels.OcrResult.builder()
                .rawText("")
                .confidence(0.0)
                .success(false)
                .error(error)
                .build();
    }
}

