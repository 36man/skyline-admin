package org.apache.skyline.admin.server.domain.service;

import org.apache.skyline.admin.server.domain.request.GeneratePluginDomainRequest;

/**
 * @author hejianbing
 * @version @Id: PluginDomainService.java, v 0.1 2022年12月24日 22:08 hejianbing Exp $
 */
public interface PluginDomainService {

    Long storePlugin(GeneratePluginDomainRequest pluginDomainRequest);

    Boolean delete(Long id);


    Boolean actived(Long id);

    Boolean disabled(Long id);
}