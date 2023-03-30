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
import org.apache.skyline.admin.server.domain.query.ApiCombineQuery;
import org.apache.skyline.admin.server.domain.repository.ApiRepository;
import org.apache.skyline.admin.server.service.ApiService;
import org.apache.skyline.admin.server.service.ClusterService;
import org.apache.skyline.admin.server.support.api.notify.ApiConfigPublisher;
import org.apache.skyline.admin.server.support.api.notify.model.API;
import org.apache.skyline.admin.server.support.api.notify.model.ConfigOptions;
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

    public Long create(ApiRequest apiRequest) {
        throwIsNotExistsCluster(apiRequest.getClusterId());

        throwIsDuplicate(apiRequest.getClusterId(),apiRequest.getMatchCondition());

        ApiDomain apiDomain = apiAssembler.convert(apiRequest);

        apiDomain.setStatus(ApiStatus.NEW);
        apiDomain.setVer(1L);

        return apiRepository.save(apiDomain);
    }


    @Override
    public Boolean update(Long id, ApiRequest apiRequest) {
        throwIsNotExistsCluster(apiRequest.getClusterId());

        ApiDomain apiDomain = apiRepository.findOneByIdIfExists(id);

        if(apiRequest.getClusterId()==apiDomain.getClusterDomain().getId()
            && !apiRequest.getMatchCondition().equals(apiDomain.getMatchCondition())) {

            throwIsDuplicate(apiRequest.getClusterId(),apiRequest.getMatchCondition());
        }

        BeanUtils.copyProperties(apiRequest, apiDomain);

        apiDomain.setId(id);


        apiDomain.setVer(apiDomain.getVer()+1);

        return apiRepository.updateById(apiDomain);
    }

    @Override
    public PageBean<ApiVO> pageList(PageRequest<ApiQuery> pageRequest) {
        ApiCombineQuery combineQuery = ApiCombineQuery
                .builder()
                .clusterId(pageRequest.getCondition().getClusterId())
                .matchCondition(pageRequest.getCondition().getMatchCondition())
                .build();

        PageBean<ApiDomain> pageBean = apiRepository.pageQuery(combineQuery, pageRequest.getPageNo(), pageRequest.getPageSize());

        return PageCommonUtils.convert(pageBean, items -> convert(items));
    }


    @Override
    @Transactional
    public boolean deleteByIds(List<Long> ids) {
        Long id = ids.get(0);
        ApiDomain apiDomain = apiRepository.findOneByIdIfExists(id);

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

        ApiDomain apiDomain = apiRepository.findOneByIdIfExists(id);
        apiDomain.setStatus(ApiStatus.IN_ENABLE);

        apiDomain.setPlugins(JSON.toJSONString(configPluginRequest.getPluginList()));

        return apiRepository.updateById(apiDomain);
    }

    @Override
    public boolean publish(List<Long> ids) {
        ApiCombineQuery combineQuery =
                ApiCombineQuery.builder()
                        .ids(ids)
                        .build();

        List<ApiDomain> apiDomainList = apiRepository.findList(combineQuery);

        AssertUtil.notEmpty(apiDomainList,"apiDomain is empty");

        Long clusterId = apiDomainList.get(0).getClusterDomain().getId();

        ConfigOptions configOptions = getConfigOptions(clusterId);

        List<API> apiList = this.assembleAPI(apiDomainList);

        return apiPublisher.change(configOptions, apiList);

    }

    private List<ApiVO> convert(List<ApiDomain> items) {
        return Optional.ofNullable(items)
                .orElseGet(ArrayList::new)
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    private ApiVO convert(ApiDomain apiDomain) {
        ApiVO vo = new ApiVO();
        BeanUtils.copyProperties(apiDomain,vo);

        ClusterVO clusterVO = clusterAssembler.convert(apiDomain.getClusterDomain());

        vo.setClusterVO(clusterVO);

        return vo;
    }

    private List<API> assembleAPI(List<ApiDomain> apiItems) {
        ClusterDomain clusterDomain = apiItems.get(0).getClusterDomain();

        return  apiItems.stream().map(apiDomain -> {
                    API api = new API();
                    api.setId(apiDomain.getId());
                    api.setClusterId(String.valueOf(apiDomain.getId()));
                    api.setApiVer(apiDomain.getVer());
                    api.setClusterDomain(clusterDomain.getDomain());
                    api.setMatchCondition(apiDomain.getMatchCondition());

                    String pluginString = apiDomain.getPlugins();

                    AssertUtil.isNotBlank(pluginString,"plugin is empty");
                    List<ConfigPluginInfo> configPluginInfoList = objectMapperCodec.deserialize(pluginString.getBytes(), new TypeReference<List<ConfigPluginInfo>>() {});

                    List<API.Plugin> plugins = configPluginInfoList.stream().map(item -> {

                        valid(item);

                        API.Plugin plugin = new API.Plugin();

                        BeanUtils.copyProperties(item,plugin);

                        return plugin;
                    }).collect(Collectors.toList());

                    api.setPlugins(plugins);

                    return api;
                }).collect(Collectors.toList());
    }


    private void throwIsNotExistsCluster(Long clusterId) {
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

        return options;
    }

    private void valid(ConfigPluginInfo item) {
        AssertUtil.notNull(item,"plugin is null");
        AssertUtil.isNotBlank(item.getStage(),"plugin stage is empty");
        AssertUtil.isNotBlank(item.getStageName(),"plugin stageName is empty");
        AssertUtil.isNotBlank(item.getJarUrl(),"plugin jarUrl is empty");
        AssertUtil.isNotBlank(item.getClassDefine(),"plugin classDefine is empty");
        AssertUtil.isNotBlank(item.getVer(),"plugin ver is null");
        AssertUtil.notNull(item.getSn(),"plugin sn is null");
    }
}
