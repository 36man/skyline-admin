package org.apache.skyline.admin.server.support.parse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.skyline.admin.commons.exception.SkylineAdminErrorCode;
import org.bravo.gaia.commons.exception.PlatformException;
import org.bravo.gaia.log.GaiaLogger;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author hejianbing
 * @version @Id: PluginScanner.java, v 0.1 2023年01月09日 10:17 hejianbing Exp $
 */
public class PluginScanner {

    private Map<String,String> content = new HashMap<>();

    private File jarPath;

    public PluginScanner(byte[] bytes) {
        this.scan(bytes);
    }

    private void scan(byte [] bytes) {
        try {
            this.jarPath = File.createTempFile(RandomStringUtils.randomNumeric(30), ".jar");

            FileCopyUtils.copy(bytes, jarPath);

            JarFile jarFile = new JarFile(jarPath);

            for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
                JarEntry jarEntry = entries.nextElement();
                if (!jarEntry.isDirectory()) {
                    InputStream inputStream = jarFile.getInputStream(jarEntry);
                    this.content.put(jarEntry.getName(), IOUtils.toString(inputStream));
                }
            }
        } catch (Exception exception) {
            GaiaLogger.getGlobalErrorLogger().error("plugin! jar content read error {}",
                    ExceptionUtils.getRootCauseMessage(exception));

            throw new PlatformException(SkylineAdminErrorCode.PLUGIN_PARSE_ERROR.getCode());
        }
    }


    public String getContent(Predicate<String> filter) {
        return content.entrySet().stream()
                .filter(map ->filter.test(map.getKey()))
                .map(p -> p.getValue())
                .findFirst().orElseGet(() -> null);
    }

    public void destroy() {
        if (this.jarPath != null) {
            this.jarPath.delete();
        }
    }
}