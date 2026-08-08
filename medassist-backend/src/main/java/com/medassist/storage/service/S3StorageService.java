package com.medassist.storage.service;

import com.medassist.common.constants.AppConstants;
import com.medassist.common.exception.FileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;

/**
 * AWS S3 storage service â€” handles medical report and profile image uploads.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class S3StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    // â”€â”€ Upload â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public String uploadReport(String userId, MultipartFile file) {
        validateFile(file, AppConstants.ALLOWED_REPORT_TYPES);
        String key = AppConstants.S3_REPORTS_PREFIX + userId + "/" + generateFileName(file);
        return uploadToS3(file, key, false);
    }

    public String uploadProfileImage(String userId, MultipartFile file) {
        validateFile(file, AppConstants.ALLOWED_IMAGE_TYPES);
        String key = AppConstants.S3_PROFILES_PREFIX + userId + "/profile." +
                     FilenameUtils.getExtension(file.getOriginalFilename());
        return uploadToS3(file, key, true);
    }

    private String uploadToS3(MultipartFile file, String key, boolean publicRead) {
        try {
            PutObjectRequest.Builder builder = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize());

            if (publicRead) {
                builder.acl(ObjectCannedACL.PUBLIC_READ);
            }

            s3Client.putObject(builder.build(),
                    RequestBody.fromBytes(file.getBytes()));

            String url = publicRead
                    ? "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key
                    : generateSignedUrl(key);

            log.info("Uploaded file to S3: {}", key);
            return url;

        } catch (IOException e) {
            throw new FileException("Failed to upload file: " + e.getMessage(), e);
        } catch (S3Exception e) {
            throw new FileException("S3 upload error: " + e.getMessage(), e);
        }
    }

    // â”€â”€ Signed URL â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public String generateSignedUrl(String key) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(2))
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build())
                .build();
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    // â”€â”€ Delete â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public void deleteFile(String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            log.info("Deleted file from S3: {}", key);
        } catch (S3Exception e) {
            log.error("Failed to delete from S3: {}", e.getMessage());
        }
    }

    // â”€â”€ Validation â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private void validateFile(MultipartFile file, String[] allowedTypes) {
        if (file == null || file.isEmpty()) {
            throw new FileException("File is empty or null");
        }
        if (file.getSize() > AppConstants.MAX_FILE_SIZE_BYTES) {
            throw new FileException("File size exceeds the 20MB limit");
        }
        String contentType = file.getContentType();
        boolean allowed = Arrays.asList(allowedTypes).contains(contentType);
        if (!allowed) {
            throw new FileException("File type '" + contentType + "' is not allowed");
        }
    }

    private String generateFileName(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        return UUID.randomUUID() + "." + extension;
    }
}

