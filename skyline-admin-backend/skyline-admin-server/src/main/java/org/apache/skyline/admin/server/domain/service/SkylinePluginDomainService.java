package org.apache.skyline.admin.server.domain.service;

import org.apache.skyline.admin.server.domain.request.GenerateSkylinePluginDomainRequest;

/**
 * @author hejianbing
 * @version @Id: SkylinePluginDomainService.java, v 0.1 2022年12月24日 22:08 hejianbing Exp $
 */
public interface SkylinePluginDomainService {

    Long createPlugin(GenerateSkylinePluginDomainRequest pluginDomainRequest);

}