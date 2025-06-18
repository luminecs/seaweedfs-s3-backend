package com.yourcompany.seaweedfs.dto;

import java.time.Instant;

public record S3ObjectDto(String key, Long size, Instant lastModified, String eTag, String storageClass) {
}
