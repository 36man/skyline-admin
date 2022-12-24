package org.apache.skyline.admin.server.domain.entities;

import java.util.Date;
import java.util.List;

import lombok.Data;

import org.bravo.gaia.commons.base.BaseDomain;

/**
 * @author hejianbing
 * @version @Id: SkylinePluginDomain.java, v 0.1 2022年12月23日 13:22 hejianbing Exp $
 */
@Data
public class SkylinePluginDomain extends BaseDomain {

    private Long         id;

    private String       maintainer;

    public String        pluginName;

    private String       defineClass;

    public String        overview;

    private List<String> keywords;

    private Date         createTime;

    private Date         updateTime;

    private boolean      deleted;
}