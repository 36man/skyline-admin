package org.apache.skyline.admin.server.domain.repository;

import org.apache.skyline.admin.commons.model.query.PluginQuery;
import org.apache.skyline.admin.commons.model.request.PageRequest;
import org.apache.skyline.admin.server.domain.entities.SkylinePluginDomain;
import org.bravo.gaia.commons.base.PageBean;

/**
 * @author hejianbing
 * @version @Id: SkylinePluginRepository.java, v 0.1 2022年12月23日 13:43 hejianbing Exp $
 */
public interface SkylinePluginRepository {


    Long create(SkylinePluginDomain skylinePluginDomain);

    boolean updateById(SkylinePluginDomain skylinePluginDomain);

    SkylinePluginDomain findByClassDefine(String classDefine);

    PageBean<SkylinePluginDomain> pageList(PageRequest<PluginQuery> condition);
}