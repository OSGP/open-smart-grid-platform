/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.config.ws.tariffswitching;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;

import com.alliander.osgp.platform.cucumber.config.ws.BaseWebServiceConfig;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceTemplateFactory;

@Configuration
public class TariffSwitchingAdhocManagementWebServiceConfig extends BaseWebServiceConfig {

    @Value("${web.service.template.default.uri.tariffswitching.adhocmanagement}")
    private String webserviceTemplateDefaultUriTariffSwitchingAdHocManagement;

    @Value("${jaxb2.marshaller.context.path.tariffswitching.adhocmanagement}")
    private String contextPathTariffSwitchingAdHocManagement;

    @Bean
    public WebServiceTemplateFactory tariffSwitchingAdHocManagementWstf() {
        return new WebServiceTemplateFactory.Builder().setMarshaller(this.tariffSwitchingAdHocManagementMarshaller())
                .setMessageFactory(this.messageFactory())
                .setDefaultUri(this.baseUri.concat(this.webserviceTemplateDefaultUriTariffSwitchingAdHocManagement))
                .setKeyStoreType(this.webserviceKeystoreType).setKeyStoreLocation(this.webserviceKeystoreLocation)
                .setKeyStorePassword(this.webserviceKeystorePassword)
                .setTrustStoreFactory(this.webServiceTrustStoreFactory()).setApplicationName(this.applicationName)
                .build();
    }

    /**
     * Method for creating the Marshaller for TariffSwitching AdHocManagement.
     *
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller tariffSwitchingAdHocManagementMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.contextPathTariffSwitchingAdHocManagement);

        return marshaller;
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for
     * TariffSwitching AdHocManagement.
     *
     * @return MarshallingPayloadMethodProcessor
     */
    @Bean
    public MarshallingPayloadMethodProcessor tariffSwitchingAdHocManagementMarshallingPayloadMethodProcessor() {
        return new MarshallingPayloadMethodProcessor(this.tariffSwitchingAdHocManagementMarshaller(),
                this.tariffSwitchingAdHocManagementMarshaller());
    }

}
