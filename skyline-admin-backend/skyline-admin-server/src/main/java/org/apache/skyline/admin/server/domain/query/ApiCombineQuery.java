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

package org.apache.skyline.admin.server.domain.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.skyline.admin.server.dal.dataobject.ApiDO;
import org.springframework.boot.context.properties.PropertyMapper;

import java.util.List;

@Data
@Accessors(chain = true)
@Builder
public class ApiCombineQuery implements CombineQuery<ApiDO> {

    private Long clusterId;

    private String matchCondition;

    private List<Long> ids;

    private boolean isLoadCluster = false;

    public boolean isLoadCluster(){
        if(isLoadCluster && clusterId != null){
            return true;
        }
        return false;
    }


    @Override
    public LambdaQueryWrapper<ApiDO> toQuery() {
        LambdaQueryWrapper<ApiDO> condition = new LambdaQueryWrapper<>();
        PropertyMapper propertyMapper = PropertyMapper.get();
        propertyMapper.from(this::getClusterId).whenNonNull()
                .to(clusterId -> condition.eq(ApiDO::getClusterId, clusterId));
        propertyMapper.from(this::getIds).when(ids-> CollectionUtils.isNotEmpty(ids))
                .to(ids ->condition.in(ApiDO::getId, ids));

        propertyMapper.from(this::getMatchCondition).whenNonNull()
                .to(matchCondition -> condition.eq(ApiDO::getMatchCondition, matchCondition));

        condition.eq(ApiDO::getDeleted, 0);

        return condition;
    }

}
