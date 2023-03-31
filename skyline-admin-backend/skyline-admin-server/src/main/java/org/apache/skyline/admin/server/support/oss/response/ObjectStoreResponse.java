package org.apache.skyline.admin.server.support.oss.response;

import lombok.Data;

/**
 * @author jojocodex
 * @version @Id: ObjectStoreResponse.java, v 0.1 2022年12月22日 09:52 jojocodex Exp $
 */
@Data
public class ObjectStoreResponse {

    private String bucketName;

    private String fileKey;

    private String resourceUrl;

    private String storePath;
}