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

package org.apache.skyline.admin.server.model.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.skyline.admin.server.dal.dataobject.ClusterDO;
import org.springframework.boot.context.properties.PropertyMapper;

@Data
@Accessors(chain = true)
@Builder
public class ClusterConditionQuery {

    private Long id;

    private String domain;

    private String clusterName;

    private String businessName;


    public LambdaQueryWrapper<ClusterDO> buildQuery(){

        LambdaQueryWrapper<ClusterDO> condition = new LambdaQueryWrapper<ClusterDO>();

        PropertyMapper propertyMapper = PropertyMapper.get();
        propertyMapper.from(this::getId).whenNonNull().to(domain-> condition.eq(ClusterDO::getId, id));
        propertyMapper.from(this.getDomain()).whenHasText().to(domain-> condition.eq(ClusterDO::getDomain,domain));
        propertyMapper.from(this.getClusterName()).whenHasText().to(clusterName-> condition.eq(ClusterDO::getClusterName,clusterName));
        propertyMapper.from(this.getBusinessName()).whenHasText().to(businessName-> condition.like(ClusterDO::getBusinessName,businessName));

        return condition;
    }
}
