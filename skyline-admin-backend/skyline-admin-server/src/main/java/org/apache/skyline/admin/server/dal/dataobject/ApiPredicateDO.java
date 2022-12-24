package org.apache.skyline.admin.server.dal.dataobject;

import lombok.Data;

/**
 * @author hejianbing
 * @version @Id: ApiPredicateDO.java, v 0.1 2022年12月23日 11:11 hejianbing Exp $
 */
@Data
public class ApiPredicateDO extends BaseDO{

    private String id;

    private String config;

    private String name;
}