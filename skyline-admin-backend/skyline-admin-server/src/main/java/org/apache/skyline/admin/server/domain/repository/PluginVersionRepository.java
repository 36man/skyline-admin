package org.apache.skyline.admin.server.domain.repository;

import org.apache.skyline.admin.server.domain.model.PluginVersionDomain;
import org.apache.skyline.admin.server.domain.query.PluginVersionCombineQuery;
import org.bravo.gaia.commons.base.PageBean;

import java.util.List;

/**
 * @author hejianbing
 * @version @Id: PluginVersionRepository.java, v 0.1 2022年12月24日 23:27 hejianbing Exp $
 */
public interface PluginVersionRepository {

    Long create(PluginVersionDomain pluginVersionDomain);

    boolean delete(PluginVersionCombineQuery combineQuery);

    boolean active(Long id,boolean active);

    boolean active(PluginVersionCombineQuery combineQuery, boolean active);

    PluginVersionDomain findOne(PluginVersionCombineQuery combineQuery);

    Boolean update(PluginVersionCombineQuery combineQuery,PluginVersionDomain pluginVersionDomain);

    PageBean<PluginVersionDomain> pageQuery(PluginVersionCombineQuery versionQuery, Integer pageNo, Integer pageSize);

    List<PluginVersionDomain> findList(PluginVersionCombineQuery combineQuery);

}