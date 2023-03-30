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

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.apache.skyline.admin.commons.model.query.PluginQuery;
import org.apache.skyline.admin.commons.model.query.PluginVersionQuery;
import org.apache.skyline.admin.commons.model.request.PageRequest;
import org.apache.skyline.admin.commons.model.vo.PluginVO;
import org.apache.skyline.admin.commons.model.vo.PluginVersionVO;
import org.apache.skyline.admin.server.commons.utils.PageCommonUtils;
import org.apache.skyline.admin.server.domain.model.PluginDomain;
import org.apache.skyline.admin.server.domain.model.PluginVersionDomain;
import org.apache.skyline.admin.server.domain.query.PluginVersionCombineQuery;
import org.apache.skyline.admin.server.domain.repository.PluginVersionRepository;
import org.apache.skyline.admin.server.service.PluginService;
import org.apache.skyline.admin.server.service.PluginVersionService;
import org.bravo.gaia.commons.base.PageBean;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PluginVersionServiceImpl implements PluginVersionService {

    private final PluginService pluginService;

    private final PluginVersionRepository pluginVersionRepository;


    @Override
    public PageBean<PluginVersionVO> search(PageRequest<String> pageRequest) {
        String [] condition = pageRequest.getCondition().split(":");
        PluginQuery pluginQuery = new PluginQuery();

        pluginQuery.setPluginName(condition[0]);
        pluginQuery.setMaintainer(condition[0]);
        pluginQuery.setPluginName(condition[0]);

        String ver = "";
        if (condition.length == 2) {
            ver = condition[1];
        }

        List<PluginVO> pluginItems = pluginService.matchQuery(pluginQuery);

        Map<Long, PluginVO> idToPlugin =  Optional.ofNullable(pluginItems)
                .orElseGet(ArrayList::new)
                .stream()
                .collect(Collectors.toMap(PluginVO::getId, v -> v));

        PluginVersionCombineQuery combineQuery =
                PluginVersionCombineQuery.builder()
                        .ver(ver)
                        .active(true)
                        .pluginIdList(idToPlugin.keySet().stream().collect(Collectors.toList()))
                        .build();

        PageBean<PluginVersionDomain> pageBean = pluginVersionRepository.pageQuery(combineQuery, pageRequest.getPageNo(),
                pageRequest.getPageSize());

        return PageCommonUtils.convert(pageBean,
                clusterDomains -> convert(clusterDomains,idToPlugin));    }

    @Override
    public PageBean<PluginVersionVO> pageList(PageRequest<PluginVersionQuery> pageRequest) {
        PluginVersionCombineQuery combineQuery = this.toQuery(pageRequest.getCondition());

        PageBean<PluginVersionDomain> pageBean = pluginVersionRepository.pageQuery(combineQuery, pageRequest.getPageNo(),
                pageRequest.getPageSize());

        return PageCommonUtils.convert(pageBean,
                clusterDomains -> convert(clusterDomains));
    }

    @Override
    public boolean deleted(Long id) {
        PluginVersionCombineQuery combineQuery = PluginVersionCombineQuery.builder()
                .id(id)
                .build();
        return pluginVersionRepository.delete(combineQuery);
    }

    @Override
    public boolean active(Long id,boolean active) {
        return pluginVersionRepository.active(id, active);
    }

    private List<PluginVersionVO> convert(List<PluginVersionDomain> items,Map<Long,PluginVO> pluginMap) {
        return Optional.ofNullable(items)
                .orElseGet(ArrayList::new)
                .stream()
                .map(e->{
                    PluginDomain pluginDomain = e.getPluginDomain();

                    PluginVersionVO versionVO = convert(e);

                    PluginVO pluginVO = pluginMap.get(pluginDomain.getId());

                    versionVO.setPluginVO(pluginVO);

                    return versionVO;
                }).collect(Collectors.toList());
    }

    private PluginVersionVO convert(PluginVersionDomain pluginVersionDomain) {
        PluginVersionVO pluginVersionVO = new PluginVersionVO();
        BeanUtils.copyProperties(pluginVersionDomain, pluginVersionVO);

        return pluginVersionVO;
    }


    private List<PluginVersionVO> convert(List<PluginVersionDomain> items) {
        return Optional.ofNullable(items)
                .orElseGet(ArrayList::new)
                .stream()
                .map(e->{
                    PluginVersionVO versionVO = convert(e);

                    PluginVO pluginVO = new PluginVO();

                    BeanUtils.copyProperties(e.getPluginDomain(), pluginVO);

                    versionVO.setPluginVO(pluginVO);

                    return versionVO;
                }).collect(Collectors.toList());
    }


    private PluginVersionCombineQuery toQuery(PluginVersionQuery versionQuery) {
        return PluginVersionCombineQuery.builder()
                .pluginIdList(Lists.newArrayList(versionQuery.getPluginId()))
                .isLoadPlugin(versionQuery.getIsLoadPlugin() != null ? true : false)
                .ver(versionQuery.getVer())
                .build();
    }

}
