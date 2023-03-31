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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.skyline.admin.server.dal.dataobject.PluginVersionDO;
import org.springframework.boot.context.properties.PropertyMapper;

import java.util.List;

@Data
@Builder
public class PluginVersionCombineQuery implements CombineQuery<PluginVersionDO>{

    private List<Long> pluginIdList;

    private String ver;

    private List<Long> ids;

    private Boolean active;

    private boolean isLoadPlugin = false;


    @Override
    public LambdaQueryWrapper<PluginVersionDO> toQuery() {
        LambdaQueryWrapper<PluginVersionDO> condition = new LambdaQueryWrapper<>();

        PropertyMapper propertyMapper = PropertyMapper.get();

        propertyMapper.from(this::getIds).when(ids-> CollectionUtils.isNotEmpty(ids))
                .to(ids->condition.in(PluginVersionDO::getId, ids));

        propertyMapper.from(this::getActive).whenNonNull().to(id-> condition.eq(
                PluginVersionDO::getActive, active));

        propertyMapper.from(this::getPluginIdList).when(CollectionUtils::isNotEmpty).to(pluginIdList-> condition.in(
                PluginVersionDO::getPluginId, pluginIdList));

        propertyMapper.from(this::getVer).whenHasText().to(ver-> condition.like(
                PluginVersionDO::getVer, ver));

        return condition;
    }
}
