package org.apache.skyline.admin.server.support.parse;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

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
    private static final YamlPropertySourceLoader LOADER       = new YamlPropertySourceLoader();

    private final PluginScanner pluginScanner;

    public PluginDefineParser(byte[] bytes) {
        pluginScanner = new PluginScanner(bytes);
    }

    public PluginDefine parse() {
        try{
            this.parsePluginDefine();

            this.parsePluginPage();

            return pluginDefine;

        }finally {
            pluginScanner.destroy();
        }
    }

    private void parsePluginPage() {
        String content = pluginScanner.getContent(name -> name.endsWith(pluginDefine.getDefinePage()));

        Preconditions.checkArgument(StringUtils.isNotBlank(content), "plugin definePage is not found");

        pluginDefine.setPageContent(content);

    }

    private void parsePluginDefine() {
        String pluginDefineContent = pluginScanner.getContent(name -> name.endsWith("skyline.yml")|| name.endsWith("skyline.yaml"));
        try{
            Preconditions.checkArgument(StringUtils.isNotBlank(pluginDefineContent), "plugin define not found");

            List<PropertySource<?>> propertySources = LOADER.load("pluginDefine", new ByteArrayResource(pluginDefineContent.getBytes()));

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