package org.apache.skyline.admin.server.domain.request;

import lombok.Builder;
import lombok.Data;
import org.apache.skyline.admin.server.domain.entities.PluginVersionDomain;
import org.apache.skyline.admin.server.domain.entities.SkylinePluginDomain;
import org.apache.skyline.admin.server.support.parse.PluginDefine;

/**
 * @author hejianbing
 * @version @Id: GenerateSkylinePluginDomainRequest.java, v 0.1 2022年12月26日 09:31 hejianbing Exp $
 */
@Data
@Builder
public class GenerateSkylinePluginDomainRequest {

    private PluginDefine pluginDefine;

    private String fileKey;

    private String jarUrl;

    private Long size;

    private String md5;

    public SkylinePluginDomain getPluginDomain() {
        SkylinePluginDomain pluginDomain = new SkylinePluginDomain();
        pluginDomain.setPluginName(pluginDefine.getPluginName());
        pluginDomain.setMaintainer(pluginDefine.getMaintainer());
        pluginDomain.setOverview(pluginDefine.getOverview());
        pluginDomain.setKeywords(pluginDefine.getKeywords());
        pluginDomain.setDefineClass(pluginDefine.getDefineClass());

        return pluginDomain;
    }

    public PluginVersionDomain getPluginVersionDomain() {
        PluginVersionDomain pluginVersionDomain = new PluginVersionDomain();
        pluginVersionDomain.setFileKey(fileKey);
        pluginVersionDomain.setJarUrl(jarUrl);
        pluginVersionDomain.setVer(pluginDefine.getVer());
        pluginVersionDomain.setFeatures(pluginDefine.getFeatures());
        pluginVersionDomain.setVerTags(pluginDefine.getVerTags());
        pluginVersionDomain.setSize(size);
        pluginVersionDomain.setDeleted(false);
        pluginVersionDomain.setActive(true);
        pluginVersionDomain.setMd5(md5);
        pluginVersionDomain.setPageContent(pluginDefine.getDefinePage());

        pluginVersionDomain.setPluginDomain(getPluginDomain());

        return pluginVersionDomain;
    }


}