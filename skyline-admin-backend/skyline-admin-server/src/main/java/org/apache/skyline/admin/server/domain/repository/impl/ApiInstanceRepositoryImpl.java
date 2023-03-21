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
import org.apache.skyline.admin.server.commons.utils.PageCommonUtils;
import org.apache.skyline.admin.server.dal.dao.ApiInstanceDao;
import org.apache.skyline.admin.server.dal.dataobject.ApiInstanceDO;
import org.apache.skyline.admin.server.domain.model.ApiInstanceDomain;
import org.apache.skyline.admin.server.domain.query.ApiInstanceCombineQuery;
import org.apache.skyline.admin.server.domain.repository.ApiInstanceRepository;
import org.bravo.gaia.commons.base.PageBean;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ApiInstanceRepositoryImpl implements ApiInstanceRepository {

    @Autowired
    private ApiInstanceDao apiInstanceDao;

    @Override
    public Long save(ApiInstanceDomain apiInstanceDomain) {
        ApiInstanceDO apiInstance = convert(apiInstanceDomain);

        apiInstanceDao.insert(apiInstance);

        return apiInstance.getId();
    }

    @Override
    public boolean updateById(ApiInstanceDomain apiInstanceDomain) {
        return apiInstanceDao.updateById(convert(apiInstanceDomain)) > 0;
    }

    @Override
    public PageBean<ApiInstanceDomain> pageQuery(ApiInstanceCombineQuery combineQuery,
                                                 Integer pageNo, Integer pageSize) {


        Page<ApiInstanceDO> page = new Page<>();
        page.setCurrent(pageNo);
        page.setSize(pageSize);

        Page<ApiInstanceDO> result = apiInstanceDao.selectPage(page, combineQuery.toQuery());

        return PageCommonUtils.convert(result, items -> convert(items));
    }

    private List<ApiInstanceDomain> convert(List<ApiInstanceDO> items){
        return Optional.ofNullable(items)
                .orElseGet(ArrayList::new)
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    private ApiInstanceDomain convert(ApiInstanceDO item) {
        ApiInstanceDomain instanceDomain = new ApiInstanceDomain();

        BeanUtils.copyProperties(item,instanceDomain);

        return instanceDomain;
    }

    private ApiInstanceDO convert(ApiInstanceDomain apiInstanceDomain) {
        ApiInstanceDO apiInstanceDO = new ApiInstanceDO();

        BeanUtils.copyProperties(apiInstanceDomain, apiInstanceDO);

        return apiInstanceDO;
    }
}
