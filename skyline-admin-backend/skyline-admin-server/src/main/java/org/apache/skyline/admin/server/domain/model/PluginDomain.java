package org.apache.skyline.admin.server.domain.model;

import lombok.Data;
import org.bravo.gaia.commons.base.BaseDomain;

import java.util.Date;

/**
 * @author hejianbing
 * @version @Id: PluginDomain.java, v 0.1 2022年12月23日 13:22 hejianbing Exp $
 */
@Data
public class PluginDomain extends BaseDomain {

    private Long         id;

    private String       maintainer;

    public String        pluginName;

    private String       defineClass;

    public String        overview;

    private Date         createTime;

    private Date         updateTime;

    private Boolean active;

}