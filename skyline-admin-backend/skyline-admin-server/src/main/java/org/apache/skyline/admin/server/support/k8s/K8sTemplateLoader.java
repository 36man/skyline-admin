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
package org.apache.skyline.admin.server.support.k8s;
import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.spring.factory.CacheableEventPublishingNacosServiceFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Data
@Slf4j
public class K8sTemplateLoader implements  SmartInitializingSingleton, ApplicationListener<ApplicationReadyEvent> {

    private String clusterTemplate;

    @Autowired
    private NacosConfigProperties nacosConfigProperties;


    private static final CacheableEventPublishingNacosServiceFactory singleton = CacheableEventPublishingNacosServiceFactory.getSingleton();


    private ClusterTemplateLoader clusterTemplateLoader = new ClusterTemplateLoader( "cluster-template",this);

    @Override
    public void afterSingletonsInstantiated() {
        addListener(clusterTemplateLoader);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        setClusterTemplate(clusterTemplateLoader.getContent());
    }

    public void addListener(NacosConfigListener nacosConfigListener){
        singleton.getConfigServices().stream()
                .forEach(configService -> {
                    try {
                        configService.addListener(nacosConfigListener.getConfigId(), nacosConfigProperties.getGroup(), nacosConfigListener);
                    } catch (Exception exception) {
                        log.warn("nacos addListener error {} ", exception.getMessage());
                    }
                });
    }

    public String getContent(String dataId) {
        for (ConfigService configService : singleton.getConfigServices()) {
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

    public class ClusterTemplateLoader extends NacosConfigListener<String>{

        private String configId;
        private final K8sTemplateLoader k8sTemplateLoader;

        public ClusterTemplateLoader(String configId, K8sTemplateLoader k8sTemplateLoader) {
            this.configId = configId;
            this.k8sTemplateLoader = k8sTemplateLoader;
        }

        @Override
        public String getConfigId() {
            return this.configId;
        }

        @Override
        public String getContent() {
            return k8sTemplateLoader.getContent(this.configId);
        }

        @Override
        public void receiveConfigInfo(String configInfo) {
            k8sTemplateLoader.setClusterTemplate(configInfo);
        }
    }


    public abstract class NacosConfigListener<T> extends AbstractListener{

        abstract String getConfigId();

        abstract T getContent();

    }

}
