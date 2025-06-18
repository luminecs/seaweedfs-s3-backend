package com.yourcompany.seaweedfs.dto;

public record PresignPartRequest(String bucketName, String key, String uploadId, int partNumber) {}

