package org.apache.skyline.admin.server.dal.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author hejianbing
 * @version @Id: ClusterDO.java, v 0.1 2022年12月23日 11:08 hejianbing Exp $
 */
@Data
@TableName("admin_cluster")
public class ClusterDO extends BaseDO {

    private Long id;

    private String clusterName;

    private String domain;

    private String bizKey;

    private Integer instanceCount;

    private Boolean configShare;

    private String configUrl;

    private String configUser;

    private String configSecret;

    private String configItem;

    private String useQuota;

    private String meno;

}