package org.apache.skyline.admin.server.domain.repository;

import org.apache.skyline.admin.server.domain.entities.SkylinePluginDomain;
import org.apache.skyline.admin.server.model.query.SkylinePluginQuery;
import org.bravo.gaia.commons.base.PageBean;
import org.bravo.gaia.commons.base.PageCondition;

/**
 * @author hejianbing
 * @version @Id: SkylinePluginRepository.java, v 0.1 2022年12月23日 13:43 hejianbing Exp $
 */
public interface SkylinePluginRepository {


    Long create(SkylinePluginDomain skylinePluginDomain);

    boolean updateById(SkylinePluginDomain skylinePluginDomain);

    SkylinePluginDomain findByClassDefine(String classDefine);

    PageBean<SkylinePluginDomain> pageList(PageCondition<SkylinePluginQuery> condition);
}