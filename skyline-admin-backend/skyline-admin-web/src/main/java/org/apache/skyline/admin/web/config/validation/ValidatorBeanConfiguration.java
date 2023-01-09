package org.apache.skyline.admin.web.config.validation;


import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;

/**
 * @author hejianbing
 * @version @Id: ValidatorBeanConfiguration.java, v 0.1 2023年01月09日 11:51 hejianbing Exp $
 */
public class ValidatorBeanConfiguration {

    @Bean
    public Validator validator(AutowireCapableBeanFactory springFactory) {
        try (ValidatorFactory factory = Validation.byProvider(HibernateValidator.class)
                .configure()
                // 快速失败
                .failFast(true)
                // 解决 SpringBoot 依赖注入问题
                .constraintValidatorFactory(new SpringConstraintValidatorFactory(springFactory))
                .buildValidatorFactory()) {
            return factory.getValidator();
        }
    }

}