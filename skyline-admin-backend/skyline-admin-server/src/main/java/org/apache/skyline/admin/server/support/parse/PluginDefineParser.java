package org.apache.skyline.admin.server.support.parse;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.PredicateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.skyline.admin.commons.exception.SkylineAdminErrorCode;
import org.bravo.gaia.commons.exception.PlatformException;
import org.bravo.gaia.log.GaiaLogger;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ByteArrayResource;

import com.google.common.base.Preconditions;

/**
 * @author hejianbing
 * @version @Id: PluginDefineParser.java, v 0.1 2022年12月24日 18:35 hejianbing Exp $
 */
@Slf4j
public class PluginDefineParser {

    private PluginDefine                          pluginDefine = new PluginDefine();
    private static final YamlPropertySourceLoader YAML_LOADER  = new YamlPropertySourceLoader();

    private final ByteJarFileLoader jarFileLoader;

    public PluginDefineParser(byte[] bytes) {
        jarFileLoader = new ByteJarFileLoader(bytes);
    }


    public PluginDefine parse() {
        try {
            this.parsePluginDefine();

            this.parsePluginPage();

            return pluginDefine;

        } finally {
            jarFileLoader.close();
        }
    }

    private void parsePluginPage() {
        String content = jarFileLoader.getContent(name -> name.endsWith(pluginDefine.getDefinePage()));

        Preconditions.checkArgument(StringUtils.isNotBlank(content), "plugin definePage is not found");

        pluginDefine.setPageContent(content);

    }

    private void parsePluginDefine() {
        String pluginDefineContent = jarFileLoader.getContent(PredicateUtils.anyPredicate(
            name -> name.endsWith("skyline.yaml"), name -> name.endsWith("skyline.yml")));

        Preconditions.checkArgument(StringUtils.isNotBlank(pluginDefineContent), "plugin define not found");

        try{
            List<PropertySource<?>> propertySources = YAML_LOADER.load("pluginDefine", new ByteArrayResource(pluginDefineContent.getBytes()));

            StandardEnvironment environment = new StandardEnvironment();

            propertySources.forEach(propertySource -> {
                environment.getPropertySources().addFirst(propertySource);
            });
            this.pluginDefine = Binder.get(environment).bind("skyline", PluginDefine.class).get();

            pluginDefine.checkNotNull();

        }catch(Exception ex){
            GaiaLogger.getGlobalErrorLogger().error("plugin! parse pluginDefine content error {}",
                    ExceptionUtils.getRootCauseMessage(ex));

            throw new PlatformException(SkylineAdminErrorCode.PLUGIN_PARSE_ERROR.getCode());
        }
    }
}