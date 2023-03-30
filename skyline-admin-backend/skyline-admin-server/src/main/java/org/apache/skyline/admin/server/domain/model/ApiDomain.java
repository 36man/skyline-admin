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
import org.apache.skyline.admin.commons.enums.ApiStatus;
import org.bravo.gaia.commons.base.BaseDomain;

import java.util.Date;

@Data
public class ApiDomain extends BaseDomain {

    private Long id;

    private ClusterDomain clusterDomain;

    private String matchCondition;

    private String description;

    private String meno;

    private Long ver;

    private ApiStatus status;

    private String plugins;

    private Date createTime;

    private Date updateTime;

    public boolean isNew() {
        return status.equals(ApiStatus.NEW);
    }
}
