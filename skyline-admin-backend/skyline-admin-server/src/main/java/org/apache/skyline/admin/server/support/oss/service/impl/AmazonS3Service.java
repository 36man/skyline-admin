package org.apache.skyline.admin.server.support.oss.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.skyline.admin.commons.enums.SymbolKind;
import org.apache.skyline.admin.server.support.oss.builder.FileKeyBuilder;
import org.apache.skyline.admin.server.support.oss.config.OSSProperties;
import org.apache.skyline.admin.server.support.oss.config.StoreType;
import org.apache.skyline.admin.server.support.oss.request.ObjectStoreRequest;
import org.apache.skyline.admin.server.support.oss.response.ObjectStoreResponse;
import org.apache.skyline.admin.server.support.oss.service.BaseOSSService;
import org.springframework.beans.factory.InitializingBean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * support Aliyun Huawei jd Tencent Minio
 * @author hejianbing
 * @version @Id: AmazonS3Service.java, v 0.1 2022年12月22日 16:22 hejianbing Exp $
 */
public class AmazonS3Service extends BaseOSSService implements InitializingBean {

    private OSSProperties ossProperties;

    private AmazonS3 ossClient;

    private String bucketName;

    public AmazonS3Service(OSSProperties ossProperties, AmazonS3 ossClient) {
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
    protected ObjectStoreResponse doUpload(ObjectStoreRequest request) throws Exception {
        String fileKey = FileKeyBuilder.newBuilder(ossProperties.getGroup(), request.getFileName()).build();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(request.getSize());
        objectMetadata.setContentType(request.getContentType());

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileKey,
            new ByteArrayInputStream(request.getBytes()), objectMetadata);

        ossClient.putObject(putObjectRequest);

        ObjectStoreResponse result = new ObjectStoreResponse();
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

        result.setStorePath(bucketName + SymbolKind.SLASH.getSymbol() + fileKey);

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

    @Override
    public StoreType storeType() {
        return StoreType.S3;
    }
}