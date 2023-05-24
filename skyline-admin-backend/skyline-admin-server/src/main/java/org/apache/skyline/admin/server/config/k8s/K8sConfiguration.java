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

package org.apache.skyline.admin.server.config.k8s;

import io.fabric8.kubernetes.client.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.skyline.admin.server.config.properties.AdminProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({AdminProperties.class})
@ConditionalOnProperty(prefix = "admin", name = "k8s.enabled", havingValue = "true")
public class K8sConfiguration {

    @Autowired
    private AdminProperties adminProperties;

    public static final String DEFAULT_MASTER_URL = "https://kubernetes.default.svc";
    public static final String DEFAULT_MASTER_PLACEHOLDER = "DEFAULT_MASTER_HOST";


    @Bean
    public KubernetesClient kubernetesClient() {
        return new DefaultKubernetesClient(buildConfig(adminProperties.getK8s()));
    }

    private Config buildConfig(AdminProperties.K8sProperties k8sProperties) {
        Config base = Config.autoConfigure(null);

        return new ConfigBuilder(base) //
                .withMasterUrl(buildMasterUrl(k8sProperties.getMasterUrl())) //
                .withApiVersion(getConfigOrDefault(k8sProperties.getApiVersion(), base.getApiVersion())) //
                .withNamespace(getConfigOrDefault(k8sProperties.getNamespace(), base.getNamespace())) //
                .withUsername(getConfigOrDefault(k8sProperties.getUsername(), base.getUsername())) //
                .withPassword(getConfigOrDefault(k8sProperties.getPassword(), base.getPassword())) //

                .withOauthToken(getConfigOrDefault(k8sProperties.getOauthToken(), base.getOauthToken())) //

                .withCaCertFile(getConfigOrDefault(k8sProperties.getCaCertFile(), base.getCaCertFile())) //
                .withCaCertData(getConfigOrDefault(k8sProperties.getCaCertData(), decodeBase64(base.getCaCertData()))) //

                .withClientKeyFile(getConfigOrDefault(k8sProperties.getClientKeyFile(), base.getClientKeyFile())) //
                .withClientKeyData(getConfigOrDefault(k8sProperties.getClientKeyData(), decodeBase64(base.getClientKeyData()))) //

                .withClientCertFile(getConfigOrDefault(k8sProperties.getClientCertFile(), base.getClientCertFile())) //
                .withClientCertData(getConfigOrDefault(k8sProperties.getClientCertData(), decodeBase64(base.getClientCertData()))) //

                .withClientKeyAlgo(getConfigOrDefault(k8sProperties.getClientKeyAlgo(), base.getClientKeyAlgo())) //
                .withClientKeyPassphrase(getConfigOrDefault(k8sProperties.getClientKeyPassphrase(), base.getClientKeyPassphrase())) //

                .withConnectionTimeout(getConfigOrDefault(k8sProperties.getConnectionTimeout(), base.getConnectionTimeout())) //
                .withRequestTimeout(getConfigOrDefault(k8sProperties.getRequestTimeout(), base.getRequestTimeout())) //
                .withRollingTimeout(getConfigOrDefault(k8sProperties.getRollingTimeout(), base.getRollingTimeout())) //

                .withWatchReconnectInterval(getConfigOrDefault(k8sProperties.getWatchReconnectInterval(), base.getWatchReconnectInterval())) //
                .withWatchReconnectLimit(getConfigOrDefault(k8sProperties.getWatchReconnectLimit(), base.getWatchReconnectLimit())) //
                .withLoggingInterval(getConfigOrDefault(k8sProperties.getLoggingInterval(), base.getLoggingInterval())) //

                .withTrustCerts(getConfigOrDefault(k8sProperties.getTrustCerts(), base.isTrustCerts())) //
                .withHttp2Disable(getConfigOrDefault(k8sProperties.getHttp2Disable(), base.isHttp2Disable())) //

                .withHttpProxy(getConfigOrDefault(k8sProperties.getHttpProxy(), base.getHttpProxy())) //
                .withHttpsProxy(getConfigOrDefault(k8sProperties.getHttpsProxy(), base.getHttpsProxy())) //
                .withProxyUsername(getConfigOrDefault(k8sProperties.getProxyUsername(), base.getProxyUsername())) //
                .withProxyPassword(getConfigOrDefault(k8sProperties.getProxyPassword(), base.getProxyPassword())) //
                .withNoProxy(getConfigOrDefault(k8sProperties.getNoProxy(), base.getNoProxy())) //
                .build();
    }


    private static String getConfigOrDefault(String value, String defaultValue) {
        if (StringUtils.isNoneBlank(value)) {
            return value;
        }
        return defaultValue;
    }

    private static boolean getConfigOrDefault(Boolean value, boolean defaultValue) {
        if (null!=value) {
            return value.booleanValue();
        }
        return defaultValue;
    }

    private static String [] getConfigOrDefault(String [] value, String [] defaultValue) {
        if (value != null && value.length > 0) {
            return value;
        }
        return defaultValue;
    }

    private static long getConfigOrDefault(Long value, long defaultValue) {
        if (null != value && value > 0) {

            return value.longValue();
        }
        return defaultValue;
    }

    public int getConfigOrDefault(Integer value, int defaultValue) {
        if (value !=null ) {
            return value.intValue();
        }
        return defaultValue;
    }

    private static String decodeBase64(String str) {
        return StringUtils.isNotEmpty(str) ?
                new String(Base64.getDecoder().decode(str)) :
                null;
    }

    private static String buildMasterUrl(String masterUrl) {
        if (DEFAULT_MASTER_PLACEHOLDER.equalsIgnoreCase(masterUrl)) {
            return DEFAULT_MASTER_URL;
        }
        return masterUrl;
    }

}
