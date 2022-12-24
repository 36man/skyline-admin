package org.apache.skyline.admin.server.dal.dataobject;

/**
 * @author hejianbing
 * @version @Id: ApiDefinitionDO.java, v 0.1 2022年12月23日 11:00 hejianbing Exp $
 */
public class ApiDefinitionDO extends BaseDO{

    private Long id;

    private String api;

    private Long ver;

    private String name;

    private Long apiGroupId;

    private Long apiClusterId;

    private String args;
}