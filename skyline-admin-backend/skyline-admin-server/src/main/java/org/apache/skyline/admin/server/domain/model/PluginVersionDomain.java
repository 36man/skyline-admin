package org.apache.skyline.admin.server.domain.model;

import lombok.Data;
import org.bravo.gaia.commons.base.BaseDomain;

import java.util.Date;
import java.util.List;

/**
 * @author hejianbing
 * @version @Id: PluginVersionDomain.java, v 0.1 2022年12月23日 13:24 hejianbing Exp $
 */
@Data
public class PluginVersionDomain extends BaseDomain {

    private Long id;

    private String ver;

    private List<String> features;

    private PluginDomain pluginDomain;

    private String pageContent;

    private Boolean deleted;

    private Boolean active;

    private Long size;

    private String jarUrl;

    private String fileKey;

    private String typeMeta;

    private Date createTime;

    private Date updateTime;
}