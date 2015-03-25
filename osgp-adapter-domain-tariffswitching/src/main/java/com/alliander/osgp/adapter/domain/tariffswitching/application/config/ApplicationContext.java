package com.alliander.osgp.adapter.domain.tariffswitching.application.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * An application context Java configuration class. The usage of Java
 * configuration requires Spring Framework 3.0
 */
@Configuration
@ComponentScan(basePackages = { "com.alliander.osgp.domain.core", "com.alliander.osgp.adapter.domain.tariffswitching" })
@EnableTransactionManagement
@PropertySource("file:${osp/osgpAdapterDomainTariffSwitching/config}")
public class ApplicationContext {

}
