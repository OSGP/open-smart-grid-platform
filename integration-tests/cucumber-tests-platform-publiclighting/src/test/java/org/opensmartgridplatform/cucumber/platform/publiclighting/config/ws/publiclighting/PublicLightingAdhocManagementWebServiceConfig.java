// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.config.ws.publiclighting;

import org.opensmartgridplatform.cucumber.core.config.ws.BaseWebServiceConfig;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;

@Configuration
public class PublicLightingAdhocManagementWebServiceConfig extends BaseWebServiceConfig {

  @Value("${web.service.template.default.uri.publiclighting.adhocmanagement}")
  private String webserviceTemplateDefaultUriPublicLightingAdHocManagement;

  @Value("${jaxb2.marshaller.context.path.publiclighting.adhocmanagement}")
  private String contextPathPublicLightingAdHocManagement;

  @Bean
  public DefaultWebServiceTemplateFactory publicLightingAdHocManagementWstf() {
    return new DefaultWebServiceTemplateFactory.Builder()
        .setMarshaller(this.publiclightingAdHocManagementMarshaller())
        .setMessageFactory(this.messageFactory())
        .setTargetUri(
            this.baseUri.concat(this.webserviceTemplateDefaultUriPublicLightingAdHocManagement))
        .setKeyStoreType(this.webserviceKeystoreType)
        .setKeyStoreLocation(this.webserviceKeystoreLocation)
        .setKeyStorePassword(this.webserviceKeystorePassword)
        .setTrustStoreFactory(this.webServiceTrustStoreFactory())
        .setApplicationName(this.applicationName)
        .build();
  }

  /**
   * Method for creating the Marshaller for PublicLighting AdHocManagement.
   *
   * @return Jaxb2Marshaller
   */
  @Bean
  public Jaxb2Marshaller publiclightingAdHocManagementMarshaller() {
    final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

    marshaller.setContextPath(this.contextPathPublicLightingAdHocManagement);

    return marshaller;
  }

  /**
   * Method for creating the Marshalling Payload Method Processor for PublicLighting
   * AdHocManagement.
   *
   * @return MarshallingPayloadMethodProcessor
   */
  @Bean
  public MarshallingPayloadMethodProcessor
      publicLightingAdHocManagementMarshallingPayloadMethodProcessor() {
    return new MarshallingPayloadMethodProcessor(
        this.publiclightingAdHocManagementMarshaller(),
        this.publiclightingAdHocManagementMarshaller());
  }
}
