// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.config.ws.tariffswitching;

import org.opensmartgridplatform.cucumber.core.config.ws.BaseWebServiceConfig;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;

@Configuration
public class TariffSwitchingAdhocManagementWebServiceConfig extends BaseWebServiceConfig {

  @Value("${web.service.template.default.uri.tariffswitching.adhocmanagement}")
  private String webserviceTemplateDefaultUriTariffSwitchingAdHocManagement;

  @Value("${jaxb2.marshaller.context.path.tariffswitching.adhocmanagement}")
  private String contextPathTariffSwitchingAdHocManagement;

  @Bean
  public DefaultWebServiceTemplateFactory tariffSwitchingAdHocManagementWstf() {
    return new DefaultWebServiceTemplateFactory.Builder()
        .setMarshaller(this.tariffSwitchingAdHocManagementMarshaller())
        .setMessageFactory(this.messageFactory())
        .setTargetUri(
            this.baseUri.concat(this.webserviceTemplateDefaultUriTariffSwitchingAdHocManagement))
        .setKeyStoreType(this.webserviceKeystoreType)
        .setKeyStoreLocation(this.webserviceKeystoreLocation)
        .setKeyStorePassword(this.webserviceKeystorePassword)
        .setTrustStoreFactory(this.webServiceTrustStoreFactory())
        .setApplicationName(this.applicationName)
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
   * Method for creating the Marshalling Payload Method Processor for TariffSwitching
   * AdHocManagement.
   *
   * @return MarshallingPayloadMethodProcessor
   */
  @Bean
  public MarshallingPayloadMethodProcessor
      tariffSwitchingAdHocManagementMarshallingPayloadMethodProcessor() {
    return new MarshallingPayloadMethodProcessor(
        this.tariffSwitchingAdHocManagementMarshaller(),
        this.tariffSwitchingAdHocManagementMarshaller());
  }
}
