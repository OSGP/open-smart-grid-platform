/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgpfoundation.osgp.webdemoapp.application.config;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.osgpfoundation.osgp.webdemoapp.application.services.KeyStoreHelper;
import org.osgpfoundation.osgp.webdemoapp.application.services.OsgpAdminClientSoapService;
import org.osgpfoundation.osgp.webdemoapp.application.services.OsgpPublicLightingClientSoapService;
import org.osgpfoundation.osgp.webdemoapp.application.services.SoapRequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

/**
 * An application context Java configuration class. The usage of Java
 * configuration requires Spring Framework 3.0 or higher with following
 * exceptions:
 * <ul>
 * <li>@EnableWebMvc annotation requires Spring Framework 3.1</li>
 * </ul>
 */
@Configuration
@ComponentScan(basePackages = { "org.osgpfoundation.osgp.webdemoapp" })
@EnableWebMvc
@ImportResource("classpath:applicationContext.xml")
public class ApplicationContext {

	private static final String VIEW_RESOLVER_PREFIX = "/WEB-INF/views/";
	private static final String VIEW_RESOLVER_SUFFIX = ".jsp";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ApplicationContext.class);

	/**
	 * Method for resolving views.
	 *
	 * @return ViewResolver
	 */
	@Bean
	public ViewResolver viewResolver() {
		final InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();

		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix(VIEW_RESOLVER_PREFIX);
		viewResolver.setSuffix(VIEW_RESOLVER_SUFFIX);

		return viewResolver;
	}

	@Bean
	public OsgpPublicLightingClientSoapService publicLightingClientSoapService () {
		return new OsgpPublicLightingClientSoapService(publicLightingAdHocMapperFacade());
	}
	
	@Bean
	public OsgpAdminClientSoapService osgpAdminClientSoapService () {
		return new OsgpAdminClientSoapService (adminAdHocMapperFacade());
	}

	
	@Bean
	public SoapRequestHelper soapRequestHelper () {
		return new SoapRequestHelper (messageFactory(),keyStoreHelper());
	}
	
	private KeyStoreHelper keyStoreHelper () {

		return new KeyStoreHelper("jks", "/etc/ssl/certs/trust.jks",
				"123456", "/etc/ssl/certs/test-org.pfx", "pkcs12", "1234");
	}
	
	private SaajSoapMessageFactory messageFactory () {
		SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory ();
		try {
			messageFactory.setMessageFactory(MessageFactory.newInstance());
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			System.err.println("Message Facotry failed!");
			e.printStackTrace();
		}
		return messageFactory;
	}
	
	private MapperFacade adminAdHocMapperFacade() {
		MapperFactory factory = new DefaultMapperFactory.Builder().build();
		factory.classMap(
				com.alliander.osgp.platform.ws.schema.admin.devicemanagement.Device.class,
				org.osgpfoundation.osgp.webdemoapp.domain.Device.class)
				.byDefault().register();

		return factory.getMapperFacade();
	}
	
	private MapperFacade publicLightingAdHocMapperFacade() {
		MapperFactory factory = new DefaultMapperFactory.Builder().build();
		factory.classMap(
				com.alliander.osgp.platform.ws.schema.publiclighting.adhocmanagement.Device.class,
				org.osgpfoundation.osgp.webdemoapp.domain.Device.class)
				.byDefault().register();

		return factory.getMapperFacade();
	}

}
