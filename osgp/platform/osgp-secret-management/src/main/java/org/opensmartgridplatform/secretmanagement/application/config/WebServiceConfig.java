/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.config;

import java.util.Properties;
import org.opensmartgridplatform.secretmanagement.application.exception.DetailSoapFaultMappingExceptionResolver;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointExceptionResolver;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.XsdSchemaCollection;
import org.springframework.xml.xsd.commons.CommonsXsdSchemaCollection;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

  private static final String SECRET_MANAGEMENT_WS_BASE_PATH = "/ws/SecretManagement/*";
  private static final String SECRET_MANAGEMENT_PORT = "SecretManagementPort";
  private static final String SECRET_MANAGEMENT_URI = "/ws/SecretManagement";
  private static final String SECRET_MANAGEMENT_NS =
      "http://www.opensmartgridplatform" + ".org/schemas/security/secretmanagement";
  private static final String SECRET_MANAGEMENT_SCHEMA_LOC = "schemas/secret-management.xsd";

  @Bean
  public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
      final ApplicationContext applicationContext) {
    final MessageDispatcherServlet servlet = new MessageDispatcherServlet();
    servlet.setApplicationContext(applicationContext);
    servlet.setTransformWsdlLocations(true);
    return new ServletRegistrationBean<>(servlet, SECRET_MANAGEMENT_WS_BASE_PATH);
  }

  /**
   * url of the WSDL by this definition is:
   *
   * <p>http://localhost:8080/ws/SecretManagement/secretManagement.wsdl
   */
  @Bean(name = "secretManagement")
  public DefaultWsdl11Definition defaultWsdl11Definition(
      final XsdSchemaCollection secretManagementSchemas) {
    final DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
    wsdl11Definition.setPortTypeName(SECRET_MANAGEMENT_PORT);
    wsdl11Definition.setLocationUri(SECRET_MANAGEMENT_URI);
    wsdl11Definition.setTargetNamespace(SECRET_MANAGEMENT_NS);
    wsdl11Definition.setSchemaCollection(secretManagementSchemas);
    return wsdl11Definition;
  }

  @Bean
  public XsdSchemaCollection secretManagementSchemas() {
    final CommonsXsdSchemaCollection sc = new CommonsXsdSchemaCollection();
    sc.setXsds(new ClassPathResource(SECRET_MANAGEMENT_SCHEMA_LOC));
    return sc;
  }

  @Bean
  public EndpointExceptionResolver exceptionResolver() {
    final SoapFaultMappingExceptionResolver exceptionResolver =
        new DetailSoapFaultMappingExceptionResolver();

    final SoapFaultDefinition faultDefinition = new SoapFaultDefinition();
    faultDefinition.setFaultCode(SoapFaultDefinition.SERVER);
    exceptionResolver.setDefaultFault(faultDefinition);

    final Properties errorMappings = new Properties();
    errorMappings.setProperty(Exception.class.getName(), SoapFaultDefinition.SERVER.toString());
    errorMappings.setProperty(
        TechnicalException.class.getName(), SoapFaultDefinition.SERVER.toString());
    exceptionResolver.setExceptionMappings(errorMappings);
    exceptionResolver.setOrder(1);

    return exceptionResolver;
  }
}
