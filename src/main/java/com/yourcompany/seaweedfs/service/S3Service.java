package com.yourcompany.seaweedfs.service;

import com.yourcompany.seaweedfs.dto.*;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final Duration presignDuration = Duration.ofSeconds(900);

    public S3Service(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    // Bucket Operations
    public List<BucketDto> listBuckets() { // <-- 返回类型修改为 List<BucketDto>
        return s3Client.listBuckets().buckets().stream()
                .map(bucket -> new BucketDto(bucket.name(), bucket.creationDate())) // <-- 进行转换
                .collect(Collectors.toList());
    }

    public void createBucket(String bucketName) {
        s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
    }

    public void deleteBucket(String bucketName) {
        s3Client.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build());
    }

    // File Operations
    public List<S3ObjectDto> listFiles(String bucketName) { // <-- 返回类型修改为 List<S3ObjectDto>
        return s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucketName).build()).contents().stream()
                .map(s3Object -> new S3ObjectDto( // <-- 进行转换
                        s3Object.key(),
                        s3Object.size(),
                        s3Object.lastModified(),
                        s3Object.eTag(),
                        s3Object.storageClassAsString()
                ))
                .collect(Collectors.toList());
    }

    public void deleteFile(String bucketName, String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(key).build());
    }

    // Presigned URL for Download/Preview
    public String getPresignedGetObjectUrl(com.yourcompany.seaweedfs.dto.GetObjectRequest request) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(request.bucketName())
                .key(request.key())
                .build();
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(presignDuration)
                .getObjectRequest(getRequest)
                .build();
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    // Presigned URL for small file upload
    public String getPresignedPutObjectUrl(com.yourcompany.seaweedfs.dto.PutObjectRequest request) {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(request.bucketName())
                .key(request.fileName())
                .build();
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(presignDuration)
                .putObjectRequest(putRequest)
                .build();
        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }

    // Multipart Upload Operations
    public InitiateMultipartResponse initiateMultipartUpload(InitiateMultipartRequest request) {
        CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
                .bucket(request.bucketName())
                .key(request.fileName())
                .build();
        CreateMultipartUploadResponse response = s3Client.createMultipartUpload(createRequest);
        return new InitiateMultipartResponse(response.uploadId(), response.key());
    }

    public String presignPartUpload(PresignPartRequest request) {
        UploadPartRequest uploadRequest = UploadPartRequest.builder()
                .bucket(request.bucketName())
                .key(request.key())
                .uploadId(request.uploadId())
                .partNumber(request.partNumber())
                .build();
        UploadPartPresignRequest presignRequest = UploadPartPresignRequest.builder()
                .signatureDuration(presignDuration)
                .uploadPartRequest(uploadRequest)
                .build();
        return s3Presigner.presignUploadPart(presignRequest).url().toString();
    }

    public void completeMultipartUpload(CompleteMultipartRequest request) {
        List<CompletedPart> completedParts = request.parts().stream()
                .map(p -> CompletedPart.builder().partNumber(p.PartNumber()).eTag(p.ETag()).build())
                .collect(Collectors.toList());

        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                .parts(completedParts)
                .build();

        CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                .bucket(request.bucketName())
                .key(request.key())
                .uploadId(request.uploadId())
                .multipartUpload(completedMultipartUpload)
                .build();
        s3Client.completeMultipartUpload(completeRequest);
    }

    public void abortMultipartUpload(String bucketName, String key, String uploadId) {
        AbortMultipartUploadRequest abortRequest = AbortMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(key)
                .uploadId(uploadId)
                .build();
        s3Client.abortMultipartUpload(abortRequest);
    }
}
