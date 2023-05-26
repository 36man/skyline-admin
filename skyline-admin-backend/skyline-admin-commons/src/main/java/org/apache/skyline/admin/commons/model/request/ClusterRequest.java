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

package org.apache.skyline.admin.commons.model.request;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
public class ClusterRequest {

    @NotBlank
    @Length(max = 50)
    private String clusterName;

    @NotBlank
    @Length(max = 50)
    private String domain;

    @NotBlank
    @Length(max = 50)
    private String bizKey;

    @NotNull
    @Max(100)
    private Integer instanceCount;

    @NotNull
    private Boolean configShare;

    private String configUrl;

    @Length(max = 50)
    private String configUser;

    private String configSecret;

    private Map<String, Object> configItem = new HashMap<>();

    @NotBlank
    @Length(max = 50)
    private String useQuota;

    @Length(max = 100)
    private String meno;

    public boolean isEquals(String clusterDomain, String clusterName) {
        if(StringUtils.isBlank(clusterDomain) && StringUtils.isBlank(clusterName)){
            return false;
        }

        boolean isSame = this.getDomain().equals(clusterDomain)
                && this.getClusterName().equals(clusterName);

        return isSame;
    }


}
