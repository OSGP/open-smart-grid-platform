//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.publiclighting.config.ws.tariffswitching;

import org.opensmartgridplatform.cucumber.core.config.ws.BaseWebServiceConfig;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;

@Configuration
public class TariffSwitchingScheduleManagementWebServiceConfig extends BaseWebServiceConfig {

  @Value("${web.service.template.default.uri.tariffswitching.schedulemanagement}")
  private String webserviceTemplateDefaultUriTariffSwitchingScheduleManagement;

  @Value("${jaxb2.marshaller.context.path.tariffswitching.schedulemanagement}")
  private String contextPathTariffSwitchingScheduleManagement;

  @Bean
  public DefaultWebServiceTemplateFactory tariffSwitchingScheduleManagementWstf() {
    return new DefaultWebServiceTemplateFactory.Builder()
        .setMarshaller(this.tariffSwitchingScheduleManagementMarshaller())
        .setMessageFactory(this.messageFactory())
        .setTargetUri(
            this.baseUri.concat(this.webserviceTemplateDefaultUriTariffSwitchingScheduleManagement))
        .setKeyStoreType(this.webserviceKeystoreType)
        .setKeyStoreLocation(this.webserviceKeystoreLocation)
        .setKeyStorePassword(this.webserviceKeystorePassword)
        .setTrustStoreFactory(this.webServiceTrustStoreFactory())
        .setApplicationName(this.applicationName)
        .build();
  }

  /**
   * Method for creating the Marshaller for TariffSwitching ScheduleManagement.
   *
   * @return Jaxb2Marshaller
   */
  @Bean
  public Jaxb2Marshaller tariffSwitchingScheduleManagementMarshaller() {
    final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

    marshaller.setContextPath(this.contextPathTariffSwitchingScheduleManagement);

    return marshaller;
  }

  /**
   * Method for creating the Marshalling Payload Method Processor for TariffSwitching
   * ScheduleManagement.
   *
   * @return MarshallingPayloadMethodProcessor
   */
  @Bean
  public MarshallingPayloadMethodProcessor
      tariffSwitchingAdHocManagementMarshallingPayloadMethodProcessor() {
    return new MarshallingPayloadMethodProcessor(
        this.tariffSwitchingScheduleManagementMarshaller(),
        this.tariffSwitchingScheduleManagementMarshaller());
  }
}
