/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.ws.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.wsdl.WsdlDefinition;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;

@Configuration
public class NotificationWebServiceConfig {

  private static final String COMMON_XSD_PATH = "schemas/common-ws-core.xsd";

  private static final String NOTIFICATION_XSD_PATH = "schemas/notification-ws-core.xsd";

  private static final String NOTIFICATION_WSDL_PATH = "CoreNotification.wsdl";

  @Bean
  public PayloadValidatingInterceptor payloadValidatingInterceptor() {
    final PayloadValidatingInterceptor payloadValidatingInterceptor =
        new PayloadValidatingInterceptor();
    final Resource[] resources =
        new Resource[] {
          new ClassPathResource(COMMON_XSD_PATH), new ClassPathResource(NOTIFICATION_XSD_PATH)
        };
    payloadValidatingInterceptor.setSchemas(resources);
    payloadValidatingInterceptor.setValidateRequest(true);
    payloadValidatingInterceptor.setValidateResponse(false);

    return payloadValidatingInterceptor;
  }

  @Bean(name = "common-ws-core")
  public SimpleXsdSchema commonXsd() {
    return new SimpleXsdSchema(new ClassPathResource(COMMON_XSD_PATH));
  }

  @Bean(name = "CoreNotification")
  public WsdlDefinition notificationWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(NOTIFICATION_WSDL_PATH));
  }

  @Bean(name = "notification-ws-core")
  public SimpleXsdSchema notificationXsd() {
    return new SimpleXsdSchema(new ClassPathResource(NOTIFICATION_XSD_PATH));
  }
}
