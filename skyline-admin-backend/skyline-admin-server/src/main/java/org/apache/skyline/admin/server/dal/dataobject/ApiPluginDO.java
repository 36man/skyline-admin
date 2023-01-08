package org.apache.skyline.admin.server.dal.dataobject;

import lombok.Data;

/**
 * @author hejianbing
 * @version @Id: ApiPluginDO.java, v 0.1 2022年12月23日 11:06 hejianbing Exp $
 */
@Data
public class ApiPluginDO extends BaseDO {

    private Long   id;

    private String pluginName;

    private Long   verId;

    private Long   apiDefinitionId;

    private String configItems;

    private long   sn;

}