/*
 * Copyright 2017 Smart Society Services B.V.
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
public class CoreWebServiceConfig {

  private static final String COMMON_XSD_PATH = "schemas/common-ws-core.xsd";
  private static final String ADHOC_MANAGEMENT_XSD_PATH = "schemas/adhocmanagement-ws-core.xsd";
  private static final String CONFIGURATION_MANAGEMENT_XSD_PATH =
      "schemas/configurationmanagement.xsd";
  private static final String DEVICE_INSTALLATION_XSD_PATH = "schemas/deviceinstallation.xsd";
  private static final String DEVICE_MANAGEMENT_XSD_PATH = "schemas/devicemanagement-ws-core.xsd";
  private static final String FIRMWARE_MANAGEMENT_XSD_PATH = "schemas/firmwaremanagement.xsd";

  private static final String ADHOC_MANAGEMENT_WSDL_PATH = "AdHocManagement.wsdl";
  private static final String CONFIGURATION_MANAGEMENT_WSDL_PATH = "ConfigurationManagement.wsdl";
  private static final String DEVICE_INSTALLATION_WSDL_PATH = "DeviceInstallation.wsdl";
  private static final String DEVICE_MANAGEMENT_WSDL_PATH = "DeviceManagement.wsdl";
  private static final String FIRMWARE_MANAGEMENT_WSDL_PATH = "FirmwareManagement.wsdl";

  @Bean
  public PayloadValidatingInterceptor payloadValidatingInterceptor() {
    final PayloadValidatingInterceptor payloadValidatingInterceptor =
        new PayloadValidatingInterceptor();
    final Resource[] resources =
        new Resource[] {
          new ClassPathResource(COMMON_XSD_PATH),
          new ClassPathResource(ADHOC_MANAGEMENT_XSD_PATH),
          new ClassPathResource(CONFIGURATION_MANAGEMENT_XSD_PATH),
          new ClassPathResource(DEVICE_INSTALLATION_XSD_PATH),
          new ClassPathResource(DEVICE_MANAGEMENT_XSD_PATH),
          new ClassPathResource(FIRMWARE_MANAGEMENT_XSD_PATH)
        };
    payloadValidatingInterceptor.setSchemas(resources);
    return payloadValidatingInterceptor;
  }

  @Bean(name = "common-ws-core")
  public SimpleXsdSchema commonXsd() {
    return new SimpleXsdSchema(new ClassPathResource(COMMON_XSD_PATH));
  }

  @Bean(name = "AdHocManagement")
  public WsdlDefinition adHocManagementWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(ADHOC_MANAGEMENT_WSDL_PATH));
  }

  @Bean(name = "adhocmanagement-ws-core")
  public SimpleXsdSchema adhocManagementXsd() {
    return new SimpleXsdSchema(new ClassPathResource(ADHOC_MANAGEMENT_XSD_PATH));
  }

  @Bean(name = "ConfigurationManagement")
  public WsdlDefinition configurationManagementWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(CONFIGURATION_MANAGEMENT_WSDL_PATH));
  }

  @Bean(name = "configurationmanagement")
  public SimpleXsdSchema configurationManagementXsd() {
    return new SimpleXsdSchema(new ClassPathResource(CONFIGURATION_MANAGEMENT_XSD_PATH));
  }

  @Bean(name = "DeviceInstallation")
  public WsdlDefinition deviceInstallationWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(DEVICE_INSTALLATION_WSDL_PATH));
  }

  @Bean(name = "deviceinstallation")
  public SimpleXsdSchema deviceInstallationXsd() {
    return new SimpleXsdSchema(new ClassPathResource(DEVICE_INSTALLATION_XSD_PATH));
  }

  @Bean(name = "DeviceManagement")
  public WsdlDefinition deviceManagementWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(DEVICE_MANAGEMENT_WSDL_PATH));
  }

  @Bean(name = "devicemanagement-ws-core")
  public SimpleXsdSchema deviceManagementXsd() {
    return new SimpleXsdSchema(new ClassPathResource(DEVICE_MANAGEMENT_XSD_PATH));
  }

  @Bean(name = "FirmwareManagement")
  public WsdlDefinition firmwareManagementWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(FIRMWARE_MANAGEMENT_WSDL_PATH));
  }

  @Bean(name = "firmwaremanagement")
  public SimpleXsdSchema firmwareManagementXsd() {
    return new SimpleXsdSchema(new ClassPathResource(FIRMWARE_MANAGEMENT_XSD_PATH));
  }
}
