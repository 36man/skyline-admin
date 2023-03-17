package org.apache.skyline.admin.server.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author hejianbing
 * @version @Id: PluginVersionDO.java, v 0.1 2022年12月23日 10:57 hejianbing Exp $
 */
@TableName("admin_plugin_version")
@Data
public class PluginVersionDO extends BaseDO {

    private Long    id;

    private String  ver;

    private String  features;

    private Long  pluginId;

    private String  pageContent;

    private Boolean deleted;

    private Boolean active;

    private Long    size;

    private String  jarUrl;

    private String  fileKey;

    private String apiDefine;

}