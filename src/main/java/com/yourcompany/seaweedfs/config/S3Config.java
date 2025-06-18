package com.yourcompany.seaweedfs.config;

import com.fasterxml.jackson.databind.ObjectMapper;
// 正常导入我们自己定义的 record
import com.yourcompany.seaweedfs.model.Credential;
import com.yourcompany.seaweedfs.model.S3Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.File;
import java.io.IOException;
import java.net.URI;

@Configuration
public class S3Config {

    @Value("${s3.config.path}")
    private String s3ConfigPath;

    @Value("${s3.endpoint}")
    private String s3Endpoint;

    @Value("${s3.region}")
    private String s3Region;

    @Bean
    public S3Client s3Client() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        // 这里的 S3Configuration 是我们自己定义的 record (com.yourcompany.seaweedfs.model.S3Configuration)
        S3Configuration config = mapper.readValue(new File(s3ConfigPath), S3Configuration.class);

        Credential adminCredentials = config.identities().stream()
                .filter(id -> "some_admin_user".equals(id.name()))
                .flatMap(id -> id.credentials().stream())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Admin user 'some_admin_user' not found in s3-config.json"));

        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                adminCredentials.accessKey(),
                adminCredentials.secretKey()
        );

        // 使用完全限定名来引用 AWS SDK 的 S3Configuration 类
        software.amazon.awssdk.services.s3.S3Configuration serviceConfiguration = software.amazon.awssdk.services.s3.S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();

        return S3Client.builder()
                .region(Region.of(s3Region))
                .endpointOverride(URI.create(s3Endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(serviceConfiguration)
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() throws IOException { // s3Presigner 也可能需要读取文件
        ObjectMapper mapper = new ObjectMapper();
        S3Configuration config = mapper.readValue(new File(s3ConfigPath), S3Configuration.class);

        Credential adminCredentials = config.identities().stream()
                .filter(id -> "some_admin_user".equals(id.name()))
                .flatMap(id -> id.credentials().stream())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Admin user 'some_admin_user' not found in s3-config.json"));

        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                adminCredentials.accessKey(),
                adminCredentials.secretKey()
        );

        // 再次使用完全限定名来为 S3Presigner 创建配置
        software.amazon.awssdk.services.s3.S3Configuration presignerServiceConfiguration = software.amazon.awssdk.services.s3.S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();

        return S3Presigner.builder()
                .region(Region.of(s3Region))
                .endpointOverride(URI.create(s3Endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(presignerServiceConfiguration)
                .build();
    }
}
