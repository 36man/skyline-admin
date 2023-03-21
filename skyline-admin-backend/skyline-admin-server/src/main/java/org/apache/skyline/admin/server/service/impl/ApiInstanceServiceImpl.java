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

import org.apache.skyline.admin.commons.model.query.ApiInstanceQuery;
import org.apache.skyline.admin.commons.model.request.ApiInstanceRequest;
import org.apache.skyline.admin.commons.model.request.PageRequest;
import org.apache.skyline.admin.commons.model.vo.ApiInstanceVO;
import org.apache.skyline.admin.server.commons.utils.PageCommonUtils;
import org.apache.skyline.admin.server.domain.model.ApiInstanceDomain;
import org.apache.skyline.admin.server.domain.query.ApiInstanceCombineQuery;
import org.apache.skyline.admin.server.domain.repository.ApiInstanceRepository;
import org.apache.skyline.admin.server.service.ApiInstanceService;
import org.bravo.gaia.commons.base.PageBean;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ApiInstanceServiceImpl implements ApiInstanceService {

    @Autowired
    private ApiInstanceRepository apiInstanceRepository;

    @Override
    public Long create(ApiInstanceRequest apiInstanceRequest) {
        ApiInstanceDomain apiInstanceDomain = new ApiInstanceDomain();

        BeanUtils.copyProperties(apiInstanceRequest, apiInstanceDomain);

        return apiInstanceRepository.save(apiInstanceDomain);
    }

    @Override
    public Boolean update(Long id, ApiInstanceRequest apiInstanceRequest) {
        ApiInstanceDomain apiInstanceDomain = new ApiInstanceDomain();

        BeanUtils.copyProperties(apiInstanceRequest, apiInstanceDomain);

        apiInstanceDomain.setId(id);

        return apiInstanceRepository.updateById(apiInstanceDomain);
    }

    @Override
    public PageBean<ApiInstanceVO> pageList(PageRequest<ApiInstanceQuery> pageRequest) {

        ApiInstanceCombineQuery combineQuery = ApiInstanceCombineQuery
                .builder()
                .clusterId(pageRequest.getCondition().getClusterId())
                .matchPath(pageRequest.getCondition().getMatchPath())
                .build();

        PageBean<ApiInstanceDomain> pageBean = apiInstanceRepository.pageQuery(combineQuery, pageRequest.getPageNo(), pageRequest.getPageSize());

        return PageCommonUtils.convert(pageBean, items -> convert(items));

    }

    private List<ApiInstanceVO> convert(List<ApiInstanceDomain> items) {
        return Optional.ofNullable(items)
                .orElseGet(ArrayList::new)
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    private ApiInstanceVO convert(ApiInstanceDomain apiInstanceDomain) {
        ApiInstanceVO vo = new ApiInstanceVO();

        BeanUtils.copyProperties(apiInstanceDomain,vo);

        return vo;
    }
}
