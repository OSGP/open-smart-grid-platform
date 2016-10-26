/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.admin.application.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import com.alliander.osgp.adapter.ws.infra.specifications.JpaDeviceSpecifications;
import com.alliander.osgp.adapter.ws.infra.specifications.JpaEventSpecifications;
import com.alliander.osgp.domain.core.specifications.DeviceSpecifications;
import com.alliander.osgp.domain.core.specifications.EventSpecifications;
import com.alliander.osgp.logging.domain.config.ReadOnlyLoggingConfig;
import com.alliander.osgp.shared.application.config.PagingSettings;

/**
 * An application context Java configuration class. The usage of Java
 * configuration requires Spring Framework 3.0
 */
@Configuration
@ComponentScan(basePackages = { "com.alliander.osgp.domain.core", "com.alliander.osgp.adapter.ws.admin",
		"com.alliander.osgp.logging.domain" })
@ImportResource("classpath:applicationContext.xml")
@Import({ MessagingConfig.class, PersistenceConfig.class, WebServiceConfig.class, ReadOnlyLoggingConfig.class })
@PropertySource("file:${osp/osgpAdapterWsAdmin/config}")
public class ApplicationContext {

	private static final String PROPERTY_NAME_DEFAULT_PROTOCOL = "default.protocol";
	private static final String PROPERTY_NAME_DEFAULT_PROTOCOL_VERSION = "default.protocol.version";

	private static final String PROPERTY_NAME_RECENT_DEVICES_PERIOD = "recent.devices.period";

	private static final String PROPERTY_NAME_PAGING_MAXIMUM_PAGE_SIZE = "paging.maximum.pagesize";
	private static final String PROPERTY_NAME_PAGING_DEFAULT_PAGE_SIZE = "paging.default.pagesize";

	@Resource
	private Environment environment;

	@Bean
	public String defaultProtocol() {
		return this.environment.getRequiredProperty(PROPERTY_NAME_DEFAULT_PROTOCOL);
	}

	@Bean
	public String defaultProtocolVersion() {
		return this.environment.getRequiredProperty(PROPERTY_NAME_DEFAULT_PROTOCOL_VERSION);
	}

	@Bean
	public Integer recentDevicesPeriod() {
		return Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_RECENT_DEVICES_PERIOD));
	}

	@Bean
	public PagingSettings pagingSettings() {
		return new PagingSettings(
				Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_PAGING_MAXIMUM_PAGE_SIZE)),
				Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_PAGING_DEFAULT_PAGE_SIZE)));
	}

	@Bean
	public EventSpecifications eventSpecifications() {
		return new JpaEventSpecifications();
	}

	@Bean
	public DeviceSpecifications deviceSpecifications() {
		return new JpaDeviceSpecifications();
	}

	@Bean
	public LocalValidatorFactoryBean validator() {
		final LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		final org.springframework.core.io.Resource[] resources = { new ClassPathResource("constraint-mappings.xml") };
		localValidatorFactoryBean.setMappingLocations(resources);
		return localValidatorFactoryBean;
	}

	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		final MethodValidationPostProcessor m = new MethodValidationPostProcessor();
		m.setValidatorFactory(this.validator());
		return m;
	}
}
