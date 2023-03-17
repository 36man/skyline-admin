package org.apache.skyline.admin.server.domain.repository;

import org.apache.skyline.admin.server.domain.model.PluginVersionDomain;
import org.apache.skyline.admin.server.domain.query.PluginVersionCombineQuery;

/**
 * @author hejianbing
 * @version @Id: PluginVersionRepository.java, v 0.1 2022年12月24日 23:27 hejianbing Exp $
 */
public interface PluginVersionRepository {

    Long create(PluginVersionDomain pluginVersionDomain);

    boolean delete(PluginVersionCombineQuery versionCombineQuery);
}