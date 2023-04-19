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

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import org.apache.skyline.admin.commons.enums.ApiStatus;
import org.apache.skyline.admin.commons.model.query.ApiQuery;
import org.apache.skyline.admin.commons.model.query.ClusterQuery;
import org.apache.skyline.admin.commons.model.request.ApiConfigPluginRequest;
import org.apache.skyline.admin.commons.model.request.ApiRequest;
import org.apache.skyline.admin.commons.model.request.ConfigPluginInfo;
import org.apache.skyline.admin.commons.model.request.PageRequest;
import org.apache.skyline.admin.commons.model.vo.ApiVO;
import org.apache.skyline.admin.commons.model.vo.ClusterVO;
import org.apache.skyline.admin.server.commons.utils.PageCommonUtils;
import org.apache.skyline.admin.server.domain.model.ApiDomain;
import org.apache.skyline.admin.server.domain.model.ClusterDomain;
import org.apache.skyline.admin.server.domain.model.PluginVersionDomain;
import org.apache.skyline.admin.server.domain.query.ApiCombineQuery;
import org.apache.skyline.admin.server.domain.query.PluginVersionCombineQuery;
import org.apache.skyline.admin.server.domain.repository.ApiRepository;
import org.apache.skyline.admin.server.domain.repository.PluginVersionRepository;
import org.apache.skyline.admin.server.service.ApiService;
import org.apache.skyline.admin.server.service.ClusterService;
import org.apache.skyline.admin.server.support.api.configcenter.ApiConfigPublisher;
import org.apache.skyline.admin.server.support.api.configcenter.model.ApiGenerateDefinition;
import org.apache.skyline.admin.server.support.api.configcenter.model.ConfigOptions;
import org.apache.skyline.admin.server.support.codec.ObjectMapperCodec;
import org.apache.skyline.admin.server.support.mapper.ApiAssembler;
import org.apache.skyline.admin.server.support.mapper.ClusterAssembler;
import org.bravo.gaia.commons.base.PageBean;
import org.bravo.gaia.commons.util.AssertUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ApiServiceImpl implements ApiService {

    private ApiRepository apiRepository;

    private ApiConfigPublisher apiPublisher;

    private ClusterService clusterService;

    private ClusterAssembler clusterAssembler;

    private ApiAssembler apiAssembler;

    private ObjectMapperCodec objectMapperCodec;

    private PluginVersionRepository pluginVersionRepository;

    public Long create(ApiRequest apiRequest) {
        throwIfNotExistsCluster(apiRequest.getClusterId());

        throwIsDuplicate(apiRequest.getClusterId(),apiRequest.getMatchCondition());

        ApiDomain apiDomain = apiAssembler.convert(apiRequest);

        apiDomain.setStatus(ApiStatus.NEW);
        apiDomain.setVer(1L);

        return apiRepository.save(apiDomain);
    }


    @Override
    public Boolean update(Long id, ApiRequest apiRequest) {
        throwIfNotExistsCluster(apiRequest.getClusterId());

        ApiDomain apiDomain = apiRepository.findOneIfExists(id);

        if (apiRequest.getClusterId() == apiDomain.getClusterDomain()
                .getId() && !apiRequest.getMatchCondition().equals(apiDomain.getMatchCondition())) {

            throwIsDuplicate(apiRequest.getClusterId(),apiRequest.getMatchCondition());
        }

        Optional.ofNullable(apiDomain.getStatus())
                .filter(status -> status == ApiStatus.ENABLE)
                .ifPresent(status -> apiDomain.setStatus(ApiStatus.PENDING));

        BeanUtils.copyProperties(apiRequest, apiDomain);
        apiDomain.setVer(apiDomain.getVer()+1);

        return apiRepository.updateById(apiDomain);
    }

    @Override
    public PageBean<ApiVO> pageList(PageRequest<ApiQuery> pageRequest) {
        ApiQuery condition = pageRequest.getCondition();

        ApiCombineQuery combineQuery = ApiCombineQuery
                .builder()
                .isLoadCluster(condition.isLoadCluster())
                .clusterId(condition.getClusterId())
                .matchCondition(condition.getMatchCondition())
                .build();

        PageBean<ApiDomain> pageBean = apiRepository.pageQuery(combineQuery, pageRequest.getPageNo(), pageRequest.getPageSize());

        return PageCommonUtils.convert(pageBean, items -> convert(items));
    }


    @Override
    @Transactional
    public boolean deleteByIds(List<Long> ids) {
        Long id = ids.get(0);
        ApiDomain apiDomain = apiRepository.findOneIfExists(id);

        ConfigOptions configOptions = getConfigOptions(apiDomain.getClusterDomain().getId());

        boolean deleted = apiRepository.deleteByIds(ids);

        if (apiDomain.isNew()) {
            return deleted;
        }
        return apiPublisher.delete(configOptions, ids);
    }

    @Override
    public boolean configPlugin(ApiConfigPluginRequest configPluginRequest) {
        Long id = configPluginRequest.getId();

        ApiDomain apiDomain = apiRepository.findOneIfExists(id);
        apiDomain.setStatus(ApiStatus.PENDING);

        List<ConfigPluginInfo> pluginList = configPluginRequest.getPluginList();

        List<ApiGenerateDefinition.ApiPlugin> apiPluginList = this.mapPlugin(pluginList);

        apiDomain.setPlugins(JSON.toJSONString(apiPluginList));

        return apiRepository.updateById(apiDomain);
    }

    @Override
    @Transactional
    public boolean publish(List<Long> ids) {
        ApiCombineQuery combineQuery =
                ApiCombineQuery.builder()
                        .ids(ids)
                        .build();

        List<ApiDomain> apiDomainList = apiRepository.findList(combineQuery);

        AssertUtil.notEmpty(apiDomainList,"apiDomain is empty");

        Long clusterId = apiDomainList.get(0).getClusterDomain().getId();

        ConfigOptions configOptions = getConfigOptions(clusterId);

        List<ApiGenerateDefinition> apiList = this.generateApiDefinition(apiDomainList);

        boolean changed = apiPublisher.doPublish(configOptions, apiList);

        ApiDomain apiDomain = new ApiDomain();
        apiDomain.setStatus(ApiStatus.ENABLE);

        ApiCombineQuery apiCombineQuery = ApiCombineQuery.builder()
                .ids(ids)
                .build();

        apiRepository.update(apiDomain,apiCombineQuery);

        return changed;
    }

    private List<ApiVO> convert(List<ApiDomain> items) {
        return Optional.ofNullable(items)
                .orElseGet(ArrayList::new)
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    private List<ApiGenerateDefinition.ApiPlugin> mapPlugin(List<ConfigPluginInfo> configPluginInfoList) {
        Map<Long, ConfigPluginInfo> idConfig = configPluginInfoList.stream().collect(
                Collectors.toMap(ConfigPluginInfo::getPluginVerId, v -> v, (v1, v2) -> v2));

        PluginVersionCombineQuery combineQuery = PluginVersionCombineQuery.builder()
                .ids(idConfig.keySet().stream().collect(Collectors.toList()))
                .isLoadPlugin(true)
                .build();
        List<PluginVersionDomain> pluginVersionList = pluginVersionRepository.findList(combineQuery);

        return pluginVersionList
                .stream()
                .map(e->{
                    ConfigPluginInfo configPluginInfo = idConfig.get(e.getId());

                    ApiGenerateDefinition.ApiPlugin apiPlugin = new ApiGenerateDefinition.ApiPlugin();
                    apiPlugin.setStage(configPluginInfo.getStage());
                    apiPlugin.setStageName(configPluginInfo.getStageName());
                    apiPlugin.setStateSn(configPluginInfo.getStateSn());
                    apiPlugin.setJarUrl(e.getJarUrl());
                    apiPlugin.setSn(configPluginInfo.getSn());
                    apiPlugin.setConfig(configPluginInfo.getConfigParams());
                    apiPlugin.setClassDefine(e.getPluginDomain().getClassDefine());
                    apiPlugin.setVer(e.getVer());

                    return apiPlugin;

                }).collect(Collectors.toList());

    }

    private ApiVO convert(ApiDomain apiDomain) {
        ApiVO apiVO = apiAssembler.convertVO(apiDomain);

        ClusterVO clusterVO = clusterAssembler.convert(apiDomain.getClusterDomain());

        apiVO.setClusterVO(clusterVO);

        return apiVO;
    }

    private List<ApiGenerateDefinition> generateApiDefinition(List<ApiDomain> apiItems) {
        ClusterDomain clusterDomain = apiItems.get(0).getClusterDomain();

        return  apiItems.stream().map(apiDomain -> {
                    ApiGenerateDefinition api = new ApiGenerateDefinition();
                    api.setId(apiDomain.getId());
                    api.setClusterId(String.valueOf(apiDomain.getId()));
                    api.setApiVer(apiDomain.getVer());
                    api.setClusterDomain(clusterDomain.getDomain());
                    api.setMatchCondition(apiDomain.getMatchCondition());

                    String pluginString = apiDomain.getPlugins();

                    AssertUtil.isNotBlank(pluginString,"plugin is empty");
                    List<ApiGenerateDefinition.ApiPlugin> apiPlugins = objectMapperCodec.deserialize(pluginString.getBytes(), new TypeReference<List<ApiGenerateDefinition.ApiPlugin>>() {});

                    valid(apiPlugins);

                    api.setPlugins(apiPlugins);

                    return api;
                }).collect(Collectors.toList());
    }


    private void throwIfNotExistsCluster(Long clusterId) {
        ClusterQuery clusterQuery = new ClusterQuery();
        clusterQuery.setId(clusterId);
        ClusterVO clusterVO = clusterService.queryForOne(clusterQuery);

        AssertUtil.notNull(clusterVO,"cluster not found");
    }

    private void throwIsDuplicate(Long clusterId, String matchCondition) {
        ApiCombineQuery combineQuery = ApiCombineQuery
                .builder()
                .clusterId(clusterId)
                .matchCondition(matchCondition)
                .build();

        boolean isExists = this.apiRepository.isExists(combineQuery);

        AssertUtil.isTrue(!isExists,"api is exists");
    }

    public ConfigOptions getConfigOptions(Long clusterId) {
        ClusterQuery clusterQuery = new ClusterQuery();
        clusterQuery.setId(clusterId);

        ClusterVO clusterVO = clusterService.queryForOne(clusterQuery);

        ConfigOptions options = new ConfigOptions();
        options.setDomain(clusterVO.getDomain());
        options.setConfigItems(clusterVO.getConfigItem());
        options.setClusterName(clusterVO.getClusterName());
        options.setConfigSecret(clusterVO.getConfigSecret());
        options.setConfigUrl(clusterVO.getConfigUrl());
        options.setId(clusterVO.getId());
        options.setClusterName(clusterVO.getClusterName());
        options.setShareConfig(clusterVO.getConfigShare());

        return options;
    }

    private void valid(List<ApiGenerateDefinition.ApiPlugin> apiPlugins) {
        apiPlugins.stream()
                .forEach(item->{
                    AssertUtil.notNull(item,"plugin is null");
                    AssertUtil.isNotBlank(item.getStage(),"plugin stage is empty");
                    AssertUtil.isNotBlank(item.getStageName(),"plugin stageName is empty");
                    AssertUtil.isNotBlank(item.getJarUrl(),"plugin jarUrl is empty");
                    AssertUtil.isNotBlank(item.getClassDefine(),"plugin classDefine is empty");
                    AssertUtil.isNotBlank(item.getVer(),"plugin ver is null");
                    AssertUtil.notNull(item.getSn(),"plugin sn is null");
                });
    }
}
