/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.config;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.PropertyException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.security.support.KeyStoreFactoryBean;

import com.alliander.osgp.platform.cucumber.support.ws.WebServiceTemplateFactory;
import com.alliander.osgp.shared.application.config.AbstractConfig;

@Configuration
public class WebServiceConfig extends AbstractConfig {

    @Value("${application.name}")
    private String applicationName;

    @Value("${base.uri}")
    private String baseUri;

    @Value("${web.service.truststore.location}")
    private String webserviceTruststoreLocation;

    @Value("${web.service.truststore.password}")
    private String webserviceTruststorePassword;

    @Value("${web.service.truststore.type}")
    private String webserviceTruststoreType;

    @Value("${web.service.keystore.basepath}")
    private String webserviceKeystoreLocation;

    @Value("${web.service.keystore.password}")
    private String webserviceKeystorePassword;

    @Value("${web.service.keystore.type}")
    private String webserviceKeystoreType;

    @Value("${web.service.template.default.uri.core.devicemanagement}")
    private String webserviceTemplateDefaultUriCoreDeviceManagement;

    @Value("${jaxb2.marshaller.context.path.core.devicemanagement}")
    private String contextPathCoreDeviceManagement;
    
    @Value("${web.service.template.default.uri.core.deviceinstallation}")
    private String webserviceTemplateDefaultUriCoreDeviceInstallation;

    @Value("${jaxb2.marshaller.context.path.core.deviceinstallation}")
    private String contextPathCoreDeviceInstallation;
    
    @Value("${web.service.template.default.uri.admin.devicemanagement}")
    private String webserviceTemplateDefaultUriAdminDeviceManagement;

    @Value("${jaxb2.marshaller.context.path.admin.devicemanagement}")
    private String contextPathAdminDeviceManagement;

    @Value("${web.service.template.default.uri.microgrids.adhocmanagement}")
    private String webserviceTemplateDefaultUriMicrogridsAdHocManagement;

    @Value("${jaxb2.marshaller.context.path.microgrids.adhocmanagement}")
    private String contextPathMicrogridsAdHocManagement;

    @Bean
    public SaajSoapMessageFactory messageFactory() {
        return new SaajSoapMessageFactory();
    }

    @Bean
    public KeyStoreFactoryBean webServiceTrustStoreFactory() {
        final KeyStoreFactoryBean factory = new KeyStoreFactoryBean();
        factory.setType(this.webserviceTruststoreType);
        factory.setLocation(new FileSystemResource(this.webserviceTruststoreLocation));
        factory.setPassword(this.webserviceTruststorePassword);

        return factory;
    }
    
    @Bean
    public WebServiceTemplateFactory coreDeviceManagementWstf() {
        return new WebServiceTemplateFactory.Builder().setMarshaller(this.coreDeviceManagementMarshaller())
                .setMessageFactory(this.messageFactory())
                .setDefaultUri(this.baseUri.concat(this.webserviceTemplateDefaultUriCoreDeviceManagement))
                .setKeyStoreType(this.webserviceKeystoreType).setKeyStoreLocation(this.webserviceKeystoreLocation)
                .setKeyStorePassword(this.webserviceKeystorePassword)
                .setTrustStoreFactory(this.webServiceTrustStoreFactory()).setApplicationName(this.applicationName)
                .build();
    }

    /**
     * Method for creating the Marshaller for Core DeviceManagement.
     *
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller coreDeviceManagementMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.contextPathCoreDeviceManagement);

        return marshaller;
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for
     * Core DeviceManagement.
     *
     * @return MarshallingPayloadMethodProcessor
     */
    @Bean
    public MarshallingPayloadMethodProcessor coreDeviceManagementMarshallingPayloadMethodProcessor() {
        return new MarshallingPayloadMethodProcessor(this.coreDeviceManagementMarshaller(),
                this.coreDeviceManagementMarshaller());
    }
    
