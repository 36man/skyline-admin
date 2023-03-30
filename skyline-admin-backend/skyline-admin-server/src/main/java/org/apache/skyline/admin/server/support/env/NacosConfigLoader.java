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

package org.apache.skyline.admin.server.support.env;

import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import org.apache.skyline.admin.server.support.codec.ObjectMapperCodec;

import java.util.LinkedHashMap;
import java.util.Map;

public class NacosConfigLoader implements ConfigLoader{

    private final NacosConfigProperties configProperties;
    private final ObjectMapperCodec objectMapperCodec;

    public NacosConfigLoader(NacosConfigProperties configProperties, ObjectMapperCodec objectMapperCodec ) {
        this.configProperties = configProperties;
        this.objectMapperCodec = objectMapperCodec;
    }

    @Override
    public ConfigCenterValue load() {
        ConfigCenterValue configCenterValue = new ConfigCenterValue();
        configCenterValue.setUrl(configProperties.getServerAddr());
        configCenterValue.setSecret(configProperties.getPassword());
        configCenterValue.setUser(configProperties.getUsername());

        Map<String,Object> items = objectMapperCodec.serialize(configProperties, LinkedHashMap.class);

        configCenterValue.setConfigs(items);

        return configCenterValue;
    }

}
