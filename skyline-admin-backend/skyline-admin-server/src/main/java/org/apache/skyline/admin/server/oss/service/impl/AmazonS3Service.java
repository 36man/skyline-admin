package org.apache.skyline.admin.server.oss.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.skyline.admin.commons.enums.SymbolKind;
import org.apache.skyline.admin.server.oss.builder.FileKeyBuilder;
import org.apache.skyline.admin.server.oss.config.OssProperties;
import org.apache.skyline.admin.server.oss.request.UploadMultipleFileRequest;
import org.apache.skyline.admin.server.oss.response.UploadMultipleFileResponse;
import org.apache.skyline.admin.server.oss.service.AbstractOssService;
import org.springframework.beans.factory.InitializingBean;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

/**
 * support Aliyun Huawei jd Tencent Minio
 * @author hejianbing
 * @version @Id: AmazonS3Service.java, v 0.1 2022年12月22日 16:22 hejianbing Exp $
 */
public class AmazonS3Service extends AbstractOssService implements InitializingBean {

    private OssProperties ossProperties;

    private AmazonS3 ossClient;

    private String bucketName;

    public AmazonS3Service(OssProperties ossProperties, AmazonS3 ossClient) {
        this.ossProperties = ossProperties;
        this.ossClient = ossClient;
    }

    @Override
    protected byte[] doGetObject(String fileKey) throws Exception {
        S3Object object = ossClient.getObject(bucketName, fileKey);

        InputStream is = object.getObjectContent();

        return IOUtils.toByteArray(is);
    }


    @Override
    protected UploadMultipleFileResponse doUpload(UploadMultipleFileRequest request) throws Exception {
        String fileKey = FileKeyBuilder.newBuilder(ossProperties.getGroup(), request.getFileName()).build();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(request.getSize());
        objectMetadata.setContentType(request.getContentType());

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileKey,
            new ByteArrayInputStream(request.getBytes()), objectMetadata);

        ossClient.putObject(putObjectRequest);

        UploadMultipleFileResponse result = new UploadMultipleFileResponse();
        result.setFileKey(fileKey);
        result.setBucketName(ossProperties.getBucketName());

        Date expiration = new Date(new Date().getTime() + 3600 * 1000);

        URL url = ossClient.generatePresignedUrl(bucketName, fileKey, expiration);

        String resourceUrl = url.toString();

        if (resourceUrl.indexOf(SymbolKind.QUESTION.getSymbol()) > 0) {
            resourceUrl = resourceUrl.substring(0,
                resourceUrl.indexOf(SymbolKind.QUESTION.getSymbol()));
        }

        if (StringUtils.isNoneBlank(ossProperties.getEndpoint())) {
            resourceUrl = ossProperties.getEndpoint() + fileKey;
        }

        result.setResourceUrl(resourceUrl);

        return result;
    }

    @Override
    protected void doDelete(String fileKey) throws Exception {
        ossClient.deleteObject(bucketName,fileKey);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String bucketName = ossProperties.getBucketName();
        if (!ossClient.doesBucketExist(bucketName)) {
            ossClient.createBucket(bucketName);
        }
        this.bucketName = bucketName;
    }
}