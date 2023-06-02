//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.common.config.ws.core;

import org.opensmartgridplatform.cucumber.core.config.ws.BaseWebServiceConfig;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;

@Configuration
public class CoreConfigurationManagementWebServiceConfig extends BaseWebServiceConfig {

  @Value("${web.service.template.default.uri.core.configurationmanagement}")
  private String webserviceTemplateDefaultUriCoreConfigurationManagement;

  @Value("${jaxb2.marshaller.context.path.core.configurationmanagement}")
  private String contextPathCoreConfigurationManagement;

  @Bean
  public DefaultWebServiceTemplateFactory coreConfigurationManagementWstf() {
    return new DefaultWebServiceTemplateFactory.Builder()
        .setMarshaller(this.coreConfigurationManagementMarshaller())
        .setMessageFactory(this.messageFactory())
        .setTargetUri(
            this.baseUri.concat(this.webserviceTemplateDefaultUriCoreConfigurationManagement))
        .setKeyStoreType(this.webserviceKeystoreType)
        .setKeyStoreLocation(this.webserviceKeystoreLocation)
        .setKeyStorePassword(this.webserviceKeystorePassword)
        .setTrustStoreFactory(this.webServiceTrustStoreFactory())
        .setApplicationName(this.applicationName)
        .build();
  }

  /**
   * Method for creating the Marshaller for Core ConfigurationManagement.
   *
   * @return Jaxb2Marshaller
   */
  @Bean
  public Jaxb2Marshaller coreConfigurationManagementMarshaller() {
    final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

    marshaller.setContextPath(this.contextPathCoreConfigurationManagement);

    return marshaller;
  }

  /**
   * Method for creating the Marshalling Payload Method Processor for Core ConfigurationManagement.
   *
   * @return MarshallingPayloadMethodProcessor
   */
  @Bean
  public MarshallingPayloadMethodProcessor
      coreConfigurationManagementMarshallingPayloadMethodProcessor() {
    return new MarshallingPayloadMethodProcessor(
        this.coreConfigurationManagementMarshaller(), this.coreConfigurationManagementMarshaller());
  }
}
