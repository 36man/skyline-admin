/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.skyline.admin.server.domain.model;

import lombok.Data;
import lombok.Getter;
import org.apache.skyline.admin.commons.enums.ClusterStatus;
import org.bravo.gaia.commons.base.BaseDomain;

import java.util.Date;
import java.util.Map;

@Data
public class ClusterDomain extends BaseDomain {

    private Long id;

    private String clusterName;

    private String domain;

    private String bizKey;

    private Integer instanceCount;

    private Boolean configShare;

    private String configUrl;

    private String configUser;

    private String configSecret;

    private Map<String,Object> configItem;

    private String useQuota;

    private String userNo;

    private String userName;

    private String meno;

    private Date createTime;

    private Date updateTime;

    private ClusterStatus status;
}
