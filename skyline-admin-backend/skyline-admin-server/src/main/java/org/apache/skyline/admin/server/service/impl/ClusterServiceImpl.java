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

package org.apache.skyline.admin.server.service.impl;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import io.fabric8.kubernetes.client.utils.PodStatusUtil;
import jodd.util.StringTemplateParser;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.skyline.admin.commons.enums.ClusterStatus;
import org.apache.skyline.admin.commons.model.query.ClusterQuery;
import org.apache.skyline.admin.commons.model.request.ClusterRequest;
import org.apache.skyline.admin.commons.model.request.PageRequest;
import org.apache.skyline.admin.commons.model.vo.ClusterVO;
import org.apache.skyline.admin.server.commons.utils.PageCommonUtils;
import org.apache.skyline.admin.server.domain.model.ClusterDomain;
import org.apache.skyline.admin.server.domain.query.ClusterCombineQuery;
import org.apache.skyline.admin.server.domain.repository.ClusterRepository;
import org.apache.skyline.admin.server.service.ClusterService;
import org.apache.skyline.admin.server.support.env.ConfigLoader;
import org.apache.skyline.admin.server.support.k8s.K8sClient;
import org.apache.skyline.admin.server.support.k8s.K8sTemplateLoader;
import org.apache.skyline.admin.server.support.k8s.WatcherWrapper;
import org.apache.skyline.admin.server.support.mapper.ClusterAssembler;
import org.bravo.gaia.commons.base.PageBean;
import org.bravo.gaia.commons.util.AssertUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ClusterServiceImpl implements Watcher<Pod>, ClusterService {

    private ClusterRepository clusterRepository;
    private ObjectProvider<ConfigLoader> configLoaderProvider;
    private ClusterAssembler clusterAssembler;

    private K8sTemplateLoader k8sTemplateLoader;

    private final K8sClient k8sClient;


    public ClusterServiceImpl(ClusterRepository clusterRepository,
                              ObjectProvider<ConfigLoader> configLoaderObjectProvider,
                              ClusterAssembler clusterAssembler,
                              K8sTemplateLoader k8sTemplateLoader,
                              K8sClient k8sClient) {
        this.clusterAssembler = clusterAssembler;
        this.configLoaderProvider = configLoaderObjectProvider;
        this.clusterRepository = clusterRepository;
        this.k8sTemplateLoader = k8sTemplateLoader;
        this.k8sClient = k8sClient;

    }

    public boolean create(ClusterRequest request){
        this.setConfigItem(request);

        ClusterDomain clusterDomain = this.convert(request);

        throwIfDuplicate(request);

        return clusterRepository.save(clusterDomain);
    }


    @Override
    public ClusterVO queryForOne(ClusterQuery clusterQuery) {
        ClusterCombineQuery combineQuery = this.toQuery(clusterQuery);

        ClusterDomain item = clusterRepository.findOneIfExists(combineQuery);
        
        return clusterAssembler.convert(item);
    }

    @Override
    public boolean update(Long id, ClusterRequest request) {
        AssertUtil.isNotBlank(request.getDomain(),"domain is blank");
        AssertUtil.isNotBlank(request.getClusterName(),"clusterName is blank");

        ClusterCombineQuery queryCluster = ClusterCombineQuery.builder()
                .id(id)
                .build();

        this.setConfigItem(request);

        ClusterDomain clusterDomain = clusterRepository.findOneIfExists(queryCluster);

        if (!request.isEquals(clusterDomain.getDomain(),clusterDomain.getClusterName())) {
            throwIfDuplicate(request);
        }

        BeanUtils.copyProperties(request, clusterDomain);

        return clusterRepository.update(queryCluster,clusterDomain);
    }

    @Override
    public boolean delete(Long id) {
        Pair<String, ClusterDomain> context = getDeployContext(id);

        WatcherWrapper<Pod,Boolean> watcherWrapper = new WatcherWrapper<>((action, pod) -> {
            if (action.equals(Action.DELETED)) {
                return clusterRepository.deleteById(id);
            }
            return null;
        });

        registerWatcher(context.getRight(), watcherWrapper);

        k8sClient.delete(context.getLeft());

        return watcherWrapper.get() == null ? false : watcherWrapper.get();

    }

    @Override
    public PageBean<ClusterVO> pageList(PageRequest<ClusterQuery> pageRequest) {
        ClusterQuery condition = pageRequest.getCondition();

        ClusterCombineQuery queryCondition = ClusterCombineQuery.builder()
                .domain(condition.getDomain())
                .clusterName(condition.getClusterName())
                .bizKey(condition.getBizKey())
                .build();

        PageBean<ClusterDomain> pageBean = clusterRepository.pageQuery(queryCondition, pageRequest.getPageNo(), pageRequest.getPageSize());

        return PageCommonUtils.convert(pageBean, clusterDomains -> convert(clusterDomains));
    }

    @Override
    public boolean applyCluster(Long id) {
        Pair<String,ClusterDomain> deployContext = this.getDeployContext(id);

        this.registerWatcher(deployContext.getRight());

        boolean deploySuccessfully = k8sClient.deploy(deployContext.getLeft());

        return deploySuccessfully;

    }


    private ClusterDomain convert(ClusterRequest request) {
        ClusterDomain clusterDomain = new ClusterDomain();

        BeanUtils.copyProperties(request, clusterDomain);

        return clusterDomain;
    }

    private List<ClusterVO> convert(List<ClusterDomain> clusterList){
        return Optional.ofNullable(clusterList)
                .orElseGet(ArrayList::new)
                .stream()
                .map(clusterAssembler::convert)
                .collect(Collectors.toList());

    }

    private ClusterCombineQuery toQuery(ClusterQuery clusterQuery) {
        return ClusterCombineQuery.builder()
                .bizKey(clusterQuery.getBizKey())
                .domain(clusterQuery.getDomain())
                .id(clusterQuery.getId())
                .clusterName(clusterQuery.getClusterName())
                .build();
    }

    private void setConfigItem(ClusterRequest request) {
        PropertyMapper propertyMapper = PropertyMapper.get();

        propertyMapper.from(request.getConfigShare())
                .whenTrue()
                .as(e-> {
                    ConfigLoader configLoader = configLoaderProvider.getIfAvailable(
                            () -> ConfigLoader.DEFAULT);
                    return configLoader.load();

                })
                .whenNonNull()
                .to(item->{
                    request.setConfigUser(item.getUser());
                    request.setConfigSecret(item.getSecret());
                    request.setConfigUrl(item.getUrl());
                    request.setConfigItem(item.getConfigs());
                });

        propertyMapper.from(request.getConfigShare())
                .whenFalse()
                .toCall(()->{
                    AssertUtil.isNotBlank(request.getConfigSecret(), "configSecret is blank");
                    AssertUtil.isNotBlank(request.getConfigUser(), "configUser is blank");
                    AssertUtil.isNotBlank(request.getConfigUrl(), "configUrl is blank");
                });
    }

    private void throwIfDuplicate(ClusterRequest request) {
        ClusterCombineQuery build = ClusterCombineQuery.builder().domain(request.getDomain())
                .clusterName(request.getClusterName()).build();

        boolean isExists = clusterRepository.isExists(build);
        AssertUtil.isTrue(!isExists, "domain is exists");
    }


    @Override
    public void eventReceived(Action action, Pod pod) {
        String domain = pod.getMetadata().getLabels().get("domain");
        if (action == Action.DELETED) {
            this.notifyClusterChanged(domain,ClusterStatus.FAILED);
            return;
        }
        ClusterStatus status = ClusterStatus.getEnumByCode(pod.getStatus().getPhase());

        if (PodStatusUtil.isRunning(pod)) {
            status = ClusterStatus.RUNNING;
        }
        this.notifyClusterChanged(domain,status);
    }

    private void notifyClusterChanged(String domain, ClusterStatus status) {
        ClusterDomain clusterDomain = new ClusterDomain();
        clusterDomain.setStatus(status);

        this.clusterRepository.update(ClusterCombineQuery.builder().domain(domain).build(), clusterDomain);
    }

    @Override
    public void onClose(WatcherException e) {

    }

    private Pair<String,ClusterDomain> getDeployContext(Long id) {
        ClusterDomain clusterDomain = clusterRepository.findOneIfExists(ClusterCombineQuery.builder().id(id).build());

        Map<String, Object> values = new HashMap<>();
        values.put("domain", clusterDomain.getDomain());
        values.put("replicas", clusterDomain.getInstanceCount());
        String useQuota = clusterDomain.getUseQuota();
        Pattern pattern = Pattern.compile("(\\d+[a-zA-Z]+)(\\d+[a-zA-Z]+)");
        Matcher matcher = pattern.matcher(useQuota);
        if (matcher.find()) {
            values.put("limits.cpu", matcher.group(1));
            values.put("limits.memory", matcher.group(2));
        }else{
            values.put("limits.cpu", "1000m");
            values.put("limits.memory", "800Mi");
        }
        String content = StringTemplateParser.ofMap(values)
                .apply(k8sTemplateLoader.getClusterTemplate());

        return Pair.of(content,clusterDomain);
    }

    private void registerWatcher(ClusterDomain clusterDomain) {
        this.registerWatcher(clusterDomain, this);
    }

    private void registerWatcher(ClusterDomain clusterDomain,Watcher<Pod> watcher) {
        k8sClient.getKubernetesClient()
                .pods()
                .inNamespace("default")
                .withLabel("domain", clusterDomain.getDomain())
                .watch(watcher);
    }
}
