package org.apache.skyline.admin.server.domain.repository.impl;

import com.alibaba.fastjson2.JSON;
import org.apache.skyline.admin.server.dal.dao.PluginVersionDao;
import org.apache.skyline.admin.server.dal.dataobject.PluginVersionDO;
import org.apache.skyline.admin.server.domain.model.PluginVersionDomain;
import org.apache.skyline.admin.server.domain.query.PluginVersionCombineQuery;
import org.apache.skyline.admin.server.domain.repository.PluginVersionRepository;
import org.bravo.gaia.commons.util.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.function.Function;

/**
 * @author hejianbing
 * @version @Id: PluginVersionRepositoryImpl.java, v 0.1 2022年12月25日 00:16 hejianbing Exp $
 */
@Repository
public class PluginVersionRepositoryImpl implements PluginVersionRepository {

    @Autowired
    private PluginVersionDao pluginVersionDao;

    @Override
    public Long create(PluginVersionDomain pluginVersionDomain) {
        return doExecute(pluginVersionDomain,item->{

            pluginVersionDao.insert(item);

            return item.getId();
        });
    }

    @Override
    public boolean delete(PluginVersionCombineQuery combineQuery) {
        int rows = pluginVersionDao.delete(combineQuery.toQuery());

        return rows > 0;
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

        return pluginVersionDO;
    }

    private <T> T doExecute(PluginVersionDomain domain, Function<PluginVersionDO, T> handler) {
        AssertUtil.notNull(domain, "domain is null");

        PluginVersionDO versionDO = convert(domain);

        return handler.apply(versionDO);
    }
}
