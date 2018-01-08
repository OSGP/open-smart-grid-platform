/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgpfoundation.osgp.webdemoapp.application.config;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;

import org.osgpfoundation.osgp.webdemoapp.application.services.OsgpAdminClientSoapService;
import org.osgpfoundation.osgp.webdemoapp.application.services.OsgpPublicLightingClientSoapService;
import org.osgpfoundation.osgp.webdemoapp.infra.platform.KeyStoreHelper;
import org.osgpfoundation.osgp.webdemoapp.infra.platform.SoapRequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

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
@PropertySources({ @PropertySource("classpath:web-demo-app.properties"),
        @PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
        @PropertySource(value = "file:${osgp/WebDemoApp/config}", ignoreResourceNotFound = true), })
public class ApplicationContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);

    private static final String VIEW_RESOLVER_PREFIX = "/WEB-INF/views/";
    private static final String VIEW_RESOLVER_SUFFIX = ".jsp";

    @Value("${web.service.truststore.location}")
    private String truststoreLocation;

    @Value("${web.service.truststore.type}")
    private String truststoreType;

    @Value("${web.service.truststore.password}")
    private String truststorePw;

    @Value("${web.service.keystore.location}")
    private String keystoreLocation;

    @Value("${web.service.keystore.type}")
    private String keystoreType;

    @Value("${web.service.keystore.password}")
    private String keystorePw;

    @Value("${web.service.client.certificate}")
    private String clientCertificate;

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

    /**
     * Bean for PublicLightingSoapClientService
     *
     * @return OsgpPublicLightingClientSoapService
     */
    @Bean
    public OsgpPublicLightingClientSoapService publicLightingClientSoapService() {
        return new OsgpPublicLightingClientSoapService(this.publicLightingAdHocMapperFacade());
    }

    /**
     * Bean for AdminClientSoapService
     *
     * @return OsgpAdminClientSoapService
     */
    @Bean
    public OsgpAdminClientSoapService osgpAdminClientSoapService() {
        return new OsgpAdminClientSoapService(this.adminAdHocMapperFacade());
    }

    /**
     * Bean for SoapRequestHelper, contains functions to create
     * WebServiceTemplates for specific domains.
     *
     * @return SoapRequestHelper
     */
    @Bean
    public SoapRequestHelper soapRequestHelper() {
        return new SoapRequestHelper(this.messageFactory(), this.keyStoreHelper());
    }

    /**
     * Returns a configured Keystore helper, which contains all security
     * settings for making singed soap requests.
     *
     * @return KeyStoreHelper
     */
    private KeyStoreHelper keyStoreHelper() {
        return new KeyStoreHelper(this.truststoreType, this.truststoreLocation, this.truststorePw,
                this.keystoreLocation + this.clientCertificate, this.keystoreType, this.keystorePw);
    }

    /**
     * Spring SoapMessageFactory for creating Soap Messages
     *
     * @return SaajSoapMessageFactory
     */
    private SaajSoapMessageFactory messageFactory() {
        final SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory();
        try {
            messageFactory.setMessageFactory(MessageFactory.newInstance());
        } catch (final SOAPException e) {
            LOGGER.error("Unable to set MessageFactory instance", e);
        }
        return messageFactory;
    }

    /**
     * Customized mapper facade for Orika
     *
     * @return MapperFacade
     */
    private MapperFacade adminAdHocMapperFacade() {
        final MapperFactory factory = new DefaultMapperFactory.Builder().build();
        factory.classMap(com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.Device.class,
                org.osgpfoundation.osgp.webdemoapp.domain.Device.class).byDefault().register();

        return factory.getMapperFacade();
    }

    /**
     * Customized mapper facade for Orika
     *
     * @return MapperFacade
     */
    private MapperFacade publicLightingAdHocMapperFacade() {
        final MapperFactory factory = new DefaultMapperFactory.Builder().build();
        factory.classMap(com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.Device.class,
                org.osgpfoundation.osgp.webdemoapp.domain.Device.class).byDefault().register();

        return factory.getMapperFacade();
    }

}
