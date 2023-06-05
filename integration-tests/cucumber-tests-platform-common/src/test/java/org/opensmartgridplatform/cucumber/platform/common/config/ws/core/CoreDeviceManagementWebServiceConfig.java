// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.common.config.ws.core;

import org.opensmartgridplatform.cucumber.core.config.ws.BaseWebServiceConfig;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;

@Configuration
public class CoreDeviceManagementWebServiceConfig extends BaseWebServiceConfig {

  @Value("${web.service.template.default.uri.core.devicemanagement}")
  private String webserviceTemplateDefaultUriCoreDeviceManagement;

  @Value("${jaxb2.marshaller.context.path.core.devicemanagement}")
  private String contextPathCoreDeviceManagement;

  @Bean
  public DefaultWebServiceTemplateFactory coreDeviceManagementWstf() {
    return new DefaultWebServiceTemplateFactory.Builder()
        .setMarshaller(this.coreDeviceManagementMarshaller())
        .setMessageFactory(this.messageFactory())
        .setTargetUri(this.baseUri.concat(this.webserviceTemplateDefaultUriCoreDeviceManagement))
        .setKeyStoreType(this.webserviceKeystoreType)
        .setKeyStoreLocation(this.webserviceKeystoreLocation)
        .setKeyStorePassword(this.webserviceKeystorePassword)
        .setTrustStoreFactory(this.webServiceTrustStoreFactory())
        .setApplicationName(this.applicationName)
        .build();
  }

  /**
   * Method for creating the Marshaller for Core DeviceManagement.
   *
   * @return Jaxb2Marshaller
   */
  @Bean
  public Jaxb2Marshaller coreDeviceManagementMarshaller() {
    final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

    marshaller.setContextPath(this.contextPathCoreDeviceManagement);

    return marshaller;
  }

  /**
   * Method for creating the Marshalling Payload Method Processor for Core DeviceManagement.
   *
   * @return MarshallingPayloadMethodProcessor
   */
  @Bean
  public MarshallingPayloadMethodProcessor coreDeviceManagementMarshallingPayloadMethodProcessor() {
    return new MarshallingPayloadMethodProcessor(
        this.coreDeviceManagementMarshaller(), this.coreDeviceManagementMarshaller());
  }
}