    @Bean
    public WebServiceTemplateFactory coreDeviceInstallationWstf() {
        return new WebServiceTemplateFactory.Builder().setMarshaller(this.coreDeviceInstallationMarshaller())
                .setMessageFactory(this.messageFactory())
                .setDefaultUri(this.baseUri.concat(this.webserviceTemplateDefaultUriCoreDeviceInstallation))
                .setKeyStoreType(this.webserviceKeystoreType).setKeyStoreLocation(this.webserviceKeystoreLocation)
                .setKeyStorePassword(this.webserviceKeystorePassword)
                .setTrustStoreFactory(this.webServiceTrustStoreFactory()).setApplicationName(this.applicationName)
                .build();
    }

    /**
     * Method for creating the Marshaller for Core DeviceInstallation.
     *
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller coreDeviceInstallationMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.contextPathCoreDeviceInstallation);

        return marshaller;
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for
     * Core DeviceInstallation.
     *
     * @return MarshallingPayloadMethodProcessor
     */
    @Bean
    public MarshallingPayloadMethodProcessor coreDeviceInstallationMarshallingPayloadMethodProcessor() {
        return new MarshallingPayloadMethodProcessor(this.coreDeviceInstallationMarshaller(),
                this.coreDeviceInstallationMarshaller());
    }
    
    @Bean
    public WebServiceTemplateFactory adminDeviceManagementWstf() {
        return new WebServiceTemplateFactory.Builder().setMarshaller(this.adminDeviceManagementMarshaller())
                .setMessageFactory(this.messageFactory())
                .setDefaultUri(this.baseUri.concat(this.webserviceTemplateDefaultUriAdminDeviceManagement))
                .setKeyStoreType(this.webserviceKeystoreType).setKeyStoreLocation(this.webserviceKeystoreLocation)
                .setKeyStorePassword(this.webserviceKeystorePassword)
                .setTrustStoreFactory(this.webServiceTrustStoreFactory()).setApplicationName(this.applicationName)
                .build();
    }

    /**
     * Method for creating the Marshaller for Admin DeviceManagement.
     *
     * @return Jaxb2Marshaller
     * @throws PropertyException 
     */
    @Bean
    public Jaxb2Marshaller adminDeviceManagementMarshaller() {
    	final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.contextPathAdminDeviceManagement);
        
        return marshaller;
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for
     * Admin DeviceManagement.
     *
     * @return MarshallingPayloadMethodProcessor
     */
    @Bean
    public MarshallingPayloadMethodProcessor adminDeviceManagementMarshallingPayloadMethodProcessor() {
        return new MarshallingPayloadMethodProcessor(this.adminDeviceManagementMarshaller(),
                this.adminDeviceManagementMarshaller());
    }

    @Bean
    public WebServiceTemplateFactory webServiceTemplateFactoryMicrogridsAdHocManagement() {
        return new WebServiceTemplateFactory.Builder().setMarshaller(this.microgridsAdHocManagementMarshaller())
                .setMessageFactory(this.messageFactory())
                .setDefaultUri(this.baseUri.concat(this.webserviceTemplateDefaultUriMicrogridsAdHocManagement))
                .setKeyStoreType(this.webserviceKeystoreType).setKeyStoreLocation(this.webserviceKeystoreLocation)
                .setKeyStorePassword(this.webserviceKeystorePassword)
                .setTrustStoreFactory(this.webServiceTrustStoreFactory()).setApplicationName(this.applicationName)
                .build();
    }

    /**
     * Method for creating the Marshaller for Microgrids AdHocManagement.
     *
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller microgridsAdHocManagementMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.contextPathMicrogridsAdHocManagement);

        return marshaller;
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for
     * Microgrids AdHocManagement.
     *
     * @return MarshallingPayloadMethodProcessor
     */
    @Bean
    public MarshallingPayloadMethodProcessor microgridsAdHocManagementMarshallingPayloadMethodProcessor() {
        return new MarshallingPayloadMethodProcessor(this.microgridsAdHocManagementMarshaller(),
                this.microgridsAdHocManagementMarshaller());
    }

}
