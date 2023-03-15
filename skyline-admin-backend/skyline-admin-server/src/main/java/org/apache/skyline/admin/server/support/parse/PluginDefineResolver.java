package org.apache.skyline.admin.server.support.parse;

import com.alibaba.fastjson2.JSON;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bravo.gaia.commons.exception.PlatformException;
import org.bravo.gaia.commons.util.AssertUtil;
import org.bravo.gaia.commons.util.ClassUtils;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hejianbing
 * @version @Id: PluginDefineResolver.java, v 0.1 2022年12月24日 18:35 hejianbing Exp $
 */
@Slf4j
public class PluginDefineResolver {

    private YamlPropertySourceLoader sourceLoader = new YamlPropertySourceLoader();
    private PluginClassLoader pluginClassLoader;


    public PluginDefineResolver(String path) {
        pluginClassLoader = new PluginClassLoader(PluginDefineResolver.class.getClassLoader(), path);
    }

    public PluginDefine resolve() {
        PluginDefine pluginDefine = this.resolveDefine();

        this.resolvePageContent(pluginDefine);

        this.resolveSwitchItems(pluginDefine);

        
        return pluginDefine;
    }

    private void resolveSwitchItems(PluginDefine pluginDefine) {
        try {
            String classDefine = pluginDefine.getClassDefine();

            Class<?> clazz = Class.forName(classDefine, true, this.pluginClassLoader);

            Object plugin = clazz.newInstance();

            Method method = plugin.getClass().getMethod("exportCapableSwitches");

            Object result = method.invoke(plugin, null);

            if (result == null) {
                return;
            }

            List<Map<String, Object>> items = new ArrayList<>();

            List<Object> capableSwitchList = (List<Object>) result;

            for (Object o : capableSwitchList) {
                if (!o.getClass().getName().equals("org.apache.skyline.plugin.api.CapableSwitch")) {
                    continue;
                }
                Map<String, Object> item = new HashMap<>();

                ReflectionUtils.doWithLocalFields(o.getClass(), field -> {
                    String name = field.getName();
                    Object value = field.get(o);
                    item.put(name, value);

                });
                items.add(item);
            }

            if (CollectionUtils.isNotEmpty(items)) {
                pluginDefine.setSwitchItems(JSON.toJSONString(items));
            }

        } catch (Exception ex) {
            throw new PlatformException("switch parse error");
        }
    }


    private void resolvePageContent(PluginDefine pluginDefine) {
        String content = pluginClassLoader.getContent(pluginDefine.getPageDefine());

        Preconditions.checkArgument(StringUtils.isNotBlank(content), "plugin pageDefine is not found");

        pluginDefine.setPageContent(content);
    }

    private PluginDefine resolveDefine() {
        String content = pluginClassLoader.getContentIfPresent(name -> name.endsWith("skyline.yaml") || name.endsWith("skyline.yml"));

        AssertUtil.isNotBlank(content, "plugin config not found");

        List<PropertySource<?>> propertySources;

        try {
            propertySources = sourceLoader.load("pluginDefine", new ByteArrayResource(content.getBytes()));
        } catch (Exception ex) {
            throw new PlatformException("load plugin config error");
        }

        StandardEnvironment environment = new StandardEnvironment();

        propertySources.forEach(propertySource -> environment.getPropertySources().addFirst(propertySource));

        PluginDefine pluginDefine = Binder.get(environment).bind("skyline", PluginDefine.class).get();

        pluginDefine.checkParams();

        boolean classDefineExists = ClassUtils.isPresent(pluginDefine.getClassDefine(), this.pluginClassLoader);

        AssertUtil.isTrue(classDefineExists, "classDefine not found");

        return pluginDefine;
    }

}