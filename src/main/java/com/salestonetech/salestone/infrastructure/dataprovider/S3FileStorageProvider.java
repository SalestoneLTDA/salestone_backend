package com.salestonetech.salestone.infrastructure.dataprovider;

import com.salestonetech.salestone.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;

@Component
public class S3FileStorageProvider implements FileStorageService {

    private final S3Client s3Client;
    private final String bucketName;

    public S3FileStorageProvider(@Value("${aws.accessKeyId}") String accessKey,
                                 @Value("${aws.secretAccessKey}") String secretKey,
                                 @Value("${aws.region}") String region,
                                 @Value("${aws.s3.bucket-name}") String bucketName) {
        this.bucketName = bucketName;
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    @Override
    public InputStream getFileContent(String fileKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        return s3Client.getObject(getObjectRequest);
    }
}
