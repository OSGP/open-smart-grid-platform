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
public class SmartMeteringManagementWebServiceConfig extends BaseWebServiceConfig {

    @Autowired
    private ApplicationConfiguration configuration;
    
    @Bean
    public WebServiceTemplateFactory smartMeteringManagementWstf() {
        return new WebServiceTemplateFactory.Builder().setMarshaller(this.smartMeteringManagementMarshaller())
                .setMessageFactory(this.messageFactory())
                .setDefaultUri(this.baseUri.concat(this.configuration.webserviceTemplateDefaultUriSmartMeteringManagement))
                .setKeyStoreType(this.webserviceKeystoreType).setKeyStoreLocation(this.webserviceKeystoreLocation)
                .setKeyStorePassword(this.webserviceKeystorePassword)
                .setTrustStoreFactory(this.webServiceTrustStoreFactory()).setApplicationName(this.applicationName)
                .build();
    }

    /**
     * Method for creating the Marshaller for SmartMetering Management.
     *
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller smartMeteringManagementMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.configuration.contextPathSmartMeteringManagement);

        return marshaller;
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for
     * SmartMetering Management.
     *
     * @return MarshallingPayloadMethodProcessor
     */
    @Bean
    public MarshallingPayloadMethodProcessor smartMeteringManagementMarshallingPayloadMethodProcessor() {
        return new MarshallingPayloadMethodProcessor(this.smartMeteringManagementMarshaller(),
                this.smartMeteringManagementMarshaller());
    }

}
