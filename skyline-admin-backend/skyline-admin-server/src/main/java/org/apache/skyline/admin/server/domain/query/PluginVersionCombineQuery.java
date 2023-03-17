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
import org.apache.skyline.admin.server.dal.dataobject.PluginVersionDO;
import org.bravo.gaia.commons.util.AssertUtil;
import org.springframework.boot.context.properties.PropertyMapper;

@Data
@Builder
public class PluginVersionCombineQuery implements CombineQuery<PluginVersionDO>{

    private Long pluginId;

    private String ver;

    @Override
    public LambdaQueryWrapper<PluginVersionDO> toQuery() {

        AssertUtil.notNull(pluginId, "pluginId is null");

        LambdaQueryWrapper<PluginVersionDO> condition = new LambdaQueryWrapper<>();

        PropertyMapper propertyMapper = PropertyMapper.get();
        propertyMapper.from(this::getPluginId).whenNonNull().to(pluginId-> condition.eq(
                PluginVersionDO::getPluginId, pluginId));

        propertyMapper.from(this::getVer).whenHasText().to(ver-> condition.eq(
                PluginVersionDO::getVer, ver));

        condition.eq(PluginVersionDO::getDeleted, false);

        return condition;
    }
}