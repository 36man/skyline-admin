package org.apache.skyline.admin.server.oss.request;

import lombok.Data;

/**
 * @author jojocodex
 * @version @Id: UploadMultipleFileRequest.java, v 0.1 2022年12月22日 00:23 jojocodex Exp $
 */
@Data
public class UploadMultipleFileRequest {
    private String group;
    private String fileName;
    private byte[] bytes;

    private Long   size;
    private String contentType;

}