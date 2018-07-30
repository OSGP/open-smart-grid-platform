/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.config.ws.publiclighting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;

import org.opensmartgridplatform.cucumber.core.config.ws.BaseWebServiceConfig;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Configuration
public class DeviceMonitoringWebServiceConfig extends BaseWebServiceConfig {

    @Value("${web.service.template.default.uri.publiclighting.devicemonitoring}")
    private String webserviceTemplateDefaultUriPublicLightingDeviceMonitoring;

    @Value("${jaxb2.marshaller.context.path.publiclighting.devicemonitoring}")
    private String contextPathPublicLightingDeviceMonitoring;

    @Bean
    public DefaultWebServiceTemplateFactory publicLightingDeviceMonitoringWstf() {
        return new DefaultWebServiceTemplateFactory.Builder()
                .setMarshaller(this.publiclightingDeviceMonitoringMarshaller()).setMessageFactory(this.messageFactory())
                .setTargetUri(this.baseUri.concat(this.webserviceTemplateDefaultUriPublicLightingDeviceMonitoring))
                .setKeyStoreType(this.webserviceKeystoreType).setKeyStoreLocation(this.webserviceKeystoreLocation)
                .setKeyStorePassword(this.webserviceKeystorePassword)
                .setTrustStoreFactory(this.webServiceTrustStoreFactory()).setApplicationName(this.applicationName)
                .build();
    }

    /**
     * Method for creating the Marshaller for PublicLighting DeviceMonitoring.
     *
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller publiclightingDeviceMonitoringMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.contextPathPublicLightingDeviceMonitoring);

        return marshaller;
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for
     * PublicLighting DeviceMonitoring.
     *
     * @return MarshallingPayloadMethodProcessor
     */
    @Bean
    public MarshallingPayloadMethodProcessor publicLightingDeviceMonitoringMarshallingPayloadMethodProcessor() {
        return new MarshallingPayloadMethodProcessor(this.publiclightingDeviceMonitoringMarshaller(),
                this.publiclightingDeviceMonitoringMarshaller());
    }

}
