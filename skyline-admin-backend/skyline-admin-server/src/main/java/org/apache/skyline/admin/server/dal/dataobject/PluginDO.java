package org.apache.skyline.admin.server.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author hejianbing
 * @version @Id: PluginDO.java, v 0.1 2022年12月23日 10:46 hejianbing Exp $
 */
@Data
@TableName("admin_plugin")
public class PluginDO extends BaseDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long   id;

    private String maintainer;

    private String classDefine;

    public String  pluginName;

    public String  overview;

    private Boolean active;

}