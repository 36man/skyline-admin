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
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.skyline.admin.server.commons.utils.MapResolver;
import org.apache.skyline.admin.server.support.api.notify.ApiConfigPublisher;
import org.apache.skyline.admin.server.support.api.notify.model.API;
import org.apache.skyline.admin.server.support.api.notify.model.ConfigOptions;
import org.bravo.gaia.commons.exception.PlatformException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.alibaba.nacos.api.annotation.NacosProperties.*;
import static com.alibaba.nacos.api.common.Constants.USERNAME;

@Slf4j
public class NacosApiConfigPublisher implements ApiConfigPublisher {

    private NacosConfigServiceSimple nacosConfigService = new NacosConfigServiceSimple();

    @Override
    public boolean delete(ConfigOptions option, List<Long> ids) {
        List<API> apiList = nacosConfigService.getApiList(option);
        if(CollectionUtils.isEmpty(apiList)) {
            return true;
        }
        List<API> items = apiList.stream().filter(api -> !ids.contains(api.getId()))
                .collect(Collectors.toList());

        if (items.size() == apiList.size()) {
            return true;
        }
        this.nacosConfigService.publish(option, items);

        return false;
    }

    @Override
    public boolean change(ConfigOptions option, List<API> apis) {



        List<API> apiList = nacosConfigService.getApiList(option);

        List<API> items = new ArrayList<>(apiList);

        if (CollectionUtils.isNotEmpty(apiList)) {
            for (API api : apis) {
                if (!apiList.contains(api)) {
                    items.add(api);
                }
            }
        } else {
            items.addAll(apis);
        }

        return nacosConfigService.publish(option, items);
    }


    public class NacosConfigServiceSimple {

        private static final String GROUP_KEY = "group";
        private static final String TIME_OUT_KEY = "timeout";

        private final Map<Long,ConfigService> clusterServiceCache = new HashMap<>();

        private Function<ConfigOptions, ConfigService> configServiceFactory = option -> {
            if (clusterServiceCache.containsKey(option.getId())) {
                return clusterServiceCache.get(option.getId());
            }
            Properties properties = getNacosProperties(option);
            try {
                ConfigService configService = NacosFactory.createConfigService(properties);
                clusterServiceCache.put(option.getId(), configService);

                return configService;
            } catch (NacosException e) {
                log.error("ConfigService can't be created with properties:"+properties, e);
                throw new PlatformException("ConfigService can't be create");
            }
        };

        private Properties getNacosProperties(ConfigOptions option) {
            Map<String, Object> configItem = option.getConfigItems();
            if (configItem == null) {
                configItem = new HashMap<>();
            }
            if (configItem.get(NAMESPACE) == null) {
                configItem.put(NAMESPACE, "public");
            }
            if (configItem.get(GROUP_KEY) == null) {
                configItem.put(GROUP_KEY, Constants.DEFAULT_GROUP);
            }
            if (configItem.get(TIME_OUT_KEY) == null) {
                configItem.put(TIME_OUT_KEY, 3000);
            }
            Properties configProperties = MapResolver.resolveProperties(configItem);

            configProperties.put(USERNAME, option.getConfigUser());
            configProperties.put(PASSWORD, option.getConfigSecret());
            configProperties.put(SERVER_ADDR, option.getConfigUrl());

            return configProperties;
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

        public boolean publish(ConfigOptions option, List<API> apis) {
            String content = JSON.toJSONString(apis);
            return execute(option, configService -> configService.publishConfig(String.valueOf(option.getId()), (String) option.getConfigItems().get(GROUP_KEY), content, ConfigType.JSON.getType()));
        }

        public List<API> getApiList(ConfigOptions option) {
            return execute(option, configService -> {
                    String content = configService.getConfig(String.valueOf(option.getId()), option.getConfigItems().get(GROUP_KEY).toString(),Long.valueOf(option.getConfigItems().get(TIME_OUT_KEY).toString()));
                    if (content != null) {
                        return JSON.parseArray(content, API.class);
                    }
                    return null;
            });

        }
    }


    public interface NacosCallback<T>{

    T doExecute(ConfigService configService) throws NacosException;


    }

}