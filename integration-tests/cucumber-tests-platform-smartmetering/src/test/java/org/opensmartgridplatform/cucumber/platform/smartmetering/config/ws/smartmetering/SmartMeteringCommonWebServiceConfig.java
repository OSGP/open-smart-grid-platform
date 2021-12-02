/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.config.ws.smartmetering;

import org.opensmartgridplatform.cucumber.core.config.ws.BaseWebServiceConfig;
import org.opensmartgridplatform.cucumber.platform.smartmetering.config.ApplicationConfiguration;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;

@Configuration
public class SmartMeteringCommonWebServiceConfig extends BaseWebServiceConfig {

  @Autowired private ApplicationConfiguration configuration;

  @Bean
  public DefaultWebServiceTemplateFactory smartMeteringCommonWebServiceTemplateFactory() {
    return new DefaultWebServiceTemplateFactory.Builder()
        .setMarshaller(this.smartMeteringCommonMarshaller())
        .setMessageFactory(this.messageFactory())
        .setTargetUri(
            this.baseUri.concat(this.configuration.webserviceTemplateDefaultUriSmartMeteringCommon))
        .setKeyStoreType(this.webserviceKeystoreType)
        .setKeyStoreLocation(this.webserviceKeystoreLocation)
        .setKeyStorePassword(this.webserviceKeystorePassword)
        .setTrustStoreFactory(this.webServiceTrustStoreFactory())
        .setApplicationName(this.applicationName)
        .build();
  }

  /**
   * Method for creating the Marshaller for SmartMetering Bundle webservice.
   *
   * @return Jaxb2Marshaller
   */
  @Bean
  public Jaxb2Marshaller smartMeteringCommonMarshaller() {
    final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

    marshaller.setContextPath(this.configuration.contextPathSmartMeteringCommon);

    return marshaller;
  }

  /**
   * Method for creating the Marshalling Payload Method Processor for SmartMetering Bundle
   * webservice.
   *
   * @return MarshallingPayloadMethodProcessor
   */
  @Bean
  public MarshallingPayloadMethodProcessor smartMeteringCommonMarshallingPayloadMethodProcessor() {
    return new MarshallingPayloadMethodProcessor(
        this.smartMeteringCommonMarshaller(), this.smartMeteringCommonMarshaller());
  }
}
