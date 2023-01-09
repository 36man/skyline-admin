package org.apache.skyline.admin.server.domain.repository.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.skyline.admin.server.dal.dao.SkylinePluginDao;
import org.apache.skyline.admin.server.dal.dataobject.SkylinePluginDO;
import org.apache.skyline.admin.server.domain.entities.SkylinePluginDomain;
import org.apache.skyline.admin.server.domain.repository.SkylinePluginRepository;
import org.apache.skyline.admin.server.model.query.SkylinePluginQuery;
import org.apache.skyline.admin.server.utils.PageCommonUtils;
import org.bravo.gaia.commons.base.PageBean;
import org.bravo.gaia.commons.base.PageCondition;
import org.bravo.gaia.id.generator.IdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

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

    @Override
    public PageBean<SkylinePluginDomain> pageList(PageCondition<SkylinePluginQuery> condition) {
        Page<SkylinePluginDO> page = new Page<>();
        page.setCurrent(condition.getPageNum());
        page.setSize(condition.getPageSize());
        LambdaQueryWrapper<SkylinePluginDO> pageCondition = new LambdaQueryWrapper<>();

        SkylinePluginQuery skylinePluginQuery = condition.getCondition();

        pageCondition.eq(StringUtils.isNoneBlank(skylinePluginQuery.getKeyword()), SkylinePluginDO::getKeywords, skylinePluginQuery.getKeyword());
        pageCondition.eq(StringUtils.isNoneBlank(skylinePluginQuery.getPluginName()), SkylinePluginDO::getPluginName, skylinePluginQuery.getPluginName());

        Page<SkylinePluginDO> result = skylinePluginDao.selectPage(page, pageCondition);

        return PageCommonUtils.convert(result,this::convertDomain);
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

    private  List<SkylinePluginDomain> convertDomain(List<SkylinePluginDO> items) {
        if (items == null) {
            return null;
        }
        return items.stream().map(item-> convertDomain(item)).collect(Collectors.toList());

    }

    private static SkylinePluginDomain convertDomain(SkylinePluginDO item) {
        if (item == null) {
            return null;
        }

        SkylinePluginDomain skylinePluginDomain = new SkylinePluginDomain();

        BeanUtils.copyProperties(item, skylinePluginDomain, "keywords");

        PropertyMapper propertyMapper = PropertyMapper.get();

        propertyMapper.from(item.getKeywords())
                .whenHasText()
                .as(s -> JSON.parseArray(s, String.class))
                .to(skylinePluginDomain::setKeywords);
        return skylinePluginDomain;
    }
}