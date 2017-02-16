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
public class CoreDeviceInstallationWebServiceConfig extends BaseWebServiceConfig {

    @Value("${web.service.template.default.uri.core.deviceinstallation}")
    private String webserviceTemplateDefaultUriCoreDeviceInstallation;

    @Value("${jaxb2.marshaller.context.path.core.deviceinstallation}")
    private String contextPathCoreDeviceInstallation;

    @Bean
    public DefaultWebServiceTemplateFactory coreDeviceInstallationWstf() {
        return new DefaultWebServiceTemplateFactory.Builder().setMarshaller(this.coreDeviceInstallationMarshaller())
                .setMessageFactory(this.messageFactory())
                .setTargetUri(this.baseUri.concat(this.webserviceTemplateDefaultUriCoreDeviceInstallation))
                .setKeyStoreType(this.webserviceKeystoreType)
                .setKeyStoreLocation(this.webserviceKeystoreLocation)
                .setKeyStorePassword(this.webserviceKeystorePassword)
                .setTrustStoreFactory(this.webServiceTrustStoreFactory())
                .setApplicationName(this.applicationName)
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
}
