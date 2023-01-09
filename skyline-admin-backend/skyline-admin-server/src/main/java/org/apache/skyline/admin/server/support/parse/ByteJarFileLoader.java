package org.apache.skyline.admin.server.support.parse;

import java.io.File;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.skyline.admin.commons.exception.SkylineAdminErrorCode;
import org.bravo.gaia.commons.exception.PlatformException;
import org.bravo.gaia.log.GaiaLogger;
import org.springframework.util.FileCopyUtils;

/**
 * @author hejianbing
 * @version @Id: ByteJarFileLoader.java, v 0.1 2023年01月09日 10:17 hejianbing Exp $
 */
public class ByteJarFileLoader implements AutoCloseable{

    private File jarPath;

    private JarFile jarFile;

    public ByteJarFileLoader(byte[] bytes) {
        ensureOpen(bytes);
    }

    private void ensureOpen(byte [] bytes) {
        try {
            this.jarPath = File.createTempFile(RandomStringUtils.randomNumeric(30), ".jar");

            FileCopyUtils.copy(bytes, jarPath);

            this.jarFile = new JarFile(jarPath);

        } catch (Exception exception) {
            GaiaLogger.getGlobalErrorLogger().error("plugin! load error {}", ExceptionUtils.getRootCauseMessage(exception));

            throw new PlatformException(SkylineAdminErrorCode.JAR_LOAD_ERROR.getCode());
        }
    }


    public String getContent(String name) {
        try{
            ZipEntry entry = jarFile.getEntry(name);

            if (entry == null) {
                return null;
            }
            InputStream inputStream = jarFile.getInputStream(entry);

            return IOUtils.toString(inputStream);

        }catch(Exception ex){
            GaiaLogger.getGlobalErrorLogger().error("plugin!  get content jar error {}", ExceptionUtils.getRootCauseMessage(ex));

            throw new PlatformException(SkylineAdminErrorCode.JAR_LOAD_ERROR.getCode());
        }
    }

    @Override
    public void close() {
        if (jarFile != null) {
            try{
                jarFile.close();
            }catch(Exception ex){

            }
        }
        if (this.jarPath != null) {
            this.jarPath.delete();
        }

    }
}