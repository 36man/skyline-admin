package org.apache.skyline.admin.server.model.request;

import lombok.Data;

/**
 * @author hejianbing
 * @version @Id: GenerateSkylinePluginRequest.java, v 0.1 2022年12月23日 13:48 hejianbing Exp $
 */
@Data
public class GenerateSkylinePluginRequest {

    private byte[] bytes;

    private String fileName;

    private Long   size;

    private String contentType;
}