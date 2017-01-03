/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.config.ws.smartmetering;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;

import com.alliander.osgp.platform.cucumber.config.ws.BaseWebServiceConfig;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceTemplateFactory;
import com.alliander.osgp.platform.dlms.cucumber.config.ApplicationConfiguration;

@Configuration
public class SmartMeteringMonitoringWebServiceConfig extends BaseWebServiceConfig {

    @Autowired 
    private ApplicationConfiguration configuration;
    
    @Bean
    public WebServiceTemplateFactory smartMeteringMonitoringManagementWstf() {
        return new WebServiceTemplateFactory.Builder().setMarshaller(this.smartMeteringMonitoringManagementMarshaller())
                .setMessageFactory(this.messageFactory())
                .setDefaultUri(this.baseUri.concat(this.configuration.webserviceTemplateDefaultUriSmartMeteringMonitoringManagement))
                .setKeyStoreType(this.webserviceKeystoreType).setKeyStoreLocation(this.webserviceKeystoreLocation)
                .setKeyStorePassword(this.webserviceKeystorePassword)
                .setTrustStoreFactory(this.webServiceTrustStoreFactory()).setApplicationName(this.applicationName)
                .build();
    }

    /**
     * Method for creating the Marshaller for SmartMetering MonitoringManagement.
     *
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller smartMeteringMonitoringManagementMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.configuration.contextPathSmartMeteringMonitoringManagement);

        return marshaller;
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for
     * SmartMetering MonitoringManagement.
     *
     * @return MarshallingPayloadMethodProcessor
     */
    @Bean
    public MarshallingPayloadMethodProcessor smartMeteringMonitoringManagementMarshallingPayloadMethodProcessor() {
        return new MarshallingPayloadMethodProcessor(this.smartMeteringMonitoringManagementMarshaller(),
                this.smartMeteringMonitoringManagementMarshaller());
    }

}
