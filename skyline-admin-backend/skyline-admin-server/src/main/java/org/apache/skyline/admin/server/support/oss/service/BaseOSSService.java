package org.apache.skyline.admin.server.support.oss.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.skyline.admin.commons.exception.AdminErrorCode;
import org.apache.skyline.admin.server.support.oss.request.ObjectStoreRequest;
import org.apache.skyline.admin.server.support.oss.response.ObjectStoreResponse;
import org.bravo.gaia.commons.exception.PlatformException;
import org.bravo.gaia.commons.util.AssertUtil;

/**
 * @author hejianbing
 * @version @Id: BaseOSSService.java, v 0.1 2022年12月22日 21:41 hejianbing Exp $
 */
@Slf4j
public abstract class BaseOSSService implements OSSService {
    @Override
    public byte[] getObject(String fileKey) {
        AssertUtil.isNotBlank(fileKey, "fileKey is null");
        return this.execute(() -> doGetObject(fileKey));
    }

    @Override
    public ObjectStoreResponse store(ObjectStoreRequest request) {
        AssertUtil.notNull(request, "ObjectStoreRequest is null");

        AssertUtil.isNotBlank(request.getFileName(),
            "ObjectStoreRequest fileName is null");

        AssertUtil.notNull(request.getBytes(), "ObjectStoreRequest bytes is null");

        AssertUtil.isTrue(request.getBytes().length > 0,
            "ObjectStoreRequest bytes is empty");

        return this.execute(() -> doUpload(request));
    }

    public void deleteObject(String fileKey) {
        AssertUtil.isNotBlank(fileKey, "fileKey is null");

        this.execute(() -> {
            doDelete(fileKey);
            return null;
        });
    }

    protected abstract byte[] doGetObject(String fileKey) throws Exception;

    protected abstract ObjectStoreResponse doUpload(ObjectStoreRequest request) throws Exception;

    protected abstract void doDelete(String fileKey) throws Exception;

    protected <T> T execute(Action<T> action) {
        try {
            T result = action.doAction();

            return result;
        } catch (Exception exception) {

            log.error("oss store error {}", ExceptionUtils.getRootCauseMessage(exception));

            if (exception instanceof PlatformException) {
                throw (PlatformException) exception;
            }
            throw new PlatformException(AdminErrorCode.OSS_ERROR.getCode());
        }
    }

    public interface Action<T> {
        T doAction() throws Exception;
    }
}