package org.apache.skyline.admin.server.domain.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.skyline.admin.commons.model.query.PluginQuery;
import org.apache.skyline.admin.commons.model.request.PageRequest;
import org.apache.skyline.admin.server.dal.dao.SkylinePluginDao;
import org.apache.skyline.admin.server.dal.dataobject.PluginDO;
import org.apache.skyline.admin.server.domain.entities.SkylinePluginDomain;
import org.apache.skyline.admin.server.domain.repository.SkylinePluginRepository;
import org.apache.skyline.admin.server.utils.PageCommonUtils;
import org.bravo.gaia.commons.base.PageBean;
import org.bravo.gaia.id.generator.IdGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

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
        PluginDO pluginDO = this.convertDO(skylinePluginDomain);

        pluginDO.setId(idGenerator.generate());

        skylinePluginDao.insert(pluginDO);

        return pluginDO.getId();
    }

    @Override
    public boolean updateById(SkylinePluginDomain skylinePluginDomain) {

        PluginDO pluginDO = convertDO(skylinePluginDomain);

        pluginDO.setId(skylinePluginDomain.getId());

        int rows = skylinePluginDao.updateById(pluginDO);

        return rows > 0;
    }

    @Override
    public SkylinePluginDomain findByClassDefine(String classDefine) {
        LambdaQueryWrapper<PluginDO> condition = new LambdaQueryWrapper<>();
        condition.eq(PluginDO::getClassDefine, classDefine);

        PluginDO pluginDO = skylinePluginDao.selectOne(condition);

        return this.convertDomain(pluginDO);
    }

    @Override
    public PageBean<SkylinePluginDomain> pageList(PageRequest<PluginQuery> condition) {
        Page<PluginDO> page = new Page<>();
        page.setCurrent(condition.getPageNo());
        page.setSize(condition.getPageSize());
        LambdaQueryWrapper<PluginDO> pageCondition = new LambdaQueryWrapper<>();

        PluginQuery skylinePluginQuery = condition.getCondition();

        pageCondition.eq(StringUtils.isNoneBlank(skylinePluginQuery.getPluginName()), PluginDO::getPluginName, skylinePluginQuery.getPluginName());

        Page<PluginDO> result = skylinePluginDao.selectPage(page, pageCondition);

        return PageCommonUtils.convert(result,this::convertDomain);
    }

    private PluginDO convertDO(SkylinePluginDomain skylinePluginDomain) {
        PluginDO pluginDO = new PluginDO();
        pluginDO.setMaintainer(skylinePluginDomain.getMaintainer());
        pluginDO.setClassDefine(skylinePluginDomain.getDefineClass());
        pluginDO.setPluginName(skylinePluginDomain.getPluginName());
        pluginDO.setOverview(skylinePluginDomain.getOverview());
        return pluginDO;
    }

    private  List<SkylinePluginDomain> convertDomain(List<PluginDO> items) {
        if (items == null) {
            return null;
        }
        return items.stream().map(item-> convertDomain(item)).collect(Collectors.toList());

    }

    private static SkylinePluginDomain convertDomain(PluginDO item) {
        if (item == null) {
            return null;
        }

        SkylinePluginDomain skylinePluginDomain = new SkylinePluginDomain();

        BeanUtils.copyProperties(item, skylinePluginDomain);

        PropertyMapper propertyMapper = PropertyMapper.get();

        return skylinePluginDomain;
    }
}