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

package org.apache.skyline.admin.server.domain.repository.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.skyline.admin.commons.enums.ApiStatus;
import org.apache.skyline.admin.server.commons.utils.PageCommonUtils;
import org.apache.skyline.admin.server.dal.dao.ApiDao;
import org.apache.skyline.admin.server.dal.dataobject.ApiDO;
import org.apache.skyline.admin.server.domain.model.ApiDomain;
import org.apache.skyline.admin.server.domain.model.ClusterDomain;
import org.apache.skyline.admin.server.domain.query.ApiCombineQuery;
import org.apache.skyline.admin.server.domain.query.ClusterCombineQuery;
import org.apache.skyline.admin.server.domain.repository.ApiRepository;
import org.apache.skyline.admin.server.domain.repository.ClusterRepository;
import org.bravo.gaia.commons.base.PageBean;
import org.bravo.gaia.commons.util.AssertUtil;
import org.bravo.gaia.commons.util.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ApiRepositoryImpl implements ApiRepository {

    @Autowired
    private ApiDao apiDao;

    @Autowired
    private ClusterRepository clusterRepository;



    @Override
    public Long save(ApiDomain apiDomain) {
        ApiDO apiDO = convert(apiDomain);

        apiDao.insert(apiDO);

        return apiDO.getId();
    }

    @Override
    public boolean updateById(ApiDomain apiDomain) {
        return apiDao.updateById(convert(apiDomain)) > 0;
    }

    @Override
    public PageBean<ApiDomain> pageQuery(ApiCombineQuery combineQuery,
                                         Integer pageNo, Integer pageSize) {


        Page<ApiDO> page = new Page<>();
        page.setCurrent(pageNo);
        page.setSize(pageSize);

        Page<ApiDO> result = apiDao.selectPage(page, combineQuery.toQuery());

        return PageCommonUtils.convert(result, items -> convert(items,combineQuery));
    }

    @Override
    public List<ApiDomain> findList(ApiCombineQuery combineQuery) {

        List<ApiDO> items = apiDao.selectList(combineQuery.toQuery());

        return convert(items,combineQuery);
    }

    @Override
    public ApiDomain findOneByIdIfExists(Long id) {
        ApiDO apiInstanceDO = apiDao.selectById(id);

        AssertUtil.notNull(apiInstanceDO, "api instance not found by id: " + id);

        return convert(apiInstanceDO);
    }

    @Override
    public boolean deleteById(Long id) {
        return apiDao.deleteById(id) > 0;
    }

    @Override
    public boolean deleteByIds(List<Long> ids) {
        return apiDao.deleteBatchIds(ids) > 0;
    }

    @Override
    public boolean isExists(ApiCombineQuery combineQuery) {
        return CollectionUtils.isNotEmpty(apiDao.selectList(combineQuery.toQuery()));
    }

    private List<ApiDomain> convert(List<ApiDO> items,ApiCombineQuery combineQuery){
        List<ApiDomain> apiDomainList = Optional.ofNullable(items).orElseGet(ArrayList::new).stream()
                .map(this::convert).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(apiDomainList) && combineQuery.isLoadCluster()) {
            Long clusterId = combineQuery.getClusterId();

            ClusterCombineQuery clusterCombineQuery = ClusterCombineQuery.builder()
                    .id(clusterId)
                    .build();
            ClusterDomain clusterDomain = clusterRepository.findOne(clusterCombineQuery);

            apiDomainList.stream()
                    .forEach(item->{
                        item.setClusterDomain(clusterDomain);
                    });
        }

        return apiDomainList;
    }

    private ApiDomain convert(ApiDO item) {
        ApiDomain apiDomain = new ApiDomain();

        BeanUtils.copyProperties(item,apiDomain);

        ClusterDomain clusterDomain = new ClusterDomain();
        clusterDomain.setId(item.getClusterId());

        apiDomain.setClusterDomain(clusterDomain);
        apiDomain.setStatus(ApiStatus.getApiStatus(item.getStatus()));
        return apiDomain;
    }

    private ApiDO convert(ApiDomain apiDomain) {
        ApiDO apiDO = new ApiDO();

        BeanUtils.copyProperties(apiDomain, apiDO);

        apiDO.setClusterId(apiDomain.getClusterDomain().getId());

        apiDO.setStatus(apiDomain.getStatus().getCode());

        return apiDO;
    }
}
