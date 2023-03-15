package org.apache.skyline.admin.server.support.oss.service;

import org.apache.skyline.admin.server.support.oss.config.StoreType;
import org.apache.skyline.admin.server.support.oss.request.ObjectStoreRequest;
import org.apache.skyline.admin.server.support.oss.response.ObjectStoreResponse;

/**
 * @author hejianbing
 * @version @Id: OSSService.java, v 0.1 2022年12月22日 20:52 hejianbing Exp $
 */
public interface OSSService {

    StoreType storeType();

    ObjectStoreResponse store(ObjectStoreRequest request);

    byte[] getObject(String fileKey);

    void deleteObject(String fileKey);
}