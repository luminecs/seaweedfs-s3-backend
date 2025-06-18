package com.yourcompany.seaweedfs.dto;

import java.time.Instant;

public record BucketDto(String name, Instant creationDate) {
}
