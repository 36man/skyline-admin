package org.apache.skyline.admin.server.oss.service.impl;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.skyline.admin.commons.exception.SkylineAdminErrorCode;
import org.apache.skyline.admin.server.oss.builder.FileKeyBuilder;
import org.apache.skyline.admin.server.oss.config.OssProperties;
import org.apache.skyline.admin.server.oss.request.UploadMultipleFileRequest;
import org.apache.skyline.admin.server.oss.response.UploadMultipleFileResponse;
import org.apache.skyline.admin.server.oss.service.AbstractOssService;
import org.aspectj.util.FileUtil;
import org.bravo.gaia.commons.exception.PlatformException;
import org.bravo.gaia.commons.util.AssertUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.FileCopyUtils;

/**
 * @author hejianbing
 * @version @Id: LocalStorageService.java, v 0.1 2022年12月22日 20:53 hejianbing Exp $
 */
public class LocalStorageService extends AbstractOssService implements InitializingBean {

    private OssProperties ossProperties;
    private File          storeLocation;

    public LocalStorageService(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    protected byte[] doGetObject(String fileKey) throws Exception {
        return FileUtil.readAsByteArray(getIfExists(fileKey));
    }

    @Override
    protected UploadMultipleFileResponse doUpload(UploadMultipleFileRequest request) throws Exception {
        FileKeyBuilder keyBuilder = FileKeyBuilder.newBuilder(request.getGroup(), request.getFileName());

        String fileKey = keyBuilder.build();

        File file = new File(storeLocation.getAbsoluteFile(), fileKey);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileCopyUtils.copy(request.getBytes(), file);

        String accessUrl = ossProperties.getEndpoint()+fileKey;

        UploadMultipleFileResponse result = new UploadMultipleFileResponse();

        result.setFileKey(fileKey);
        result.setResourceUrl(accessUrl);
        result.setBucketName(this.ossProperties.getBucketName());

        return result;
    }

    @Override
    public void doDelete(String fileKey) throws Exception {
        FileUtils.delete(getIfExists(fileKey));
    }

    protected File getIfExists(String fileKey) {
        File file = new File(storeLocation,fileKey);

        if (!file.exists()) {
            throw new PlatformException(SkylineAdminErrorCode.OSS_OBJECT_NOT_FOUND.getCode());
        }
        return file;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        AssertUtil.isNotBlank(this.ossProperties.getEndpoint(), "oss endpoint is null");

        String location = this.ossProperties.getBucketName();

        if (!StringUtils.isNotBlank(location)) {
            location = System.getProperty("user.dir");
        }
        File storePath = new File(location);

        if (!storePath.exists()) {
            storePath.mkdirs();
        }
        this.storeLocation = storePath.getAbsoluteFile();
    }
}