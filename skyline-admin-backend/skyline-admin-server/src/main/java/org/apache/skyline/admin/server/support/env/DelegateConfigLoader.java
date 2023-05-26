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

import org.bravo.gaia.commons.util.AssertUtil;
import org.springframework.beans.factory.SmartInitializingSingleton;

public class DelegateConfigLoader implements ConfigLoader, SmartInitializingSingleton {

    private ConfigLoader configLoader;

    private ConfigCenterValue configCenterValue;

    public DelegateConfigLoader(ConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    @Override
    public ConfigCenterValue load() {
        return configCenterValue;
    }

    @Override
    public void afterSingletonsInstantiated() {
        this.configCenterValue = configLoader.load();
        AssertUtil.notNull(configCenterValue, "config center value is null");
        AssertUtil.isNotBlank(configCenterValue.getUrl(), "config center url is blank");

    }
}
