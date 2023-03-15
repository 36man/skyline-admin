package org.apache.skyline.admin.server.domain.repository;

import org.apache.skyline.admin.commons.model.query.PluginQuery;
import org.apache.skyline.admin.commons.model.request.PageRequest;
import org.apache.skyline.admin.server.domain.model.PluginDomain;
import org.bravo.gaia.commons.base.PageBean;

/**
 * @author hejianbing
 * @version @Id: PluginRepository.java, v 0.1 2022年12月23日 13:43 hejianbing Exp $
 */
public interface PluginRepository {

    Long create(PluginDomain skylinePluginDomain);

    boolean updateById(PluginDomain skylinePluginDomain);

    PluginDomain findByClassDefine(String classDefine);

    PageBean<PluginDomain> pageList(PageRequest<PluginQuery> condition);
}