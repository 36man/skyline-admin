package org.apache.skyline.admin.server.domain.model;

import lombok.Data;
import org.bravo.gaia.commons.base.BaseDomain;

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

    private boolean deleted;

    private boolean active;

    private Long size;

    private String md5;

    private String jarUrl;

    private String fileKey;

    private List<String> verTags;

    private String switchItems;
}