// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.common.config.ws.admin;

import org.opensmartgridplatform.cucumber.core.config.ws.BaseWebServiceConfig;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;

@Configuration
public class AdminDeviceManagementWebServiceConfig extends BaseWebServiceConfig {

  @Value("${web.service.template.default.uri.admin.devicemanagement}")
  private String webserviceTemplateDefaultUriAdminDeviceManagement;

  @Value("${jaxb2.marshaller.context.path.admin.devicemanagement}")
  private String contextPathAdminDeviceManagement;

  @Bean
  public DefaultWebServiceTemplateFactory adminDeviceManagementWstf() {
    return new DefaultWebServiceTemplateFactory.Builder()
        .setMarshaller(this.adminDeviceManagementMarshaller())
        .setMessageFactory(this.messageFactory())
        .setTargetUri(this.baseUri.concat(this.webserviceTemplateDefaultUriAdminDeviceManagement))
        .setKeyStoreType(this.webserviceKeystoreType)
        .setKeyStoreLocation(this.webserviceKeystoreLocation)
        .setKeyStorePassword(this.webserviceKeystorePassword)
        .setTrustStoreFactory(this.webServiceTrustStoreFactory())
        .setApplicationName(this.applicationName)
        .build();
  }

  /**
   * Method for creating the Marshaller for Admin DeviceManagement.
   *
   * @return Jaxb2Marshaller
   */
  @Bean
  public Jaxb2Marshaller adminDeviceManagementMarshaller() {
    final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

    marshaller.setContextPath(this.contextPathAdminDeviceManagement);

    return marshaller;
  }

  /**
   * Method for creating the Marshalling Payload Method Processor for Admin DeviceManagement.
   *
   * @return MarshallingPayloadMethodProcessor
   */
  @Bean
  public MarshallingPayloadMethodProcessor
      adminDeviceManagementMarshallingPayloadMethodProcessor() {
    return new MarshallingPayloadMethodProcessor(
        this.adminDeviceManagementMarshaller(), this.adminDeviceManagementMarshaller());
  }
}
