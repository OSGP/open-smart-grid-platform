/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
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
public class CoreDeviceInstallationWebServiceConfig extends BaseWebServiceConfig {

  @Value("${web.service.template.default.uri.core.deviceinstallation}")
  private String webserviceTemplateDefaultUriCoreDeviceInstallation;

  @Value("${jaxb2.marshaller.context.path.core.deviceinstallation}")
  private String contextPathCoreDeviceInstallation;

  @Bean
  public DefaultWebServiceTemplateFactory coreDeviceInstallationWstf() {
    return new DefaultWebServiceTemplateFactory.Builder()
        .setMarshaller(this.coreDeviceInstallationMarshaller())
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
   * Method for creating the Marshalling Payload Method Processor for Core DeviceInstallation.
   *
   * @return MarshallingPayloadMethodProcessor
   */
  @Bean
  public MarshallingPayloadMethodProcessor
      coreDeviceInstallationMarshallingPayloadMethodProcessor() {
    return new MarshallingPayloadMethodProcessor(
        this.coreDeviceInstallationMarshaller(), this.coreDeviceInstallationMarshaller());
  }
}
