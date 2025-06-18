package com.yourcompany.seaweedfs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// 这个 DTO 只包含 Uppy 需要的字段
public record PartDto(
        @JsonProperty("PartNumber") Integer partNumber,
        @JsonProperty("ETag") String eTag,
        @JsonProperty("Size") Long size
) {
}
