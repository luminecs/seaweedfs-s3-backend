package com.yourcompany.seaweedfs.controller;

import com.yourcompany.seaweedfs.dto.*;
import com.yourcompany.seaweedfs.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.Part;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    // File list/delete
    @GetMapping("/buckets/{bucketName}/files")
    public List<S3ObjectDto> listFiles(@PathVariable String bucketName) {
        return s3Service.listFiles(bucketName);
    }

    @DeleteMapping("/buckets/{bucketName}/files/{key}")
    public ResponseEntity<Void> deleteFile(@PathVariable String bucketName, @PathVariable String key) {
        s3Service.deleteFile(bucketName, key);
        return ResponseEntity.ok().build();
    }

    // Presigned URLs for GET and single PUT
    @PostMapping("/presigned-url/get-object")
    public PresignUrlResponse getObjectUrl(@RequestBody GetObjectRequest request) {
        return new PresignUrlResponse(s3Service.getPresignedGetObjectUrl(request));
    }

    @PostMapping("/presigned-url/put-object")
    public PresignUrlResponse putObjectUrl(@RequestBody PutObjectRequest request) {
        return new PresignUrlResponse(s3Service.getPresignedPutObjectUrl(request));
    }

    // Multipart Upload URLs
    @PostMapping("/initiate-multipart")
    public InitiateMultipartResponse initiateMultipart(@RequestBody InitiateMultipartRequest request) {
        return s3Service.initiateMultipartUpload(request);
    }

    @PostMapping("/presigned-url/part")
    public PresignUrlResponse presignPart(@RequestBody PresignPartRequest request) {
        return new PresignUrlResponse(s3Service.presignPartUpload(request));
    }

    @PostMapping("/complete-multipart")
    public ResponseEntity<Void> completeMultipart(@RequestBody CompleteMultipartRequest request) {
        s3Service.completeMultipartUpload(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/abort-multipart")
    public ResponseEntity<Void> abortMultipart(@RequestBody AbortMultipartRequest request) {
        s3Service.abortMultipartUpload(request.bucketName(), request.key(), request.uploadId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list-parts")
    public List<PartDto> listParts(@RequestParam String bucketName, @RequestParam String key, @RequestParam String uploadId) {
        return s3Service.listParts(bucketName, key, uploadId);
    }
}
