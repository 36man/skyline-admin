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

package org.apache.skyline.admin.server.support.nacos;

import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.spring.factory.CacheableEventPublishingNacosServiceFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NacosConfigServiceCustomizer {


    @Autowired
    private NacosConfigProperties nacosConfigProperties;

    private static final CacheableEventPublishingNacosServiceFactory singleton = CacheableEventPublishingNacosServiceFactory.getSingleton();

    private volatile List<ConfigService> configServices;

    public void addListener(NacosConfigListener nacosConfigListener){
        this.addListener(nacosConfigListener,nacosConfigListener.getDataId(),nacosConfigProperties.getGroup());
    }

    public void addListener(Listener listener,String dataId,String group){
        singleton.getConfigServices().stream()
                .forEach(configService -> {
                    try {
                        configService.addListener(dataId, StringUtils.isBlank(group)? Constants.DEFAULT_GROUP:group, listener);
                    } catch (Exception exception) {
                        log.warn("nacos addListener error {} ", exception.getMessage());
                    }
                });
    }

    public String getContent(String dataId) {
        for (ConfigService configService : this.getConfigService()) {
            try {
                String config = configService.getConfig(dataId, nacosConfigProperties.getGroup(), 3000);

                if (StringUtils.isNoneBlank(config)) {
                    return config;
                }
            } catch (Exception exception) {
                log.warn("nacos dataId get config error {} ", exception.getMessage());
            }
        }
        return null;
    }

    public List<ConfigService> getConfigService() {
        if (configServices == null) {
            synchronized (this) {
                if (configServices == null) {
                    configServices = singleton.getConfigServices().stream().collect(Collectors.toList());
                }
            }
        }
        return configServices;
    }
}
