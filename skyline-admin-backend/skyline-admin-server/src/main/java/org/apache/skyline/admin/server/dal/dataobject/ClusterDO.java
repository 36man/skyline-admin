package org.apache.skyline.admin.server.dal.dataobject;

import lombok.Data;

/**
 * @author hejianbing
 * @version @Id: ClusterDO.java, v 0.1 2022年12月23日 11:08 hejianbing Exp $
 */
@Data
public class ClusterDO extends BaseDO {

    private Long id;

    private String clusterName;

    private String domain;

    private String businessName;

    private Integer instanceCount;

    private String configShare;

    private String configUrl;

    private String configUser;

    private String configSecret;

    private String useQuota;

    private String configPassword;

    private String meno;



}