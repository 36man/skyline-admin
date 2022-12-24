package org.apache.skyline.admin.server.service;

import org.apache.skyline.admin.server.model.request.GenerateSkylinePluginRequest;

/**
 * @author hejianbing
 * @version @Id: SkylinePluginService.java, v 0.1 2022年12月23日 13:39 hejianbing Exp $
 */
public interface SkylinePluginService {

    Boolean uploadPlugin(GenerateSkylinePluginRequest pluginRequest) ;
}