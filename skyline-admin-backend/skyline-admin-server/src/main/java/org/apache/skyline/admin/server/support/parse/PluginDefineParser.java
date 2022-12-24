package org.apache.skyline.admin.server.support.parse;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
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

/**
 * @author hejianbing
 * @version @Id: PluginDefineParser.java, v 0.1 2022年12月24日 18:35 hejianbing Exp $
 */
public class PluginDefineParser {
    public static final Map<Predicate<JarEntry>, Function<String,Map<String,Object>>> handler = new HashMap<>();

    private static PathMatcher pathMatcher = new AntPathMatcher();

    private static final String PLUGIN_NAME_PATTERN = "**.plugin_define.json";
    private static final String PLUGIN_PAGE_PATTERN = "**.plugin_page.html";


    static {
        handler.put(new Predicate<JarEntry>() {
            @Override
            public boolean test(JarEntry jarEntry) {
                return !jarEntry.isDirectory() && pathMatcher.match(PLUGIN_NAME_PATTERN,jarEntry.getName());
            }
        }, new Function<String, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(String content) {
                return JSON.parseObject(content, Map.class);
            }
        });

        handler.put(jarEntry -> !jarEntry.isDirectory() && pathMatcher.match(PLUGIN_PAGE_PATTERN,jarEntry.getName()), new Function<String, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(String content) {
                Map<String,Object> result = new HashMap<>();
                result.put("pageContent", content);

                return result;
            }
        });
    }

    private File jarPath;

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
        Map<String,Object> result = new HashMap<>();
        try{
            JarFile jarFile = new JarFile(jarPath);
            for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
                JarEntry jarEntry = entries.nextElement();
                for (Map.Entry<Predicate<JarEntry>, Function<String,Map<String,Object>>> entry : handler.entrySet()) {
                    Predicate<JarEntry> condition = entry.getKey();
                    if (condition.test(jarEntry)) {
                        InputStream inputStream = jarFile.getInputStream(jarEntry);
                        result.putAll(entry.getValue().apply(IOUtils.toString(inputStream)));
                    }
                }
            }

        }catch(Exception ex){

            GaiaLogger.getGlobalErrorLogger().error("plugin define parse error {}", ExceptionUtils.getRootCauseMessage(ex));

            throw new PlatformException(SkylineAdminErrorCode.PLUGIN_PARSE_ERROR.getCode());
        }finally {
            jarPath.delete();
        }
        return this.bindToPluginDefine(result);
    }

    private PluginDefine bindToPluginDefine(Map<String, Object> result) {
        ConfigurationPropertySource sources = new MapConfigurationPropertySource(result);

        Binder binder = new Binder(sources);

        PluginDefine pluginDefine = binder.bind("", Bindable.of(PluginDefine.class)).get();

        pluginDefine.checkNotNull();

        return pluginDefine;
    }
}