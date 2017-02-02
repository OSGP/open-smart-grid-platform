/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.automatictests.platform.dlms.config.ws.smartmetering;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;

<<<<<<< HEAD:automatictests-platform-dlms/src/test/java/com/alliander/osgp/automatictests/platform/dlms/config/ws/smartmetering/SmartMeteringConfigurationWebServiceConfig.java
import com.alliander.osgp.automatictests.platform.config.ws.BaseWebServiceConfig;
import com.alliander.osgp.automatictests.platform.dlms.config.ApplicationConfiguration;
=======
import com.alliander.osgp.platform.cucumber.config.ws.BaseWebServiceConfig;
import com.alliander.osgp.platform.dlms.cucumber.config.ApplicationConfiguration;
>>>>>>> 1d7b6b21b837cf95c9ffee3feb3efe9aec24d57c:cucumber-tests-platform-dlms/src/test/java/com/alliander/osgp/platform/dlms/cucumber/config/ws/smartmetering/SmartMeteringConfigurationWebServiceConfig.java
import com.alliander.osgp.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Configuration
public class SmartMeteringConfigurationWebServiceConfig extends BaseWebServiceConfig {

    @Autowired
    private ApplicationConfiguration configuration;

    @Bean
    public DefaultWebServiceTemplateFactory smartMeteringConfigurationManagementWstf() {
        return new DefaultWebServiceTemplateFactory.Builder().setMarshaller(this.smartMeteringConfigurationManagementMarshaller())
                .setMessageFactory(this.messageFactory())
                .setTargetUri(this.baseUri.concat(this.configuration.webserviceTemplateDefaultUriSmartMeteringConfigurationManagement))
                .setKeyStoreType(this.webserviceKeystoreType).setKeyStoreLocation(this.webserviceKeystoreLocation)
                .setKeyStorePassword(this.webserviceKeystorePassword)
                .setTrustStoreFactory(this.webServiceTrustStoreFactory()).setApplicationName(this.applicationName)
                .build();
    }

    /**
     * Method for creating the Marshaller for SmartMetering ConfigurationManagement.
     *
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller smartMeteringConfigurationManagementMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.configuration.contextPathSmartMeteringConfigurationManagement);

        return marshaller;
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for
     * SmartMetering ConfigurationManagement.
     *
     * @return MarshallingPayloadMethodProcessor
     */
    @Bean
    public MarshallingPayloadMethodProcessor smartMeteringConfigurationManagementMarshallingPayloadMethodProcessor() {
        return new MarshallingPayloadMethodProcessor(this.smartMeteringConfigurationManagementMarshaller(),
                this.smartMeteringConfigurationManagementMarshaller());
    }

}
