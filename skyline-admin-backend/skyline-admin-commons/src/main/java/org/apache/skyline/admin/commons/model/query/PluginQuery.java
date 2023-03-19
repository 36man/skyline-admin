package org.apache.skyline.admin.commons.model.query;

import lombok.Data;

/**
 * @author hejianbing
 * @version @Id: PluginQuery.java, v 0.1 2022年12月27日 09:48 hejianbing Exp $
 */
@Data
public class PluginQuery {

    private String classDefine;

    private String pluginName;

    private String maintainer;

}