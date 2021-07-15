/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.config.ws.core;

import org.opensmartgridplatform.cucumber.core.config.ws.BaseWebServiceConfig;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;

@Configuration
public class CoreFirmwareManagementWebServiceConfig extends BaseWebServiceConfig {

  @Value("${web.service.template.default.uri.core.firmwaremanagement}")
  private String webserviceTemplateDefaultUriCoreFirmwareManagement;

  @Value("${jaxb2.marshaller.context.path.core.firmwaremanagement}")
  private String contextPathCoreFirmwareManagement;

  @Bean
  public DefaultWebServiceTemplateFactory coreFirmwareManagementWstf() {
    return new DefaultWebServiceTemplateFactory.Builder()
        .setMarshaller(this.coreFirmwareManagementMarshaller())
        .setMessageFactory(this.messageFactory())
        .setTargetUri(this.baseUri.concat(this.webserviceTemplateDefaultUriCoreFirmwareManagement))
        .setKeyStoreType(this.webserviceKeystoreType)
        .setKeyStoreLocation(this.webserviceKeystoreLocation)
        .setKeyStorePassword(this.webserviceKeystorePassword)
        .setTrustStoreFactory(this.webServiceTrustStoreFactory())
        .setApplicationName(this.applicationName)
        .build();
  }

  /**
   * Method for creating the Marshaller for Core FirmwareManagement.
   *
   * @return Jaxb2Marshaller
   */
  @Bean
  public Jaxb2Marshaller coreFirmwareManagementMarshaller() {
    final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

    marshaller.setContextPath(this.contextPathCoreFirmwareManagement);

    return marshaller;
  }

  /**
   * Method for creating the Marshalling Payload Method Processor for Core FirmwareManagement.
   *
   * @return MarshallingPayloadMethodProcessor
   */
  @Bean
  public MarshallingPayloadMethodProcessor
      coreFirmwareManagementMarshallingPayloadMethodProcessor() {
    return new MarshallingPayloadMethodProcessor(
        this.coreFirmwareManagementMarshaller(), this.coreFirmwareManagementMarshaller());
  }
}
