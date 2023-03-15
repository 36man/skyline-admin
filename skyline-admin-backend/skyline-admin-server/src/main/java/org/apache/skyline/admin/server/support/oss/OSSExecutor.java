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

package org.apache.skyline.admin.server.support.oss;

import org.apache.skyline.admin.server.support.oss.config.StoreType;
import org.apache.skyline.admin.server.support.oss.service.OSSService;
import org.bravo.gaia.commons.util.AssertUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OSSExecutor implements InitializingBean {

    @Autowired
    private ApplicationContext applicationContext;

    private Map<StoreType, OSSService> storeTypeService;

    public <T> T doExecute(StoreType storeType, Function<OSSService,T> callback) {

        OSSService ossService = storeTypeService.get(storeType);

        AssertUtil.notNull(ossService, "ossService not found match type");

        return callback.apply(ossService);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        storeTypeService = applicationContext.getBeansOfType(OSSService.class).values().stream()
                .collect(Collectors.toMap(OSSService::storeType, v -> v));

    }
}
