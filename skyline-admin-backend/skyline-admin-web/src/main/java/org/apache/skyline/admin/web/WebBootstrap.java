package org.apache.skyline.admin.web;

import org.apache.skyline.admin.web.config.SwaggerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

/**
 * @author jojocodex
 * @version @Id: WebBootstrap.java, v 0.1 2022年12月21日 15:57 jojocodex Exp $
 */
@EnableAutoConfiguration
@ImportResource("classpath*:META-INF/spring/module-*.xml")
@Import(SwaggerConfiguration.class)
public class WebBootstrap {

    public static void main(String[] args) {

        SpringApplication.run(WebBootstrap.class, args);

    }
}