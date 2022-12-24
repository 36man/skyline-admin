package org.apache.skyline.admin.server.dal.dataobject;

import lombok.Data;

/**
 * @author hejianbing
 * @version @Id: ApiClusterDO.java, v 0.1 2022年12月23日 11:08 hejianbing Exp $
 */
@Data
public class ApiClusterDO extends BaseDO{

    private Long id;

    private String name;

    private String domain;

    private Integer port;

    private String dbUrl;

    private String redisUrl;

    private String nacosUrl;
}