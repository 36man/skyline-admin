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

package org.apache.skyline.admin.server.support.api.config.model;

import com.google.common.collect.ImmutableMultiset;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Data
public class ApiGenerateDefinition implements Serializable {

    private Long id;

    private String clusterId;

    private Long apiVer;

    private String clusterDomain;

    private String matchCondition;

    private List<ApiPlugin> plugins;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ApiGenerateDefinition))
            return false;
        ApiGenerateDefinition apiInfo = (ApiGenerateDefinition) o;
        return Objects.equals(id, apiInfo.id) && Objects.equals(clusterId,
                apiInfo.clusterId) && Objects.equals(clusterDomain,
                apiInfo.clusterDomain) && Objects.equals(matchCondition,
                apiInfo.matchCondition) && isEqual(plugins, apiInfo.plugins);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clusterId, clusterDomain, matchCondition, plugins);
    }

    @Data
    public static class ApiPlugin {

        private String stage;

        private String stageName;

        private Integer stateSn;

        private String jarUrl;

        private Integer sn;

        private String config;

        private String classDefine;

        private String ver;

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof ApiPlugin))
                return false;
            ApiPlugin plugin = (ApiPlugin) o;
            return Objects.equals(getStateSn(), plugin.getStateSn()) && Objects.equals(getJarUrl(),
                    plugin.getJarUrl()) && Objects.equals(getSn(),
                    plugin.getSn()) && Objects.equals(getConfig(),
                    plugin.getConfig()) && Objects.equals(getClassDefine(),
                    plugin.getClassDefine());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getStateSn(), getJarUrl(), getSn(), getConfig(), getClassDefine());
        }
    }

    public static boolean isEqual(List<ApiPlugin> source, List<ApiPlugin> target) {
        return ImmutableMultiset.copyOf(source).equals(ImmutableMultiset.copyOf(target));
    }
}
