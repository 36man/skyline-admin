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

package org.apache.skyline.admin.server.config.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "admin")
public class AdminProperties{

    private SwaggerConfig swagger  = new SwaggerConfig();

    private String changeSrvType = "nacos";

    private K8sProperties k8s = new K8sProperties();


    @Data
    public static class SwaggerConfig{

        private boolean enable = true;

    }

    @Setter
    @Getter
    public static class K8sProperties{
        private String masterUrl;
        private String apiVersion;
        private String namespace;
        private String username;
        private String password;
        private String oauthToken;
        private String caCertFile;
        private String caCertData;
        private String clientKeyFile;
        private String clientKeyData;
        private String clientCertFile;
        private String clientCertData;
        private String clientKeyAlgo;
        private String clientKeyPassphrase;
        private int connectionTimeout;
        private int requestTimeout;
        private long rollingTimeout;
        private int watchReconnectInterval;
        private int watchReconnectLimit;
        private int loggingInterval;
        private Boolean trustCerts = true;
        private Boolean http2Disable;
        private String httpProxy;
        private String httpsProxy;
        private String proxyUsername;
        private String proxyPassword;
        private String [] noProxy;
        private boolean enabled = false;
    }
}
