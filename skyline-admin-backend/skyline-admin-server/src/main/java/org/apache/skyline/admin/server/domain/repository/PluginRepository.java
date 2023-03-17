package org.apache.skyline.admin.server.domain.repository;

import org.apache.skyline.admin.server.domain.model.PluginDomain;
import org.apache.skyline.admin.server.domain.query.PluginCombineQuery;
import org.bravo.gaia.commons.base.PageBean;

/**
 * @author hejianbing
 * @version @Id: PluginRepository.java, v 0.1 2022年12月23日 13:43 hejianbing Exp $
 */
public interface PluginRepository {

    Long create(PluginDomain pluginDomain);

    boolean updateById(PluginDomain pluginDomain);

    PluginDomain findOne(PluginCombineQuery pluginCombineQuery);

    PageBean<PluginDomain> pageQuery(PluginCombineQuery condition,Integer pageNo,Integer pageSize);

    boolean delete(PluginCombineQuery combineQuery);
}