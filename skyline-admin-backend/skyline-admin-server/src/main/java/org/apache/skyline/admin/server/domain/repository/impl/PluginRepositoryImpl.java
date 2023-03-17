package org.apache.skyline.admin.server.domain.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.skyline.admin.server.commons.utils.PageCommonUtils;
import org.apache.skyline.admin.server.dal.dao.PluginDao;
import org.apache.skyline.admin.server.dal.dataobject.PluginDO;
import org.apache.skyline.admin.server.domain.model.PluginDomain;
import org.apache.skyline.admin.server.domain.query.PluginCombineQuery;
import org.apache.skyline.admin.server.domain.repository.PluginRepository;
import org.bravo.gaia.commons.base.PageBean;
import org.bravo.gaia.commons.util.AssertUtil;
import org.bravo.gaia.id.generator.IdGenerator;
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
 * @version @Id: PluginRepositoryImpl.java, v 0.1 2022年12月23日 13:43 hejianbing Exp $
 */
@Repository
public class PluginRepositoryImpl implements PluginRepository {

    @Autowired
    private PluginDao pluginDao;

    @Autowired
    private IdGenerator<Long> idGenerator;

    @Override
    public Long create(PluginDomain pluginDomain) {
        return doExecute(pluginDomain,item->{

            item.setId(idGenerator.generate());

            pluginDao.insert(item);

            return item.getId();
        });
    }

    @Override
    public boolean updateById(PluginDomain pluginDomain) {
        return doExecute(pluginDomain,item->{
            int rows = pluginDao.updateById(item);
            return rows > 0;
        });
    }

    @Override
    public PluginDomain findOne(PluginCombineQuery pluginCombineQuery) {
        return selectList(pluginCombineQuery).get(0);
    }

    public PageBean<PluginDomain> pageQuery(PluginCombineQuery pluginCombineQuery,
                                            Integer pageNo,
                                            Integer pageSize) {
        Page<PluginDO> page = new Page<>();
        page.setCurrent(pageNo);
        page.setSize(pageSize);

        Page<PluginDO> result = pluginDao.selectPage(page, pluginCombineQuery.toQuery());

        return PageCommonUtils.convert(result, items -> convert(items));
    }

    @Override
    public boolean delete(PluginCombineQuery combineQuery) {
        return pluginDao.delete(combineQuery.toQuery()) > 0;
    }

    private PluginDO convertDO(PluginDomain skylinePluginDomain) {
        PluginDO pluginDO = new PluginDO();
        pluginDO.setMaintainer(skylinePluginDomain.getMaintainer());
        pluginDO.setClassDefine(skylinePluginDomain.getDefineClass());
        pluginDO.setPluginName(skylinePluginDomain.getPluginName());
        pluginDO.setOverview(skylinePluginDomain.getOverview());
        return pluginDO;
    }

    private List<PluginDomain> selectList(PluginCombineQuery pluginCombineQuery) {
        LambdaQueryWrapper<PluginDO> condition = pluginCombineQuery.toQuery();

        List<PluginDO> pluginDOList = pluginDao.selectList(condition);

        return convert(pluginDOList);
    }

    private List<PluginDomain> convert(List<PluginDO> items) {
        return Optional.ofNullable(items)
                .orElseGet(ArrayList::new)
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    private PluginDomain convert(PluginDO pluginDO) {
        PluginDomain pluginDomain = new PluginDomain();

        BeanUtils.copyProperties(pluginDO, pluginDomain);

        return pluginDomain;
    }

    private <T> T doExecute(PluginDomain pluginDomain, Function<PluginDO, T> handler) {
        PluginDO pluginDO = convertDO(pluginDomain);

        AssertUtil.notNull(pluginDO, "pluginDO is null");

        return handler.apply(pluginDO);
    }
}