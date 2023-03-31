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

package org.apache.skyline.admin.server.support.api.notify.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.spring.factory.CacheableEventPublishingNacosServiceFactory;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.skyline.admin.server.commons.utils.MapResolver;
import org.apache.skyline.admin.server.support.api.notify.ApiConfigPublisher;
import org.apache.skyline.admin.server.support.api.notify.model.ApiGenerateDefinition;
import org.apache.skyline.admin.server.support.api.notify.model.ConfigOptions;
import org.bravo.gaia.commons.exception.PlatformException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class NacosApiConfigPublisher implements ApiConfigPublisher {


    private final CacheableEventPublishingNacosServiceFactory nacosServiceFactory = CacheableEventPublishingNacosServiceFactory
            .getSingleton();


    private InternalNacosConfigService configService = new InternalNacosConfigService();

    @Override
    public boolean delete(ConfigOptions option, List<Long> ids) {
        List<ApiGenerateDefinition> apiList = configService.getApiList(option);
        if(CollectionUtils.isEmpty(apiList)) {
            return true;
        }
        List<ApiGenerateDefinition> items = apiList.stream().filter(api -> !ids.contains(api.getId()))
                .collect(Collectors.toList());

        if (items.size() == apiList.size()) {
            return true;
        }
        this.configService.publish(option, items);

        return false;
    }

    @Override
    public boolean change(ConfigOptions option, List<ApiGenerateDefinition> apis) {
        List<ApiGenerateDefinition> apiList = configService.getApiList(option);

        List<ApiGenerateDefinition> items = new ArrayList<>(apiList);
        boolean change = false;

        if (CollectionUtils.isNotEmpty(apiList)) {
            for (ApiGenerateDefinition api : apis) {
                if (!apiList.contains(api)) {
                    items.add(api);
                    change = true;
                }
            }
        } else {
            items.addAll(apis);
            change = true;
        }

        if (change) {
            return configService.publish(option, items);
        }
        return true;
    }


    public class InternalNacosConfigService {

        private static final String GROUP_KEY = "group";
        private static final String TIME_OUT_KEY = "timeout";

        private final Map<Long,ConfigService> clusterServiceCache = new HashMap<>();

        private Function<ConfigOptions, ConfigService> configServiceFactory = option -> {
            Properties properties = getNacosProperties(option);

            setDefaultOptions(option);

            try{
                return nacosServiceFactory.createConfigService(properties);
            }catch (Exception e){
                log.error("ConfigService can't be created with properties:"+properties, e);
                throw new PlatformException("ConfigService can't be create");
            }
        };

        private void setDefaultOptions(ConfigOptions option) {
            Map<String, Object> configItems = option.getConfigItems();

            if (configItems == null) {
                configItems = new HashMap<>();
                option.setConfigItems(configItems);
            }

            if (configItems.get(TIME_OUT_KEY) == null) {
                configItems.put(TIME_OUT_KEY, 3000);
            }
        }

        private Properties getNacosProperties(ConfigOptions option) {
            Properties nacosConfig = MapResolver.resolveProperties(option.getConfigItems());

            Properties properties = new Properties();

            processProperties(properties,nacosConfig,PropertyKeyConst.SERVER_ADDR);
            processProperties(properties,nacosConfig,PropertyKeyConst.NAMESPACE);
            processProperties(properties,nacosConfig,PropertyKeyConst.ENDPOINT);
            processProperties(properties,nacosConfig,PropertyKeyConst.SECRET_KEY);
            processProperties(properties,nacosConfig,PropertyKeyConst.ACCESS_KEY);
            processProperties(properties,nacosConfig,PropertyKeyConst.RAM_ROLE_NAME);
            processProperties(properties,nacosConfig,PropertyKeyConst.CONFIG_LONG_POLL_TIMEOUT);
            processProperties(properties,nacosConfig,PropertyKeyConst.CONFIG_RETRY_TIME);
            processProperties(properties,nacosConfig,PropertyKeyConst.MAX_RETRY);
            processProperties(properties,nacosConfig,PropertyKeyConst.USERNAME);
            processProperties(properties,nacosConfig,PropertyKeyConst.PASSWORD);
            processProperties(properties,nacosConfig,PropertyKeyConst.ENABLE_REMOTE_SYNC_CONFIG);

            return properties;
        }

        private <T> T execute(ConfigOptions apiOption, NacosCallback<T> callable) {
            try{
                ConfigService configService = configServiceFactory.apply(apiOption);

                return callable.doExecute(configService);
            }catch (Exception e){
                if(clusterServiceCache.containsKey(apiOption.getId()) && clusterServiceCache.get(apiOption.getId()) != null){
                    try{
                        clusterServiceCache.get(apiOption.getId()).shutDown();
                        clusterServiceCache.remove(apiOption.getId());
                    }catch (Exception ex) {
                    }
                }
                throw new PlatformException("Nacos config service error",e);
            }
        }

        public boolean publish(ConfigOptions option, List<ApiGenerateDefinition> apis) {
            String content = JSON.toJSONString(apis);
            return execute(option, configService -> configService.publishConfig(String.valueOf(option.getId()), (String) option.getConfigItems().get(GROUP_KEY), content, ConfigType.JSON.getType()));
        }

        public List<ApiGenerateDefinition> getApiList(ConfigOptions option) {
            return execute(option, configService -> {
                    String content = configService.getConfig(String.valueOf(option.getId()), option.getConfigItems().get(GROUP_KEY).toString(),Long.valueOf(option.getConfigItems().get(TIME_OUT_KEY).toString()));
                    if (content != null) {
                        return JSON.parseArray(content, ApiGenerateDefinition.class);
                    }
                return Lists.newArrayList();
            });

        }
    }

    public interface NacosCallback<T>{

    T doExecute(ConfigService configService) throws NacosException;


    }

    private void processProperties(Properties config,Properties nacosConfig,String key) {
        Object value = nacosConfig.get(key);

        if (value != null) {
            config.put(key, value);
        }
    }



}