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
import org.apache.skyline.admin.server.dal.dataobject.PluginDO;
import org.springframework.boot.context.properties.PropertyMapper;

@Data
@Accessors(chain = true)
@Builder
public class PluginCombineQuery implements CombineQuery<PluginDO> {

    private Long id;

    private String classDefine;

    private String pluginName;

    private String maintainer;

    public LambdaQueryWrapper<PluginDO> toQuery() {
        LambdaQueryWrapper<PluginDO> condition = new LambdaQueryWrapper<>();

        PropertyMapper propertyMapper = PropertyMapper.get();
        propertyMapper.from(this::getPluginName).whenNonNull().to(pluginName-> condition.like(PluginDO::getPluginName, pluginName));
        propertyMapper.from(this::getMaintainer).whenNonNull().to(maintainer-> condition.like(PluginDO::getMaintainer, maintainer));
        propertyMapper.from(this::getClassDefine).whenNonNull().to(classDefine-> condition.like(PluginDO::getClassDefine, classDefine));
        propertyMapper.from(this::getId).whenHasText().to(id-> condition.eq(PluginDO::getId, id));

        condition.eq(PluginDO::getDeleted, false);

        return condition;
    }
}
