package org.apache.skyline.admin.server.oss.config;

import org.apache.skyline.admin.server.oss.service.impl.AmazonS3Service;
import org.apache.skyline.admin.server.oss.service.impl.LocalStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

/**
 * @author jojocodex
 * @version @Id: OssConfiguration.java, v 0.1 2022年12月21日 20:51 jojocodex Exp $
 */

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OssProperties.class)
public class OssConfiguration {

    @Autowired
    private OssProperties ossProperties;

    @Bean
    @ConditionalOnProperty(value = "admin.oss.storeType", havingValue = "S3", matchIfMissing = false)
    public AmazonS3Service s3client() {
        ClientConfiguration clientConfiguration = new ClientConfiguration();

        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
                ossProperties.getEndpoint(), ossProperties.getRegion());

        AWSCredentials awsCredentials = new BasicAWSCredentials(ossProperties.getAccessKey(), ossProperties.getSecretKey());

        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);

        AmazonS3 s3Client = AmazonS3Client.builder()
                .withEndpointConfiguration(endpointConfiguration)
                .withClientConfiguration(clientConfiguration)
                .withCredentials(awsCredentialsProvider)
                .disableChunkedEncoding()
                .withPathStyleAccessEnabled(ossProperties.getPathStyleAccess())
                .build();

        AmazonS3Service amazonS3Service = new AmazonS3Service(ossProperties, s3Client);

        return amazonS3Service;
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalStorageService localService() {
        return new LocalStorageService(ossProperties);
    }
}