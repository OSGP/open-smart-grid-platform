/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.ws.tariffswitching.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.wsdl.WsdlDefinition;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;

@Configuration
public class TariffSwitchingWebServiceConfig {

  private static final String COMMON_XSD_PATH = "schemas/common-ws-tariffswitching.xsd";
  private static final String TS_ADHOCMANAGEMENT_XSD_PATH =
      "schemas/adhocmanagement-ws-tariffswitching.xsd";
  private static final String TS_SCHEDULE_MANAGEMENT_XSD_PATH =
      "schemas/schedulemanagement-ws-tariffswitching.xsd";
  private static final String TS_NOTIFICATION_XSD_PATH =
      "schemas/notification-ws-tariffswitching.xsd";

  private static final String TS_ADHOC_MANAGEMENT_WSDL_PATH = "TariffSwitchingAdHocManagement.wsdl";
  private static final String TS_SCHEDULE_MANAGEMENT_WSDL_PATH =
      "TariffSwitchingScheduleManagement.wsdl";
  private static final String TS_NOTIFICATION_WSDL_PATH = "TariffSwitchingNotification.wsdl";

  @Bean
  public PayloadValidatingInterceptor payloadValidatingInterceptor() {
    final PayloadValidatingInterceptor payloadValidatingInterceptor =
        new PayloadValidatingInterceptor();
    final Resource[] resources =
        new Resource[] {
          new ClassPathResource(COMMON_XSD_PATH),
          new ClassPathResource(TS_ADHOCMANAGEMENT_XSD_PATH),
          new ClassPathResource(TS_SCHEDULE_MANAGEMENT_XSD_PATH)
        };
    payloadValidatingInterceptor.setSchemas(resources);
    return payloadValidatingInterceptor;
  }

  @Bean(name = "common-ws-tariffswitching")
  public SimpleXsdSchema commonXsd() {
    return new SimpleXsdSchema(new ClassPathResource(COMMON_XSD_PATH));
  }

  @Bean(name = "TariffSwitchingAdHocManagement")
  public WsdlDefinition tariffSwitchingAdHocManagementWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(TS_ADHOC_MANAGEMENT_WSDL_PATH));
  }

  @Bean(name = "adhocmanagement-ws-tariffswitching")
  public SimpleXsdSchema tariffSwitchingAdHocManagementXsd() {
    return new SimpleXsdSchema(new ClassPathResource(TS_ADHOCMANAGEMENT_XSD_PATH));
  }

  @Bean(name = "TariffSwitchingScheduleManagement")
  public WsdlDefinition tariffSwitchingScheduleManagementWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(TS_SCHEDULE_MANAGEMENT_WSDL_PATH));
  }

  @Bean(name = "schedulemanagement-ws-tariffswitching")
  public SimpleXsdSchema tariffSwitchingScheduleManagementXsd() {
    return new SimpleXsdSchema(new ClassPathResource(TS_SCHEDULE_MANAGEMENT_XSD_PATH));
  }

  @Bean(name = "TariffSwitchingNotification")
  public WsdlDefinition tariffSwitchingNotificationWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(TS_NOTIFICATION_WSDL_PATH));
  }

  @Bean(name = "notification-ws-tariffswitching")
  public SimpleXsdSchema tariffSwitchingNotificationXsd() {
    return new SimpleXsdSchema(new ClassPathResource(TS_NOTIFICATION_XSD_PATH));
  }
}
