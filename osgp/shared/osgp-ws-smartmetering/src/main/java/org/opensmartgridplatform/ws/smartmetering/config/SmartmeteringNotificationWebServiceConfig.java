//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.ws.smartmetering.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.wsdl.WsdlDefinition;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;

@Configuration
public class SmartmeteringNotificationWebServiceConfig {

  private static final String COMMON_XSD_PATH = "schemas/base-ws-smartmetering.xsd";

  private static final String SMART_METERING_NOTIFICATION_XSD_PATH =
      "schemas/notification-ws-smartmetering.xsd";

  private static final String SMART_METERING_NOTIFICATION_WSDL_PATH =
      "SmartMeteringNotification.wsdl";

  @Bean
  public PayloadValidatingInterceptor payloadValidatingInterceptor() {
    final PayloadValidatingInterceptor payloadValidatingInterceptor =
        new PayloadValidatingInterceptor();
    final Resource[] resources =
        new Resource[] {
          new ClassPathResource(COMMON_XSD_PATH),
          new ClassPathResource(SMART_METERING_NOTIFICATION_XSD_PATH)
        };
    payloadValidatingInterceptor.setSchemas(resources);
    payloadValidatingInterceptor.setValidateRequest(true);
    payloadValidatingInterceptor.setValidateResponse(false);

    return payloadValidatingInterceptor;
  }

  @Bean(name = "common")
  public SimpleXsdSchema commonXsd() {
    return new SimpleXsdSchema(new ClassPathResource(COMMON_XSD_PATH));
  }

  @Bean(name = "SmartMeteringNotification")
  public WsdlDefinition smartMeteringNotificationWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(SMART_METERING_NOTIFICATION_WSDL_PATH));
  }

  @Bean(name = "notification-ws-smartmetering")
  public SimpleXsdSchema smartMeteringNotificationXsd() {
    return new SimpleXsdSchema(new ClassPathResource(SMART_METERING_NOTIFICATION_XSD_PATH));
  }
}
