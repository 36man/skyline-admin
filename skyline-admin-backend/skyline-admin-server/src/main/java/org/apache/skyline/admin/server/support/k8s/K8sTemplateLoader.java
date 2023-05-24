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
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.skyline.admin.server.support.nacos.NacosConfigListener;
import org.apache.skyline.admin.server.support.nacos.NacosConfigServiceCustomizer;
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

    @Autowired
    private NacosConfigServiceCustomizer nacosCustomizer;


    private ClusterTemplateLoader clusterTemplateLoader = new ClusterTemplateLoader( "cluster-template",this);

    @Override
    public void afterSingletonsInstantiated() {
        nacosCustomizer.addListener(clusterTemplateLoader);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        setClusterTemplate(clusterTemplateLoader.getContent());
    }


    public class ClusterTemplateLoader extends NacosConfigListener<String> {

        private String dataId;
        private final K8sTemplateLoader k8sTemplateLoader;

        public ClusterTemplateLoader(String configId, K8sTemplateLoader k8sTemplateLoader) {
            this.dataId = configId;
            this.k8sTemplateLoader = k8sTemplateLoader;
        }

        @Override
        public String getDataId() {
            return this.dataId;
        }

        @Override
        public String getContent() {
            return nacosCustomizer.getContent(this.dataId);
        }

        @Override
        public void receiveConfigInfo(String configInfo) {
            k8sTemplateLoader.setClusterTemplate(configInfo);
        }
    }
}
