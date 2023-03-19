package org.apache.skyline.admin.server.domain.repository.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.skyline.admin.server.commons.constants.CopyIgnoreFields;
import org.apache.skyline.admin.server.commons.utils.PageCommonUtils;
import org.apache.skyline.admin.server.dal.dao.PluginVersionDao;
import org.apache.skyline.admin.server.dal.dataobject.PluginVersionDO;
import org.apache.skyline.admin.server.domain.model.PluginDomain;
import org.apache.skyline.admin.server.domain.model.PluginVersionDomain;
import org.apache.skyline.admin.server.domain.query.PluginVersionCombineQuery;
import org.apache.skyline.admin.server.domain.repository.PluginVersionRepository;
import org.apache.skyline.admin.server.support.codec.ObjectMapperCodec;
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

/**
 * @author hejianbing
 * @version @Id: PluginVersionRepositoryImpl.java, v 0.1 2022年12月25日 00:16 hejianbing Exp $
 */
@Repository
public class PluginVersionRepositoryImpl implements PluginVersionRepository {

    @Autowired
    private PluginVersionDao pluginVersionDao;

    @Autowired
    private ObjectMapperCodec objectMapperCodec;

    @Override
    public Long create(PluginVersionDomain pluginversiondomain) {
        return doExecute(pluginversiondomain,item->{

            pluginVersionDao.insert(item);

            return item.getId();
        });
    }

    @Override
    public boolean delete(PluginVersionCombineQuery combineQuery) {
        int rows = pluginVersionDao.delete(combineQuery.toQuery());

        return rows > 0;
    }

    @Override
    public boolean active(PluginVersionCombineQuery combineQuery) {

        PluginVersionDO versionDO = new PluginVersionDO();
        versionDO.setActive(true);

        return pluginVersionDao.update(versionDO, combineQuery.toQuery()) > 0;

    }

    @Override
    public boolean disable(PluginVersionCombineQuery combineQuery) {
        PluginVersionDO versionDO = new PluginVersionDO();
        versionDO.setActive(false);

        return pluginVersionDao.update(versionDO, combineQuery.toQuery()) > 0;    }

    @Override
    public PluginVersionDomain findOne(PluginVersionCombineQuery combineQuery) {
        return selectList(combineQuery).get(0);
    }

    @Override
    public Boolean update(PluginVersionCombineQuery combineQuery,
                          PluginVersionDomain pluginVersionDomain) {
        return doExecute(pluginVersionDomain,item-> pluginVersionDao.update(item, combineQuery.toQuery()) > 0);
    }

    @Override
    public PageBean<PluginVersionDomain> pageQuery(PluginVersionCombineQuery combineQuery,
                                                   Integer pageNo, Integer pageSize) {


        Page<PluginVersionDO> page = new Page<>();
        page.setCurrent(pageNo);
        page.setSize(pageSize);

        Page<PluginVersionDO> result = pluginVersionDao.selectPage(page, combineQuery.toQuery());

        return PageCommonUtils.convert(result, items -> convert(items));

    }

    private List<PluginVersionDomain> selectList(PluginVersionCombineQuery combineQuery) {

        List<PluginVersionDO> items = pluginVersionDao.selectList(
                combineQuery.toQuery());

        List<PluginVersionDomain> result = this.convert(items);

        return result;
    }

    private List<PluginVersionDomain> convert(List<PluginVersionDO> items) {
        return Optional.ofNullable(items)
                .orElseGet(ArrayList::new)
                .stream()
                .map(item-> convert(item)).collect(Collectors.toList());
    }

    private PluginVersionDomain convert(PluginVersionDO pluginVersionDO) {
        if (pluginVersionDO == null) {
            return null;
        }

        PluginVersionDomain pluginVersionDomain = new PluginVersionDomain();

        BeanUtils.copyProperties(pluginVersionDO, pluginVersionDomain, CopyIgnoreFields.FEATURES);

        PluginDomain pluginDomain = new PluginDomain();
        pluginDomain.setId(pluginVersionDO.getPluginId());

        pluginVersionDomain.setPluginDomain(pluginDomain);

        Optional.ofNullable(pluginVersionDO.getFeatures())
                .filter(StringUtils::isNoneBlank)
                .ifPresent(e-> pluginVersionDomain.setFeatures(objectMapperCodec.deserialize(e, List.class)));

        return pluginVersionDomain;
    }

    private PluginVersionDO convert(PluginVersionDomain pluginVersionDomain) {
        PluginVersionDO pluginVersionDO = new PluginVersionDO();
        pluginVersionDO.setVer(pluginVersionDomain.getVer());
        pluginVersionDO.setFeatures(JSON.toJSONString(pluginVersionDomain.getFeatures()));

        pluginVersionDO.setPluginId(pluginVersionDomain.getPluginDomain().getId());
        pluginVersionDO.setPageContent(pluginVersionDomain.getPageContent());
        pluginVersionDO.setDeleted(false);
        pluginVersionDO.setActive(true);
        pluginVersionDO.setSize(pluginVersionDomain.getSize());
        pluginVersionDO.setJarUrl(pluginVersionDomain.getJarUrl());
        pluginVersionDO.setFileKey(pluginVersionDomain.getFileKey());
        pluginVersionDO.setTypeMeta(pluginVersionDomain.getTypeMeta());

        return pluginVersionDO;
    }

    private <T> T doExecute(PluginVersionDomain domain, Function<PluginVersionDO, T> handler) {
        AssertUtil.notNull(domain, "domain is null");

        PluginVersionDO versionDO = convert(domain);

        return handler.apply(versionDO);
    }
}
