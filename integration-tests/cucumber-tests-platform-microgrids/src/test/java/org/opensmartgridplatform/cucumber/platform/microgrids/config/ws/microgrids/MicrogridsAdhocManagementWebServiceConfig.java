//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.microgrids.config.ws.microgrids;

import org.opensmartgridplatform.cucumber.core.config.ws.BaseWebServiceConfig;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;

@Configuration
public class MicrogridsAdhocManagementWebServiceConfig extends BaseWebServiceConfig {

  @Value("${web.service.template.default.uri.microgrids.adhocmanagement}")
  private String webserviceTemplateDefaultUriMicrogridsAdHocManagement;

  @Value("${jaxb2.marshaller.context.path.microgrids.adhocmanagement}")
  private String contextPathMicrogridsAdHocManagement;

  @Bean
  public DefaultWebServiceTemplateFactory webServiceTemplateFactoryMicrogridsAdHocManagement() {
    return new DefaultWebServiceTemplateFactory.Builder()
        .setMarshaller(this.microgridsAdHocManagementMarshaller())
        .setMessageFactory(this.messageFactory())
        .setTargetUri(
            this.baseUri.concat(this.webserviceTemplateDefaultUriMicrogridsAdHocManagement))
        .setKeyStoreType(this.webserviceKeystoreType)
        .setKeyStoreLocation(this.webserviceKeystoreLocation)
        .setKeyStorePassword(this.webserviceKeystorePassword)
        .setTrustStoreFactory(this.webServiceTrustStoreFactory())
        .setApplicationName(this.applicationName)
        .build();
  }

  /**
   * Method for creating the Marshaller for Microgrids AdHocManagement.
   *
   * @return Jaxb2Marshaller
   */
  @Bean
  public Jaxb2Marshaller microgridsAdHocManagementMarshaller() {
    final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

    marshaller.setContextPath(this.contextPathMicrogridsAdHocManagement);

    return marshaller;
  }

  /**
   * Method for creating the Marshalling Payload Method Processor for Microgrids AdHocManagement.
   *
   * @return MarshallingPayloadMethodProcessor
   */
  @Bean
  public MarshallingPayloadMethodProcessor
      microgridsAdHocManagementMarshallingPayloadMethodProcessor() {
    return new MarshallingPayloadMethodProcessor(
        this.microgridsAdHocManagementMarshaller(), this.microgridsAdHocManagementMarshaller());
  }
}
