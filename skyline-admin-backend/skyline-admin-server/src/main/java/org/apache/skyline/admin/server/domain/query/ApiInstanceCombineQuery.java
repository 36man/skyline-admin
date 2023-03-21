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
import org.apache.skyline.admin.server.dal.dataobject.ApiInstanceDO;
import org.springframework.boot.context.properties.PropertyMapper;

@Data
@Accessors(chain = true)
@Builder
public class ApiInstanceCombineQuery implements CombineQuery<ApiInstanceDO> {

    private Long clusterId;

    private String matchPath;

    @Override
    public LambdaQueryWrapper<ApiInstanceDO> toQuery() {
        LambdaQueryWrapper<ApiInstanceDO> condition = new LambdaQueryWrapper<>();
        PropertyMapper propertyMapper = PropertyMapper.get();
        propertyMapper.from(this::getClusterId).whenNonNull()
                .to(clusterId -> condition.eq(ApiInstanceDO::getClusterId, clusterId));

        propertyMapper.from(this::getMatchPath).whenNonNull()
                .to(matchPath -> condition.eq(ApiInstanceDO::getMatchPath, matchPath));


        return condition;
    }

}
