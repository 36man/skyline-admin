package org.apache.skyline.admin.server.oss.service;

import org.apache.skyline.admin.server.oss.request.UploadMultipleFileRequest;
import org.apache.skyline.admin.server.oss.response.UploadMultipleFileResponse;

/**
 * @author hejianbing
 * @version @Id: OssService.java, v 0.1 2022年12月22日 20:52 hejianbing Exp $
 */
public interface OssService {

    UploadMultipleFileResponse upload(UploadMultipleFileRequest request);

    byte[] getObject(String fileKey);

    void deleteObject(String fileKey);
}