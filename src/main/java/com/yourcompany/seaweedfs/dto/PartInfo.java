package com.yourcompany.seaweedfs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PartInfo(@JsonProperty("PartNumber") int PartNumber,
                       @JsonProperty("ETag") String ETag
) {
}

