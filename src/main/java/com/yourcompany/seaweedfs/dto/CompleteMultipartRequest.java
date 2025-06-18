package com.yourcompany.seaweedfs.dto;

import java.util.List;

public record CompleteMultipartRequest(String bucketName, String key, String uploadId, List<PartInfo> parts) {}

