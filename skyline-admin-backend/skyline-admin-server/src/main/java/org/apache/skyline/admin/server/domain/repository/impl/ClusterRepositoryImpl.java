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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.skyline.admin.server.dal.dao.ClusterDao;
import org.apache.skyline.admin.server.dal.dataobject.ClusterDO;
import org.apache.skyline.admin.server.domain.entities.ClusterDomain;
import org.apache.skyline.admin.server.domain.repository.ClusterRepository;
import org.apache.skyline.admin.server.model.query.ClusterQueryCondition;
import org.apache.skyline.admin.server.utils.PageCommonUtils;
import org.bravo.gaia.commons.base.PageBean;
import org.bravo.gaia.commons.util.AssertUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class ClusterRepositoryImpl implements ClusterRepository {

    @Autowired
    private ClusterDao clusterDao;

    @Override
    public boolean isExists(ClusterQueryCondition queryCondition) {
        return CollectionUtils.isNotEmpty(selectList(queryCondition));
    }

    @Override
    public boolean save(ClusterDomain clusterDomain) {
        return doExecute(clusterDomain, item -> clusterDao.insert(item) > 0);
    }

    @Override
    public ClusterDomain findOne(ClusterQueryCondition queryCondition) {
        return selectList(queryCondition).get(0);
    }

    @Override
    public ClusterDomain findOneIfExists(ClusterQueryCondition queryCondition) {
        List<ClusterDomain> result = selectList(queryCondition);

        AssertUtil.notEmpty(result, "illegal Argument");

        return result.get(0);
    }

    @Override
    public List<ClusterDomain> findList(ClusterQueryCondition queryCondition) {
        return selectList(queryCondition);
    }

    @Override
    public boolean update(ClusterQueryCondition queryCluster, ClusterDomain clusterDomain) {
        return doExecute(clusterDomain, item -> clusterDao.update(item, queryCluster.buildQuery()) > 0);
    }

    @Override
    public boolean deleteById(Long id) {
        return clusterDao.deleteById(id) > 0;
    }

    @Override
    public PageBean<ClusterDomain> pageQuery(ClusterQueryCondition queryCondition, Integer pageNo, Integer pageSize) {
        Page<ClusterDO> page = new Page<>();
        page.setCurrent(pageNo);
        page.setSize(pageSize);

        Page<ClusterDO> result = clusterDao.selectPage(page, queryCondition.buildQuery());

        return PageCommonUtils.convert(result, items -> convert(items));
    }

    public List<ClusterDomain> selectList(ClusterQueryCondition queryCluster) {
        LambdaQueryWrapper<ClusterDO> condition = queryCluster.buildQuery();
        List<ClusterDO> cluserList = clusterDao.selectList(condition);

        return Optional.ofNullable(cluserList)
                .orElseGet(ArrayList::new)
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    private ClusterDomain convert(ClusterDO clusterDO){
        if (null == clusterDO) {
            return null;
        }
        ClusterDomain clusterDomain = new ClusterDomain();

        BeanUtils.copyProperties(clusterDO, clusterDomain);

        return clusterDomain;
    }

    private ClusterDO convert(ClusterDomain clusterDomain){
        if (null == clusterDomain) {
            return null;
        }
        ClusterDO clusterDO = new ClusterDO();

        BeanUtils.copyProperties(clusterDomain, clusterDO);

        return clusterDO;
    }

    private List<ClusterDomain> convert(List<ClusterDO> result) {
        return Optional.ofNullable(result)
                .orElseGet(ArrayList::new)
                .stream().map(e->convert(e))
                .collect(Collectors.toList());
    }

    private <T> T doExecute(ClusterDomain clusterDomain, Function<ClusterDO, T> handle) {
        AssertUtil.notNull(clusterDomain, "clusterDomain is null");


        ClusterDO clusterDO = convert(clusterDomain);

        AssertUtil.notNull(clusterDO, "clusterDO is null");

        return handle.apply(clusterDO);
    }

}