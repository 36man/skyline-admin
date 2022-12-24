package org.apache.skyline.admin.server.domain.repository.impl;

import org.apache.skyline.admin.server.dal.dao.PluginVersionDao;
import org.apache.skyline.admin.server.dal.dataobject.PluginVersionDO;
import org.apache.skyline.admin.server.domain.entities.PluginVersionDomain;
import org.apache.skyline.admin.server.domain.repository.PluginVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson2.JSON;

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
        PluginVersionDO pluginVersionDO = this.convertDO(pluginVersionDomain);

        pluginVersionDao.insert(pluginVersionDO);

        return pluginVersionDO.getId();
    }

    private PluginVersionDO convertDO(PluginVersionDomain pluginVersionDomain) {
        PluginVersionDO pluginVersionDO = new PluginVersionDO();
        pluginVersionDO.setVer(pluginVersionDomain.getVer());
        pluginVersionDO.setFeatures(JSON.toJSONString(pluginVersionDomain.getFeatures()));

        pluginVersionDO.setPluginId(pluginVersionDomain.getPluginDomain().getId());
        pluginVersionDO.setPageContent(pluginVersionDomain.getPageContent());
        pluginVersionDO.setDeleted(false);
        pluginVersionDO.setActive(true);
        pluginVersionDO.setSize(pluginVersionDomain.getSize());
        pluginVersionDO.setMd5(pluginVersionDomain.getMd5());
        pluginVersionDO.setJarUrl(pluginVersionDomain.getJarUrl());
        pluginVersionDO.setFileKey(pluginVersionDomain.getFileKey());
        pluginVersionDO.setVerTags(JSON.toJSONString(pluginVersionDomain.getVerTags()));

        return pluginVersionDO;

    }
}




















