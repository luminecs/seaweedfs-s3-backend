package com.yourcompany.seaweedfs.controller;

import com.yourcompany.seaweedfs.dto.BucketDto;
import com.yourcompany.seaweedfs.dto.CreateBucketRequest;
import com.yourcompany.seaweedfs.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.Bucket;

import java.util.List;

@RestController
@RequestMapping("/api/buckets")
public class BucketController {

    private final S3Service s3Service;

    public BucketController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping
    public List<BucketDto> listBuckets() {
        return s3Service.listBuckets();
    }

    @PostMapping
    public ResponseEntity<Void> createBucket(@RequestBody CreateBucketRequest request) {
        s3Service.createBucket(request.bucketName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{bucketName}")
    public ResponseEntity<Void> deleteBucket(@PathVariable String bucketName) {
        s3Service.deleteBucket(bucketName);
        return ResponseEntity.ok().build();
    }
}
