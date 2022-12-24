package org.apache.skyline.admin.server.support.parse;

import java.util.List;

import lombok.Data;

import org.apache.commons.lang3.StringUtils;
import org.apache.skyline.admin.commons.enums.SymbolKind;
import org.bravo.gaia.commons.util.AssertUtil;

/**
 * @author hejianbing
 * @version @Id: PluginDefine.java, v 0.1 2022年12月24日 19:27 hejianbing Exp $
 */
@Data
public class PluginDefine {

    private String pluginName;

    private String maintainer;

    private List<String> keywords;

    private String overview;

    private String defineClass;

    private String definePage;

    private String ver;

    private List<String> features;

    private List<String> verTags;

    public String getPluginName() {
        if (StringUtils.isBlank(this.pluginName)) {
            return this.defineClass.substring(this.defineClass.lastIndexOf(SymbolKind.DOT.getSymbol()) + 1);
        }
        return this.pluginName;
    }

    public void checkNotNull() {
        AssertUtil.isNotBlank(maintainer, "maintainer is null");
        AssertUtil.notEmpty(keywords, "keywords is empty");
        AssertUtil.isNotBlank(defineClass, "defineClass is empty");
        AssertUtil.isNotBlank(definePage, "definePage is empty");
        AssertUtil.isNotBlank(ver, "ver is empty");
    }

}