/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.skyline.admin.server.support.parse;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.ibatis.plugin.PluginException;
import org.bravo.gaia.commons.exception.PlatformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginClassLoader extends ClassLoader {

    static {
        registerAsParallelCapable();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginClassLoader.class);
    private final File pluginPath;
    private List<Jar> allJars;

    private final ReentrantLock jarScanLock = new ReentrantLock();

    public PluginClassLoader(ClassLoader parent, String packagePath){
        super(parent);

        pluginPath = new File(packagePath);

        if (!pluginPath.exists()) {
            throw new PluginException("plugin source not found");
        }
    }

    public String getContent(String resourceName) {
        return getContent(jar -> jar.getName().equals(resourceName));
    }

    public String getContentIfPresent(Predicate<String> condition){
        return getContent(jar -> condition.test(jar.getName()));
    }

    public String getContent(Predicate<JarEntry> condition) {
        List<Jar> jars = getAllJars();
        for (Jar jar : jars) {
            JarFile jarFile = jar.jarFile;

            for(Enumeration<JarEntry> enumeration =  jarFile.entries(); enumeration.hasMoreElements(); ) {
                JarEntry jarEntry = enumeration.nextElement();

                System.out.println("===>>> "+jarEntry.getName());

                if (!jarEntry.isDirectory() && condition.test(jarEntry)) {
                    try {
                        InputStream is = jarFile.getInputStream(jarEntry);
                        return IOUtils.toString(is);
                    } catch (Exception ex) {
                        LOGGER.error("get content error with resource");
                        throw new PlatformException("load resource error");
                    }
                }
            }

        }
        return null;
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        List<Jar> allJars = getAllJars();
        String path = name.replace('.', '/').concat(".class");
        for (Jar jar : allJars) {
            JarEntry entry = jar.jarFile.getJarEntry(path);
            if (entry == null) {
                continue;
            }
            try {
                URL classFileUrl = new URL("jar:file:" + jar.sourceFile.getAbsolutePath() + "!/" + path);
                byte[] data;
                try (final BufferedInputStream is = new BufferedInputStream(
                        classFileUrl.openStream()); final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    int ch;
                    while ((ch = is.read()) != -1) {
                        baos.write(ch);
                    }
                    data = baos.toByteArray();
                }
                return defineClass(name, data, 0, data.length);
            } catch (IOException e) {
                LOGGER.error("find class fail.", e);
            }
        }
        throw new ClassNotFoundException("Can't find " + name);
    }

    @Override
    protected URL findResource(String name) {
        List<Jar> allJars = getAllJars();
        for (Jar jar : allJars) {
            JarEntry entry = jar.jarFile.getJarEntry(name);
            if (entry != null) {
                try {
                    return new URL("jar:file:" + jar.sourceFile.getAbsolutePath() + "!/" + name);
                } catch (MalformedURLException ignored) {
                }
            }
        }
        return null;
    }

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        List<URL> allResources = new LinkedList<>();
        List<Jar> allJars = getAllJars();
        for (Jar jar : allJars) {
            JarEntry entry = jar.jarFile.getJarEntry(name);
            if (entry != null) {
                allResources.add(new URL("jar:file:" + jar.sourceFile.getAbsolutePath() + "!/" + name));
            }
        }

        final Iterator<URL> iterator = allResources.iterator();
        return new Enumeration<URL>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public URL nextElement() {
                return iterator.next();
            }
        };
    }

    private List<Jar> getAllJars() {
        if (allJars == null) {
            jarScanLock.lock();
            try {
                if (allJars == null) {
                    allJars = doGetJars();
                }
            } finally {
                jarScanLock.unlock();
            }
        }

        return allJars;
    }

    private LinkedList<Jar> doGetJars() {
        LinkedList<Jar> jars = new LinkedList<>();
        if (pluginPath.exists()) {
            try {
                JarFile jarFile = new JarFile(pluginPath);
                Jar jar = new Jar(jarFile, pluginPath);

                jars.add(jar);

                LOGGER.info("{} loaded.", pluginPath.toString());

            } catch (IOException e) {
                LOGGER.error("{} jar file can't be resolved", pluginPath.getPath(), e);
            }
        }
        return jars;
    }

    @Data
    @RequiredArgsConstructor
    private static class Jar {
        private final JarFile jarFile;
        private final File sourceFile;
    }
}