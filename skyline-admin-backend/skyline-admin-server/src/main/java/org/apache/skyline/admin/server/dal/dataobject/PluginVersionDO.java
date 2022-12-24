package org.apache.skyline.admin.server.dal.dataobject;

import lombok.Data;

/**
 * @author hejianbing
 * @version @Id: PluginVersionDO.java, v 0.1 2022年12月23日 10:57 hejianbing Exp $
 */
@Data
public class PluginVersionDO extends BaseDO {

    private Long    id;

    private String  ver;

    private String  features;

    private Long  pluginId;

    private String  pageContent;

    private boolean deleted;

    private boolean active;

    private Long    size;

    private String  md5;

    private String  jarUrl;

    private String  fileKey;

    private String  verTags;

}