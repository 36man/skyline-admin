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
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.skyline.admin.commons.enums.ClusterStatus;
import org.apache.skyline.admin.server.commons.constants.CopyIgnoreFields;
import org.apache.skyline.admin.server.dal.dao.ClusterDao;
import org.apache.skyline.admin.server.dal.dataobject.ClusterDO;
import org.apache.skyline.admin.server.domain.model.ClusterDomain;
import org.apache.skyline.admin.server.domain.repository.ClusterRepository;
import org.apache.skyline.admin.server.domain.query.ClusterCombineQuery;
import org.apache.skyline.admin.server.support.codec.ObjectMapperCodec;
import org.apache.skyline.admin.server.commons.utils.PageCommonUtils;
import org.bravo.gaia.commons.base.PageBean;
import org.bravo.gaia.commons.util.AssertUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class ClusterRepositoryImpl implements ClusterRepository {

    private ClusterDao clusterDao;

    private ObjectMapperCodec objectMapperCodec;


    @Override
    public boolean isExists(ClusterCombineQuery combineQuery) {
        return CollectionUtils.isNotEmpty(selectList(combineQuery));
    }

    @Override
    public boolean save(ClusterDomain clusterDomain) {
        return doExecute(clusterDomain, item -> clusterDao.insert(item) > 0);
    }

    @Override
    public ClusterDomain findOne(ClusterCombineQuery combineQuery) {
        return selectList(combineQuery).get(0);
    }

    @Override
    public ClusterDomain findOneIfExists(ClusterCombineQuery combineQuery) {
        List<ClusterDomain> result = selectList(combineQuery);

        AssertUtil.notEmpty(result, "Illegal argument");

        return result.get(0);
    }

    @Override
    public List<ClusterDomain> findList(ClusterCombineQuery combineQuery) {
        return selectList(combineQuery);
    }

    @Override
    public boolean update(ClusterCombineQuery combineQuery, ClusterDomain clusterDomain) {
        return doExecute(clusterDomain, item -> clusterDao.update(item, combineQuery.toQuery()) > 0);
    }

    @Override
    public boolean deleteById(Long id) {
        return clusterDao.deleteById(id) > 0;
    }

    @Override
    public PageBean<ClusterDomain> pageQuery(ClusterCombineQuery combineQuery, Integer pageNo, Integer pageSize) {
        Page<ClusterDO> page = new Page<>();
        page.setCurrent(pageNo);
        page.setSize(pageSize);

        Page<ClusterDO> result = clusterDao.selectPage(page, combineQuery.toQuery());

        return PageCommonUtils.convert(result, items -> convert(items));
    }

    public List<ClusterDomain> selectList(ClusterCombineQuery combineQuery) {
        LambdaQueryWrapper<ClusterDO> condition = combineQuery.toQuery();
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

        BeanUtils.copyProperties(clusterDO, clusterDomain,
                CopyIgnoreFields.CONFIG_ITEM,
                CopyIgnoreFields.STATUS);

        Optional.ofNullable(clusterDO.getConfigItem())
                .filter(StringUtils::isNoneBlank)
                .ifPresent(item -> clusterDomain.setConfigItem(objectMapperCodec.deserialize(item, Map.class)));

        Optional.ofNullable(clusterDO.getStatus())
                .filter(Objects::nonNull)
                .ifPresent(code -> {
                    ClusterStatus status = ClusterStatus.getEnumByCode(code);
                    clusterDomain.setStatus(status);
                });

        return clusterDomain;
    }

    private ClusterDO convert(ClusterDomain clusterDomain){
        if (null == clusterDomain) {
            return null;
        }
        ClusterDO clusterDO = new ClusterDO();

        BeanUtils.copyProperties(clusterDomain, clusterDO,CopyIgnoreFields.CONFIG_ITEM,
                CopyIgnoreFields.STATUS);

        Optional.ofNullable(clusterDomain.getConfigItem())
                .filter(MapUtils::isNotEmpty)
                .ifPresent(item-> clusterDO.setConfigItem(objectMapperCodec.serialize(item)));

        Optional.ofNullable(clusterDomain.getStatus())
                .filter(Objects::nonNull)
                .ifPresent(status-> clusterDO.setStatus(status.getCode()));

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
