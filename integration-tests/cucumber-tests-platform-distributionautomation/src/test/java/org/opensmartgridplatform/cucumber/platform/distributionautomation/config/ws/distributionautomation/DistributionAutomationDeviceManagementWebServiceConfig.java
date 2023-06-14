// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.distributionautomation.config.ws.distributionautomation;

import org.opensmartgridplatform.cucumber.core.config.ws.BaseWebServiceConfig;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;

@Configuration
public class DistributionAutomationDeviceManagementWebServiceConfig extends BaseWebServiceConfig {

  @Value("${web.service.template.default.uri.distributionautomation.devicemanagement}")
  private String webServiceTemplateDefaultUriDistributionAutomationDeviceManagement;

  @Value("${jaxb2.marshaller.context.path.distributionautomation.devicemanagement}")
  private String contextPathDistributionAutomationDeviceManagement;

  @Bean
  public DefaultWebServiceTemplateFactory
      webServiceTemplateFactoryDistributionAutomationDeviceManagement() {
    return new DefaultWebServiceTemplateFactory.Builder()
        .setMarshaller(this.distributionAutomationDeviceManagementMarshaller())
        .setMessageFactory(this.messageFactory())
        .setTargetUri(
            this.baseUri.concat(
                this.webServiceTemplateDefaultUriDistributionAutomationDeviceManagement))
        .setKeyStoreType(this.webserviceKeystoreType)
        .setKeyStoreLocation(this.webserviceKeystoreLocation)
        .setKeyStorePassword(this.webserviceKeystorePassword)
        .setTrustStoreFactory(this.webServiceTrustStoreFactory())
        .setApplicationName(this.applicationName)
        .build();
  }

  @Bean
  public Jaxb2Marshaller distributionAutomationDeviceManagementMarshaller() {
    final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

    marshaller.setContextPath(this.contextPathDistributionAutomationDeviceManagement);

    return marshaller;
  }

  @Bean
  public MarshallingPayloadMethodProcessor
      distributionAutomationDeviceManagementMarshallingPayloadMethodProcessor() {
    return new MarshallingPayloadMethodProcessor(
        this.distributionAutomationDeviceManagementMarshaller(),
        this.distributionAutomationDeviceManagementMarshaller());
  }
}
