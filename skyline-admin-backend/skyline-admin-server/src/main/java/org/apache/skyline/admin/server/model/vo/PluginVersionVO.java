package org.apache.skyline.admin.server.model.vo;

import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * @author hejianbing
 * @version @Id: PluginVersionVO.java, v 0.1 2023年01月05日 14:51 hejianbing Exp $
 */
@Data
public class PluginVersionVO {

    private Long            id;

    private String          ver;

    private List<String>    features;

    private SkylinePluginVO skylinePluginVO;

    private String          pageContent;

    private boolean         deleted;

    private boolean         active;

    private Long            size;

    private String          md5;

    private String          jarUrl;

    private String          fileKey;

    private List<String>    verTags;

    private Date            createTime;

    private Date            updateTime;

}