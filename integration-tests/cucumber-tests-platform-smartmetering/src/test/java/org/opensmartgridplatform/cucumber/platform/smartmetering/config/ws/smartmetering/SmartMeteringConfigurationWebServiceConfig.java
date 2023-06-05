// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.config.ws.smartmetering;

import org.opensmartgridplatform.cucumber.core.config.ws.BaseWebServiceConfig;
import org.opensmartgridplatform.cucumber.platform.smartmetering.config.ApplicationConfiguration;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;

@Configuration
public class SmartMeteringConfigurationWebServiceConfig extends BaseWebServiceConfig {

  @Autowired private ApplicationConfiguration configuration;

  @Bean
  public DefaultWebServiceTemplateFactory smartMeteringConfigurationWebServiceTemplateFactory() {
    return new DefaultWebServiceTemplateFactory.Builder()
        .setMarshaller(this.smartMeteringConfigurationMarshaller())
        .setMessageFactory(this.messageFactory())
        .setTargetUri(
            this.baseUri.concat(
                this.configuration.webserviceTemplateDefaultUriSmartMeteringConfiguration))
        .setKeyStoreType(this.webserviceKeystoreType)
        .setKeyStoreLocation(this.webserviceKeystoreLocation)
        .setKeyStorePassword(this.webserviceKeystorePassword)
        .setTrustStoreFactory(this.webServiceTrustStoreFactory())
        .setApplicationName(this.applicationName)
        .build();
  }

  /**
   * Method for creating the Marshaller for SmartMetering Configuration webservice.
   *
   * @return Jaxb2Marshaller
   */
  @Bean
  public Jaxb2Marshaller smartMeteringConfigurationMarshaller() {
    final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

    marshaller.setContextPath(this.configuration.contextPathSmartMeteringConfiguration);

    return marshaller;
  }

  /**
   * Method for creating the Marshalling Payload Method Processor for SmartMetering Configuration
   * webservice.
   *
   * @return MarshallingPayloadMethodProcessor
   */
  @Bean
  public MarshallingPayloadMethodProcessor
      smartMeteringConfigurationManagementPayloadMethodProcessor() {
    return new MarshallingPayloadMethodProcessor(
        this.smartMeteringConfigurationMarshaller(), this.smartMeteringConfigurationMarshaller());
  }
}
