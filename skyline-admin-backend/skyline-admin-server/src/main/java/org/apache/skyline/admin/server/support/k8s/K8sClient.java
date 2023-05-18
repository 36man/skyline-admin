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
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.List;

@Component
public class K8sClient {

    private ObjectProvider<KubernetesClient> kubernetesClientsProvider;

    public K8sClient(ObjectProvider<KubernetesClient> kubernetesClientsProvider) {
        this.kubernetesClientsProvider = kubernetesClientsProvider;
    }

    public void apply(String content) {
        kubernetesClientsProvider.ifAvailable(kubernetesClient -> {
            List<HasMetadata> result = kubernetesClient.load(new ByteArrayInputStream(content.getBytes()))
                    .inNamespace("default")
                    .createOrReplace();


//            GenericKubernetesResourceList resourceList = kubernetesClient.genericKubernetesResources("v1", "deployment")
//                    .inNamespace("default")
//                    .list();
        });
    }
}
