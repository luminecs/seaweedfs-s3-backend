package com.yourcompany.seaweedfs.dto;

public record AbortMultipartRequest(String bucketName, String key, String uploadId) {
}
