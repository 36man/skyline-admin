package org.apache.skyline.admin.server.support.parse;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.skyline.admin.commons.exception.SkylineAdminErrorCode;
import org.bravo.gaia.commons.exception.PlatformException;
import org.bravo.gaia.log.GaiaLogger;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.PathMatcher;

import com.alibaba.fastjson2.JSON;
import com.google.common.base.Preconditions;

/**
 * @author hejianbing
 * @version @Id: PluginDefineParser.java, v 0.1 2022年12月24日 18:35 hejianbing Exp $
 */
public class PluginDefineParser {

    private static PathMatcher  pathMatcher         = new AntPathMatcher();

    private static final String PLUGIN_NAME_PATTERN = "**.plugin_define.json";

    private File                jarPath;

    public PluginDefineParser(byte[] bytes) {
        init(bytes);
    }

    private void init(byte[] bytes) {
        try{
            jarPath = File.createTempFile(RandomStringUtils.randomNumeric(30), ".jar");
            FileCopyUtils.copy(bytes, jarPath);
        }catch(Exception ex){
            throw new PlatformException(SkylineAdminErrorCode.PLUGIN_PARSE_ERROR.getCode());
        }
    }

    public PluginDefine parse() {
        try{

            PluginDefine pluginDefine = getPluginDefine();

            this.parsePluginPage(pluginDefine);

            return pluginDefine;

        }finally {
            jarPath.delete();
        }
    }

    private void parsePluginPage(PluginDefine pluginDefine) {
        String content = scanJarContent(jarEntry -> !jarEntry.isDirectory() && jarEntry.getName().endsWith(pluginDefine.getDefinePage()));

        Preconditions.checkArgument(StringUtils.isNotBlank(content), "plugin definePage is not found");

        pluginDefine.setPageContent(content);

    }

    private PluginDefine getPluginDefine() {
        String pluginDefineContent = scanJarContent(jarEntry-> !jarEntry.isDirectory() && pathMatcher.match(PLUGIN_NAME_PATTERN,jarEntry.getName()));

        Preconditions.checkArgument(StringUtils.isNotBlank(pluginDefineContent), "plugin define not found");

        ConfigurationPropertySource sources = new MapConfigurationPropertySource(JSON.parseObject(pluginDefineContent, Map.class));

        Binder binder = new Binder(sources);

        PluginDefine pluginDefine = binder.bind("", Bindable.of(PluginDefine.class)).get();

        pluginDefine.checkNotNull();

        return pluginDefine;

    }

    public String scanJarContent(Predicate<JarEntry> filter) {
        try {
            JarFile jarFile = new JarFile(jarPath);
            for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
                JarEntry jarEntry = entries.nextElement();
                if (filter.test(jarEntry)) {
                    InputStream inputStream = jarFile.getInputStream(jarEntry);
                    return IOUtils.toString(inputStream);
                }
            }
        } catch (Exception exception) {
            GaiaLogger.getGlobalErrorLogger().error("plugin jarEntry forEach error {}", ExceptionUtils.getRootCauseMessage(exception));

            throw new PlatformException(SkylineAdminErrorCode.PLUGIN_PARSE_ERROR.getCode());
        }
        return null;
    }

}