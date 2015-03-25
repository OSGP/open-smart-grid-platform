package com.alliander.osgp.adapter.ws.tariffswitching.application.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * An application context Java configuration class. The usage of Java
 * configuration requires Spring Framework 3.0
 */
@Configuration
@ComponentScan(basePackages = { "com.alliander.osgp.domain.core", "com.alliander.osgp.adapter.ws.tariffswitching" })
@EnableTransactionManagement()
@ImportResource("classpath:applicationContext.xml")
@Import({ PersistenceConfig.class, MessagingConfig.class, WebServiceConfig.class })
@PropertySource("file:${osp/osgpAdapterWsTariffSwitching/config}")
public class ApplicationContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);

    /**
     * @return
     */
    @Bean
    public LocalValidatorFactoryBean validator() {
        LOGGER.debug("Initializing Local Validator Factory Bean");
        final LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        final org.springframework.core.io.Resource[] resources = { new ClassPathResource("constraint-mappings.xml") };
        localValidatorFactoryBean.setMappingLocations(resources);
        return localValidatorFactoryBean;
    }

    /**
     * @return
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        LOGGER.debug("Initializing Method Validation Post Processor Bean");

        final MethodValidationPostProcessor m = new MethodValidationPostProcessor();
        m.setValidatorFactory(this.validator());
        return m;
    }

}
