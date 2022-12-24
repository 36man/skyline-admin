package org.apache.skyline.admin.server.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author hejianbing
 * @version @Id: SkylinePluginVO.java, v 0.1 2022年12月27日 10:40 hejianbing Exp $
 */
@Data
public class SkylinePluginVO {

    private Long   id;

    private String maintainer;

    private String defineClass;

    public String  pluginName;

    public String  overview;

    private String keywords;

    private Date   createTime;

    private Date   updateTime;
}