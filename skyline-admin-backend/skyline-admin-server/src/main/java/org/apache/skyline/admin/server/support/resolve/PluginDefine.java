package org.apache.skyline.admin.server.support.resolve;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.skyline.admin.commons.enums.SymbolKind;
import org.bravo.gaia.commons.util.AssertUtil;

import java.util.List;

/**
 * @author hejianbing
 * @version @Id: PluginDefine.java, v 0.1 2022年12月24日 19:27 hejianbing Exp $
 */
@Data
public class PluginDefine {

    private String pluginName;

    private String classDefine;

    private String pageDefine;

    private String pageContent;

    private String maintainer;

    private String overview;

    private String ver;

    private List<String> features;

    private String typeMeta;

    public String getPluginName() {
        if (StringUtils.isBlank(this.pluginName)) {
            return this.classDefine.substring(
                    this.classDefine.lastIndexOf(SymbolKind.DOT.getSymbol()) + 1);
        }
        return this.pluginName;
    }

    public void checkParams() {
        AssertUtil.isNotBlank(maintainer, "plugin! maintainer is null");
        AssertUtil.isNotBlank(classDefine, "plugin! classDefine is null");
        AssertUtil.isNotBlank(pageDefine, "pageDefine! definePage is null");
        AssertUtil.isNotBlank(ver, "ver is null");
    }
}