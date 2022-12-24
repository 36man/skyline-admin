package org.apache.skyline.admin.server.dal.dataobject;

import lombok.Data;

/**
 * @author hejianbing
 * @version @Id: ApiGroupDO.java, v 0.1 2022年12月23日 11:09 hejianbing Exp $
 */
@Data
public class ApiGroupDO extends BaseDO{

    private String id;

    private String groupName;

    private String memo;
}