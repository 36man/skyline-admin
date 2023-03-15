package org.apache.skyline.admin.server.support.oss.service.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.skyline.admin.commons.exception.AdminErrorCode;
import org.apache.skyline.admin.server.support.oss.builder.FileKeyBuilder;
import org.apache.skyline.admin.server.support.oss.config.OSSProperties;
import org.apache.skyline.admin.server.support.oss.config.StoreType;
import org.apache.skyline.admin.server.support.oss.request.ObjectStoreRequest;
import org.apache.skyline.admin.server.support.oss.response.ObjectStoreResponse;
import org.apache.skyline.admin.server.support.oss.service.BaseOSSService;
import org.aspectj.util.FileUtil;
import org.bravo.gaia.commons.exception.PlatformException;
import org.bravo.gaia.commons.util.AssertUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.File;

/**
 * @author hejianbing
 * @version @Id: LocalStorageService.java, v 0.1 2022年12月22日 20:53 hejianbing Exp $
 */
@Component
public class LocalStorageService extends BaseOSSService implements InitializingBean {

    private OSSProperties ossProperties;
    private File          storeLocation;

    public LocalStorageService(OSSProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    protected byte[] doGetObject(String fileKey) throws Exception {
        return FileUtil.readAsByteArray(getIfExists(fileKey));
    }

    @Override
    protected ObjectStoreResponse doUpload(ObjectStoreRequest request) throws Exception {
        FileKeyBuilder keyBuilder = FileKeyBuilder.newBuilder(ossProperties.getGroup(), request.getFileName());

        String fileKey = keyBuilder.build();

        File file = new File(storeLocation.getAbsoluteFile(), fileKey);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileCopyUtils.copy(request.getBytes(), file);

        String accessUrl = ossProperties.getEndpoint()+fileKey;

        ObjectStoreResponse result = new ObjectStoreResponse();

        result.setFileKey(fileKey);
        result.setResourceUrl(accessUrl);
        result.setBucketName(this.ossProperties.getStorePath());

        return result;
    }

    @Override
    public void doDelete(String fileKey) throws Exception {
        FileUtils.delete(getIfExists(fileKey));
    }

    protected File getIfExists(String fileKey) {
        File file = new File(storeLocation,fileKey);

        if (!file.exists()) {
            throw new PlatformException(AdminErrorCode.OSS_OBJECT_NOT_FOUND.getCode());
        }
        return file;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        AssertUtil.isNotBlank(this.ossProperties.getEndpoint(), "oss endpoint is null");

        String location = this.ossProperties.getStorePath();

        if (!StringUtils.isNotBlank(location)) {
            location = System.getProperty("user.dir");
        }
        File storePath = new File(location);

        if (!storePath.exists()) {
            storePath.mkdirs();
        }
        this.storeLocation = storePath.getAbsoluteFile();
    }

    @Override
    public StoreType storeType() {
        return StoreType.LOCAL;
    }
}