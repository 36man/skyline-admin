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

package org.apache.skyline.admin.web.config;

import org.apache.skyline.admin.server.config.properties.AdminProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    private static final String DEFAULT_SWAGGER_API_VERSION = "2.3.0";

    private static final String TITLE = "Admin API Document";

    private static final String DESCRIPTION = "Admin API";

    private static final String CONTACT_NAME = "skyline";

    private static final String CONTACT_URL = "https://github.com/apache/skyline";

    private static final String CONTACT_EMAIL = "";

    private static final String TOKEN_DESCRIPTION = "user auth";

    private static final String TOKEN_MODEL_REF_TYPE = "string";

    private static final String TOKEN_PARAMETER_TYPE = "header";

    private static final String X_ACCESS_TOKEN = "Access-Token";

    private static final String BASE_PACKAGE = "org.apache.skyline.admin.web.controller";

    @Autowired
    private AdminProperties adminProperties;


    @Bean
    public Docket createRestApi() {
        ParameterBuilder commonParam = new ParameterBuilder();

        List<Parameter> pars = new ArrayList<>();
        commonParam.name(X_ACCESS_TOKEN)
                .description(TOKEN_DESCRIPTION)
                .modelRef(new ModelRef(TOKEN_MODEL_REF_TYPE))
                .parameterType(TOKEN_PARAMETER_TYPE)
                .required(false);

        pars.add(commonParam.build());

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(this.apiInfo())
                .enable(adminProperties.getSwagger().isEnable())
                .globalOperationParameters(pars)
                .select()
                .apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public BeanPostProcessor springfoxBeanHandler() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
                if (bean instanceof WebMvcRequestHandlerProvider) {
                    customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
                }
                return bean;
            }

            private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(final List<T> mappings) {
                List<T> copy = mappings.stream()
                        .filter(mapping -> mapping.getPatternParser() == null)
                        .collect(toList());
                mappings.clear();
                mappings.addAll(copy);
            }

            @SuppressWarnings("unchecked")
            private List<RequestMappingInfoHandlerMapping> getHandlerMappings(final Object bean) {
                try {
                    Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
                    field.setAccessible(true);
                    return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        };
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(TITLE).description(DESCRIPTION)
                .version(DEFAULT_SWAGGER_API_VERSION)
                .contact(new Contact(CONTACT_NAME, CONTACT_URL, CONTACT_EMAIL))
                .build();
    }

}
