package com.alliander.osgp.adapter.domain.smartmetering.application.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * An application context Java configuration class. The usage of Java
 * configuration requires Spring Framework 3.0
 */
@Configuration
@ComponentScan(basePackages = { "com.alliander.osgp.domain.core", "com.alliander.osgp.adapter.domain.smartmetering" })
@EnableTransactionManagement
@PropertySource("file:${osp/osgpAdapterDomainSmartMetering/config}")
public class ApplicationContext {

}
