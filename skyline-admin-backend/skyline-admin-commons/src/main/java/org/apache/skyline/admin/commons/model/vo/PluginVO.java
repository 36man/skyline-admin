package org.apache.skyline.admin.commons.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author hejianbing
 * @version @Id: PluginVO.java, v 0.1 2022年12月27日 10:40 hejianbing Exp $
 */
@Data
public class PluginVO {

    private Long   id;

    private String maintainer;

    private String defineClass;

    public String  pluginName;

    public String  overview;

    private Date   createTime;

    private Date   updateTime;
}