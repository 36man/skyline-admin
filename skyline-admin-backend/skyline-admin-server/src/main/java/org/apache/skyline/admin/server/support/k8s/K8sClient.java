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
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.client.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bravo.gaia.commons.util.AssertUtil;
import org.bravo.gaia.commons.util.CollectionUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class K8sClient {

    private ObjectProvider<KubernetesClient> kubernetesClientsProvider;


    public K8sClient(ObjectProvider<KubernetesClient> kubernetesClientsProvider) {
        this.kubernetesClientsProvider = kubernetesClientsProvider;
    }

    public boolean deploy(String content) {
        try{

            KubernetesClient k8sClient = this.getKubernetesClient();

            k8sClient.resource(content).createOrReplace();

            return true;
        }catch(Exception ex){
            return false;
        }
    }

    public boolean delete(String content) {
        try{

            KubernetesClient k8sClient = this.getKubernetesClient();

            List<StatusDetails> statusDetailsList = k8sClient.resource(content)
                    .withTimeout(5000, TimeUnit.MICROSECONDS)
                    .delete();

            for (StatusDetails statusDetails : statusDetailsList) {
                if (statusDetails != null && statusDetails.getKind() != null
                        && CollectionUtils.isNotEmpty(statusDetails.getCauses())) {
                    log.info("delete {} {} {}", statusDetails.getKind(), statusDetails.getName(), statusDetails.getCauses());
                    return false;
                }
            }
            return true;
        }catch(Exception ex){
            log.error("delete error {}", ex);
            return false;
        }
    }

    public KubernetesClient getKubernetesClient() {
        validate();
        return kubernetesClientsProvider.getIfAvailable();
    }

    private void validate() {
        AssertUtil.notNull(kubernetesClientsProvider.getIfAvailable() != null,"kubernetesClient is null");
    }
}
