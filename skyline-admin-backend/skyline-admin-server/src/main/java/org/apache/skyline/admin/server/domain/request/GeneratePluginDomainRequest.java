package org.apache.skyline.admin.server.domain.request;

import lombok.Builder;
import lombok.Data;
import org.apache.skyline.admin.server.domain.model.PluginVersionDomain;
import org.apache.skyline.admin.server.domain.model.PluginDomain;
import org.apache.skyline.admin.server.support.parse.PluginDefine;

/**
 * @author hejianbing
 * @version @Id: GeneratePluginDomainRequest.java, v 0.1 2022年12月26日 09:31 hejianbing Exp $
 */
@Data
@Builder
public class GeneratePluginDomainRequest {

    private PluginDefine pluginDefine;

    private String fileKey;

    private String jarUrl;

    private Long size;

    public PluginDomain getPluginDomain() {
        PluginDomain pluginDomain = new PluginDomain();
        pluginDomain.setPluginName(pluginDefine.getPluginName());
        pluginDomain.setMaintainer(pluginDefine.getMaintainer());
        pluginDomain.setOverview(pluginDefine.getOverview());
        pluginDomain.setDefineClass(pluginDefine.getClassDefine());

        return pluginDomain;
    }

    public PluginVersionDomain getPluginVersionDomain() {
        PluginVersionDomain pluginVersionDomain = new PluginVersionDomain();
        pluginVersionDomain.setFileKey(fileKey);
        pluginVersionDomain.setJarUrl(jarUrl);
        pluginVersionDomain.setVer(pluginDefine.getVer());
        pluginVersionDomain.setFeatures(pluginDefine.getFeatures());
        pluginVersionDomain.setSize(size);
        pluginVersionDomain.setDeleted(false);
        pluginVersionDomain.setActive(true);
        pluginVersionDomain.setPageContent(pluginDefine.getPageDefine());

        pluginVersionDomain.setPluginDomain(getPluginDomain());

        return pluginVersionDomain;
    }


}