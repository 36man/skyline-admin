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

package org.apache.skyline.admin.server.support.config;

import lombok.Data;
import org.bravo.gaia.commons.util.AssertUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ConfigCenterEnvironmentLoader {


    public ConfigCenterEnvironmentLoader(){
    }

    public ConfigCenterValue load(){
        ConfigCenterValue configCenterValue =this.doLoad();

        AssertUtil.isNotBlank(configCenterValue.getUser(),"configCenter user is blank");
        AssertUtil.isNotBlank(configCenterValue.getSecret(),"configCenter secure is blank");
        AssertUtil.isNotBlank(configCenterValue.getUrl(),"configCenter url is blank");

        return configCenterValue;

    }

    private ConfigCenterValue doLoad() {
        ConfigCenterValue configCenterValue = new ConfigCenterValue();

        Map<String, Object> items = new HashMap<>();
        items.put("namespace","default");
        items.put("group", "default");

        configCenterValue.setUrl("http://localhost:8889");
        configCenterValue.setUser("nacos");
        configCenterValue.setSecret("nacos");
        configCenterValue.setItems(items);

        return configCenterValue;
    }

    @Data
    public static class ConfigCenterValue {

        private String user;

        private String secret;

        private String url;

        private Map<String, Object> items = new HashMap<>();

    }
}
