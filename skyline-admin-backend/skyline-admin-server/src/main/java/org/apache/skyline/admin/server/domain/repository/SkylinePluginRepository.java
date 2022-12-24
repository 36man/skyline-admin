package org.apache.skyline.admin.server.domain.repository;

import org.apache.skyline.admin.server.domain.entities.SkylinePluginDomain;

/**
 * @author hejianbing
 * @version @Id: SkylinePluginRepository.java, v 0.1 2022年12月23日 13:43 hejianbing Exp $
 */
public interface SkylinePluginRepository {


    Long create(SkylinePluginDomain skylinePluginDomain);

    boolean updateById(SkylinePluginDomain skylinePluginDomain);

    SkylinePluginDomain findByClassDefine(String classDefine);

}