package org.apache.skyline.admin.server.domain.repository;

import org.apache.skyline.admin.server.domain.entities.PluginVersionDomain;

/**
 * @author hejianbing
 * @version @Id: PluginVersionRepository.java, v 0.1 2022年12月24日 23:27 hejianbing Exp $
 */
public interface PluginVersionRepository {

    Long create(PluginVersionDomain pluginVersionDomain);
}