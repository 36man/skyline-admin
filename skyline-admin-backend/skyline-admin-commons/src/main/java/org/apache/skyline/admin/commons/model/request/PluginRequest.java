package org.apache.skyline.admin.commons.model.request;

import lombok.Data;

/**
 * @author hejianbing
 * @version @Id: PluginRequest.java, v 0.1 2022年12月23日 13:48 hejianbing Exp $
 */
@Data
public class PluginRequest {

    private byte[] bytes;

    private String fileName;

    private Long   size;

    private String contentType;
}