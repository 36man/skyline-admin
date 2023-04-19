package org.apache.skyline.admin.commons.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author hejianbing
 * @version @Id: PluginVersionVO.java, v 0.1 2023年01月05日 14:51 hejianbing Exp $
 */
@Data
public class PluginVersionVO {

    private Long id;

    private String ver;

    private List<String> features;

    private PluginVO pluginVO;

    private String pageContent;

    private String typeMeta;

    private boolean active;

    private Long size;

    private String jarUrl;

    private String fileKey;

    private Date createTime;

    private Date updateTime;

}