package org.apache.skyline.admin.server.support.oss.request;

import lombok.Data;

/**
 * @author jojocodex
 * @version @Id: ObjectStoreRequest.java, v 0.1 2022年12月22日 00:23 jojocodex Exp $
 */
@Data
public class ObjectStoreRequest {

    private String fileName;

    private byte[] bytes;

    private Long   size;

    private String contentType;

}