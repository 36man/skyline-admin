package org.apache.skyline.admin.server.domain.repository.impl;

import java.util.List;

import org.apache.skyline.admin.server.dal.dao.SkylinePluginDao;
import org.apache.skyline.admin.server.dal.dataobject.SkylinePluginDO;
import org.apache.skyline.admin.server.domain.entities.SkylinePluginDomain;
import org.apache.skyline.admin.server.domain.repository.SkylinePluginRepository;
import org.bravo.gaia.id.generator.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 * @author hejianbing
 * @version @Id: SkylinePluginRepositoryImpl.java, v 0.1 2022年12月23日 13:43 hejianbing Exp $
 */
@Repository
public class SkylinePluginRepositoryImpl implements SkylinePluginRepository {

    @Autowired
    private SkylinePluginDao skylinePluginDao;

    @Autowired
    private IdGenerator<Long> idGenerator;

    @Override
    public Long create(SkylinePluginDomain skylinePluginDomain) {
        SkylinePluginDO pluginDO = this.convertDO(skylinePluginDomain);

        pluginDO.setId(idGenerator.generate());

        skylinePluginDao.insert(pluginDO);

        return pluginDO.getId();
    }

    @Override
    public boolean updateById(SkylinePluginDomain skylinePluginDomain) {

        SkylinePluginDO pluginDO = convertDO(skylinePluginDomain);

        pluginDO.setId(skylinePluginDomain.getId());

        int rows = skylinePluginDao.updateById(pluginDO);

        return rows > 0;
    }

    @Override
    public SkylinePluginDomain findByClassDefine(String classDefine) {
        LambdaQueryWrapper<SkylinePluginDO> condition = new LambdaQueryWrapper<>();
        condition.eq(SkylinePluginDO::getDefineClass, classDefine);

        SkylinePluginDO pluginDO = skylinePluginDao.selectOne(condition);

        return this.convertDomain(pluginDO);
    }

    private SkylinePluginDomain convertDomain(SkylinePluginDO pluginDO) {
        if (pluginDO == null) {
            return null;
        }
        SkylinePluginDomain pluginDomain = new SkylinePluginDomain();
        SkylinePluginDomain skylinePluginDomain = new SkylinePluginDomain();
        skylinePluginDomain.setId(pluginDO.getId());
        skylinePluginDomain.setMaintainer(pluginDO.getMaintainer());
        skylinePluginDomain.setPluginName(pluginDO.getPluginName());
        skylinePluginDomain.setDefineClass(pluginDO.getDefineClass());
        skylinePluginDomain.setOverview(pluginDO.getOverview());
        skylinePluginDomain.setKeywords(JSON.parseObject(pluginDO.getKeywords(), List.class));
        skylinePluginDomain.setCreateTime(pluginDO.getCreateTime());
        skylinePluginDomain.setUpdateTime(pluginDO.getUpdateTime());

        return pluginDomain;
    }


    private SkylinePluginDO convertDO(SkylinePluginDomain skylinePluginDomain) {
        SkylinePluginDO pluginDO = new SkylinePluginDO();
        pluginDO.setMaintainer(skylinePluginDomain.getMaintainer());
        pluginDO.setDefineClass(skylinePluginDomain.getDefineClass());
        pluginDO.setPluginName(skylinePluginDomain.getPluginName());
        pluginDO.setOverview(skylinePluginDomain.getOverview());
        pluginDO.setKeywords(JSON.toJSONString(skylinePluginDomain.getKeywords()));
        return pluginDO;
    }
}