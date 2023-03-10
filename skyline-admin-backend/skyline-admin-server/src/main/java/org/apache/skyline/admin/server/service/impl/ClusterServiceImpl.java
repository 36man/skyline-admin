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

import org.apache.skyline.admin.commons.model.query.ClusterQuery;
import org.apache.skyline.admin.commons.model.request.ClusterRequest;
import org.apache.skyline.admin.commons.model.request.PageRequest;
import org.apache.skyline.admin.commons.model.vo.ClusterVO;
import org.apache.skyline.admin.server.domain.entities.ClusterDomain;
import org.apache.skyline.admin.server.domain.repository.ClusterRepository;
import org.apache.skyline.admin.server.model.query.ClusterQueryCondition;
import org.apache.skyline.admin.server.service.ClusterService;
import org.apache.skyline.admin.server.utils.PageCommonUtils;
import org.bravo.gaia.commons.base.PageBean;
import org.bravo.gaia.commons.util.AssertUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClusterServiceImpl implements ClusterService {

    @Autowired
    private ClusterRepository clusterRepository;

    public boolean create(ClusterRequest request){
        ClusterDomain clusterDomain = this.convert(request);

        this.throwIfExists(ClusterQueryCondition.builder().domain(request.getDomain()).build(), "domain is exists");

        return clusterRepository.save(clusterDomain);
    }

    @Override
    public boolean update(Long id, ClusterRequest request) {
        ClusterQueryCondition queryCluster = ClusterQueryCondition.builder()
                .id(id)
                .build();

        ClusterDomain clusterDomain = clusterRepository.findOneIfExists(queryCluster);

        BeanUtils.copyProperties(request, clusterDomain);

        return clusterRepository.update(queryCluster,clusterDomain);
    }



    @Override
    public boolean delete(Long id) {
        return clusterRepository.deleteById(id);
    }

    @Override
    public PageBean<ClusterVO> pageList(PageRequest<ClusterQuery> pageRequest) {
        ClusterQuery condition = pageRequest.getCondition();

        ClusterQueryCondition queryCondition = ClusterQueryCondition.builder()
                .domain(condition.getDomain())
                .clusterName(condition.getClusterName())
                .businessName(condition.getBusinessName())
                .build();

        PageBean<ClusterDomain> pageBean = clusterRepository.pageQuery(queryCondition, pageRequest.getPageNo(), pageRequest.getPageSize());

        return PageCommonUtils.convert(pageBean, clusterDomains -> convert(clusterDomains));
    }

    @Override
    public boolean applyCluster(Long id) {
        return false;
    }

    private ClusterDomain convert(ClusterRequest request) {
        ClusterDomain clusterDomain = new ClusterDomain();

        BeanUtils.copyProperties(request, clusterDomain);

        return clusterDomain;
    }

    private void throwIfExists(ClusterQueryCondition queryClusterCondition, String message) {
        boolean isExists = clusterRepository.isExists(queryClusterCondition);
        AssertUtil.isTrue(isExists, message);

    }

    private List<ClusterVO> convert(List<ClusterDomain> clusterList){
        return Optional.ofNullable(clusterList)
                .orElseGet(ArrayList::new)
                .stream()
                .map(item->convert(item))
                .collect(Collectors.toList());
    }

    private ClusterVO convert(ClusterDomain clusterDomain) {
        ClusterVO clusterVO = new ClusterVO();

        BeanUtils.copyProperties(clusterDomain,clusterVO);

        return clusterVO;
    }
}
