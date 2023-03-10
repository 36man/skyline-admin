package org.apache.skyline.admin.server.oss.service;

import org.apache.skyline.admin.server.oss.request.ObjectStoreRequest;
import org.apache.skyline.admin.server.oss.response.ObjectStoreResponse;

/**
 * @author hejianbing
 * @version @Id: OssService.java, v 0.1 2022年12月22日 20:52 hejianbing Exp $
 */
public interface OssService {

    ObjectStoreResponse store(ObjectStoreRequest request);

    byte[] getObject(String fileKey);

    void deleteObject(String fileKey);
}