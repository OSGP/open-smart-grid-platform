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
      "schemas/configurationmanagement-ws-core.xsd";
  private static final String DEVICE_INSTALLATION_XSD_PATH =
      "schemas/deviceinstallation-ws-core.xsd";
  private static final String DEVICE_MANAGEMENT_XSD_PATH = "schemas/devicemanagement-ws-core.xsd";
  private static final String FIRMWARE_MANAGEMENT_XSD_PATH =
      "schemas/firmwaremanagement-ws-core.xsd";

  private static final String ADHOC_MANAGEMENT_WSDL_PATH = "CoreAdHocManagement.wsdl";
  private static final String CONFIGURATION_MANAGEMENT_WSDL_PATH =
      "CoreConfigurationManagement.wsdl";
  private static final String DEVICE_INSTALLATION_WSDL_PATH = "CoreDeviceInstallation.wsdl";
  private static final String DEVICE_MANAGEMENT_WSDL_PATH = "CoreDeviceManagement.wsdl";
  private static final String FIRMWARE_MANAGEMENT_WSDL_PATH = "CoreFirmwareManagement.wsdl";

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

  @Bean(name = "CoreAdHocManagement")
  public WsdlDefinition adHocManagementWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(ADHOC_MANAGEMENT_WSDL_PATH));
  }

  @Bean(name = "adhocmanagement-ws-core")
  public SimpleXsdSchema adhocManagementXsd() {
    return new SimpleXsdSchema(new ClassPathResource(ADHOC_MANAGEMENT_XSD_PATH));
  }

  @Bean(name = "CoreConfigurationManagement")
  public WsdlDefinition configurationManagementWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(CONFIGURATION_MANAGEMENT_WSDL_PATH));
  }

  @Bean(name = "configurationmanagement-ws-core")
  public SimpleXsdSchema configurationManagementXsd() {
    return new SimpleXsdSchema(new ClassPathResource(CONFIGURATION_MANAGEMENT_XSD_PATH));
  }

  @Bean(name = "CoreDeviceInstallation")
  public WsdlDefinition deviceInstallationWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(DEVICE_INSTALLATION_WSDL_PATH));
  }

  @Bean(name = "deviceinstallation-ws-core")
  public SimpleXsdSchema deviceInstallationXsd() {
    return new SimpleXsdSchema(new ClassPathResource(DEVICE_INSTALLATION_XSD_PATH));
  }

  @Bean(name = "CoreDeviceManagement")
  public WsdlDefinition deviceManagementWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(DEVICE_MANAGEMENT_WSDL_PATH));
  }

  @Bean(name = "devicemanagement-ws-core")
  public SimpleXsdSchema deviceManagementXsd() {
    return new SimpleXsdSchema(new ClassPathResource(DEVICE_MANAGEMENT_XSD_PATH));
  }

  @Bean(name = "CoreFirmwareManagement")
  public WsdlDefinition firmwareManagementWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(FIRMWARE_MANAGEMENT_WSDL_PATH));
  }

  @Bean(name = "firmwaremanagement-ws-core")
  public SimpleXsdSchema firmwareManagementXsd() {
    return new SimpleXsdSchema(new ClassPathResource(FIRMWARE_MANAGEMENT_XSD_PATH));
  }
}
