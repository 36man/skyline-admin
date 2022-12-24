package org.apache.skyline.admin.server.dal.dataobject;

import lombok.Data;

import java.util.Date;

/**
 * @author hejianbing
 * @version @Id: BaseDO.java, v 0.1 2022年12月23日 10:47 hejianbing Exp $
 */
@Data
public class BaseDO {

    private Date updateTime;

    private Date createTime;
}