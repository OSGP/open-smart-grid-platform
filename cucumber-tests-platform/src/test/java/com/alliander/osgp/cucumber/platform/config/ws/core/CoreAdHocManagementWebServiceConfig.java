/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.config.ws.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;

import com.alliander.osgp.cucumber.platform.config.ws.BaseWebServiceConfig;
import com.alliander.osgp.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Configuration
public class CoreAdHocManagementWebServiceConfig extends BaseWebServiceConfig {

    @Value("${web.service.template.default.uri.core.adhocmanagement}")
    private String webserviceTemplateDefaultUriCoreAdHocManagement;

    @Value("${jaxb2.marshaller.context.path.core.adhocmanagement}")
    private String contextPathCoreAdHocManagement;

    @Bean
    public DefaultWebServiceTemplateFactory coreAdHocManagementWstf() {
        return new DefaultWebServiceTemplateFactory.Builder().setMarshaller(this.coreAdHocManagementMarshaller())
                .setMessageFactory(this.messageFactory())
                .setTargetUri(this.baseUri.concat(this.webserviceTemplateDefaultUriCoreAdHocManagement))
                .setKeyStoreType(this.webserviceKeystoreType)
                .setKeyStoreLocation(this.webserviceKeystoreLocation)
                .setKeyStorePassword(this.webserviceKeystorePassword)
                .setTrustStoreFactory(this.webServiceTrustStoreFactory())
                .setApplicationName(this.applicationName)
                .build();
    }

    /**
     * Method for creating the Marshaller for Core AdHocManagement.
     *
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller coreAdHocManagementMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.contextPathCoreAdHocManagement);

        return marshaller;
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for
     * Core AdHocManagement.
     *
     * @return MarshallingPayloadMethodProcessor
     */
    @Bean
    public MarshallingPayloadMethodProcessor coreAdHocManagementMarshallingPayloadMethodProcessor() {
        return new MarshallingPayloadMethodProcessor(this.coreAdHocManagementMarshaller(),
                this.coreAdHocManagementMarshaller());
    }
}
