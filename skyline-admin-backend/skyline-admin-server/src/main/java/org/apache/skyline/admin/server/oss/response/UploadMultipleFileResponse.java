package org.apache.skyline.admin.server.oss.response;

import lombok.Data;

/**
 * @author jojocodex
 * @version @Id: UploadMultipleFileResponse.java, v 0.1 2022年12月22日 09:52 jojocodex Exp $
 */
@Data
public class UploadMultipleFileResponse {

    private String bucketName;

    private String fileKey;

    private String resourceUrl;

}