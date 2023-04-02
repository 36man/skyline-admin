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

package org.apache.skyline.admin.server.support.api.configcenter.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.NacosConfigService;
import com.alibaba.nacos.spring.context.event.config.EventPublishingConfigService;
import com.alibaba.nacos.spring.factory.CacheableEventPublishingNacosServiceFactory;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.skyline.admin.server.commons.utils.MapResolver;
import org.apache.skyline.admin.server.support.api.configcenter.ApiConfigPublisher;
import org.apache.skyline.admin.server.support.api.configcenter.model.ApiGenerateDefinition;
import org.apache.skyline.admin.server.support.api.configcenter.model.ConfigOptions;
import org.bravo.gaia.commons.exception.PlatformException;
import org.bravo.gaia.commons.util.AssertUtil;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class NacosConfigPublisher implements ApiConfigPublisher {

    private final NacosConfigChangeService configService = new NacosConfigChangeService();

    @Override
    public boolean delete(ConfigOptions option, List<Long> ids) {
        return doAction(option, result -> {

            if (CollectionUtils.isEmpty(result)) {
                return true;
            }

            List<ApiGenerateDefinition> items = result
                    .stream()
                    .filter(api -> !ids.contains(api.getId()))
                    .collect(Collectors.toList());

            if (items.size() == result.size()) {
                return true;
            }

            return configService.publishConfig(option, items);
        });

    }

    @Override
    public boolean doPublish(ConfigOptions option, List<ApiGenerateDefinition> apis) {
        return doAction(option, (list) -> {
            List<ApiGenerateDefinition> items = new ArrayList<>(list);

            boolean change = false;

            if (CollectionUtils.isNotEmpty(list)) {
                for (ApiGenerateDefinition api : apis) {
                    if (!list.contains(api)) {
                        items.add(api);
                        change = true;
                    }
                }
            } else {
                items.addAll(apis);
                change = true;
            }

            if (change) {
                return configService.publishConfig(option, items);
            }
            return true;
        });
    }

    private <R> R doAction(ConfigOptions configOptions,
                           Function<List<ApiGenerateDefinition>, R> callback) {

        List<ApiGenerateDefinition> result =  this.configService.getConfig(configOptions,ApiGenerateDefinition.class);

        return callback.apply(CollectionUtils.isEmpty(result) ? Collections.emptyList() : result);

    }

    static public class NacosConfigChangeService {

        private final CacheableEventPublishingNacosServiceFactory nacosServiceFactory = CacheableEventPublishingNacosServiceFactory
                .getSingleton();

        private static final String GROUP_KEY = "group";
        private static final String TIME_OUT_KEY = "timeout";

        private final Map<Long, ConfigService> clusterServiceCache = new ConcurrentHashMap<>();

        private Function<ConfigOptions, ConfigService> configServiceFactory = option -> {
            setDefaultOptions(option);

            ConfigService configService = clusterServiceCache.get(option.getId());

            if (configService != null) {
                return configService;
            }

            Properties properties = getNacosProperties(option);


            try{
                EventPublishingConfigService nacosEventConfigService = (EventPublishingConfigService)nacosServiceFactory.createConfigService(properties);

                Field configServiceField = nacosEventConfigService.getClass().getDeclaredField("configService");

                configServiceField.setAccessible(true);

                NacosConfigService nacosConfigService = (NacosConfigService)configServiceField.get(nacosEventConfigService);

                clusterServiceCache.put(option.getId(), nacosConfigService);

                return nacosConfigService;

            }catch (Exception e){
                log.error("ConfigService can't be created with properties:"+properties, e);
                throw new PlatformException("ConfigService can't be create");
            }
        };

        public boolean publishConfig(ConfigOptions option, Object content) {
            AssertUtil.notNull(content,"content can't be null");
            return execute(option, configService -> configService.publishConfig(String.valueOf(option.getId()), (String) option.getConfigItems().get(GROUP_KEY), JSON.toJSONString(content), ConfigType.JSON.getType()));
        }

        public <T> List<T> getConfig(ConfigOptions option, Class<T> clazz) {

            AssertUtil.notNull(option.getId(),"option id can't be null");

            return execute(option, configService -> {

                AssertUtil.notNull(configService, "configService is null");

                String content = configService.getConfig(String.valueOf(option.getId()),
                        option.getConfig(GROUP_KEY, String.class),
                        option.getConfig(TIME_OUT_KEY, Long.class));

                if (StringUtils.isBlank(content)) {
                    return Lists.newArrayList();
                }

                return JSON.parseArray(content, clazz);
            });
        }

        private void setDefaultOptions(ConfigOptions option) {
            Map<String, Object> configItems = option.getConfigItems();

            if (configItems == null) {
                configItems = new HashMap<>();
                option.setConfigItems(configItems);
            }

            if (configItems.get(TIME_OUT_KEY) == null) {
                configItems.put(TIME_OUT_KEY, 3000L);
            }

            option.setConfigItems(configItems);
        }

        private Properties getNacosProperties(ConfigOptions option) {
            Properties nacosConfig = MapResolver.resolveProperties(option.getConfigItems());

            Properties properties = new Properties();

            processProperties(properties,nacosConfig, PropertyKeyConst.SERVER_ADDR);
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

        private <T> T execute(ConfigOptions apiOption, NacosConfigChangeService.NacosCallback<T> callable) {
            try{
                ConfigService configService = configServiceFactory.apply(apiOption);

                return callable.doExecute(configService);
            }catch (Exception e){
                clusterServiceCache.remove(apiOption.getId());

                log.error("Nacos config service error", e);

                throw new PlatformException("Nacos config service error");
            }
        }

        private void processProperties(Properties config,Properties nacosConfig,String key) {
            Object value = nacosConfig.get(key);

            if (value != null) {
                config.put(key, value);
            }
        }

        public interface NacosCallback<T>{
            T doExecute(ConfigService configService) throws NacosException;

        }

    }

}