/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.jasper.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import org.opensmartgridplatform.adapter.protocol.jasper.client.JasperWirelessSmsClient;
import org.opensmartgridplatform.adapter.protocol.jasper.client.JasperWirelessTerminalClient;
import org.opensmartgridplatform.adapter.protocol.jasper.exceptions.OsgpJasperException;
import org.opensmartgridplatform.adapter.protocol.jasper.infra.ws.CorrelationIdProviderService;
import org.opensmartgridplatform.adapter.protocol.jasper.infra.ws.JasperWirelessSmsSoapClient;
import org.opensmartgridplatform.adapter.protocol.jasper.infra.ws.JasperWirelessTerminalSoapClient;
import org.opensmartgridplatform.adapter.protocol.jasper.rest.client.JasperWirelessSmsRestClient;
import org.opensmartgridplatform.adapter.protocol.jasper.rest.client.JasperWirelessTerminalRestClient;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;

/** An application context Java configuration class for Jasper Wireless settings. */
@Configuration
@PropertySource("classpath:jasper-interface.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/JasperInterface/config}", ignoreResourceNotFound = true)
@ComponentScan(basePackages = {"org.opensmartgridplatform.adapter.protocol.jasper"})
public class JasperWirelessConfig extends AbstractConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(JasperWirelessConfig.class);

  @Value("${jwcc.getsession.retries}")
  private String retries;

  @Value("${jwcc.getsession.sleep.between.retries}")
  private String sleepBetweenRetries;

  @Value("${jwcc.uri}")
  private String uri;

  @Value("${jwcc.licensekey}")
  private String licenceKey;

  @Value("${jwcc.username}")
  private String username;

  @Value("${jwcc.password}")
  private String password;

  @Value("${jwcc.api.version}")
  private String apiVersion;

  @Value("${jwcc.validity_period:0}")
  private String validityPeriod;

  @Value("${jwcc.api.type:SOAP}")
  private String apiType;

  public enum ApiType {
    SOAP,
    REST
  }

  @Bean
  public Jaxb2Marshaller marshaller() {
    final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setContextPaths("com.jasperwireless.api.ws.service");
    return marshaller;
  }

  @Bean
  SaajSoapMessageFactory messageFactory() throws OsgpJasperException {
    final SaajSoapMessageFactory saajSoapMessageFactory;
    try {
      saajSoapMessageFactory = new SaajSoapMessageFactory(MessageFactory.newInstance());
      saajSoapMessageFactory.setSoapVersion(SoapVersion.SOAP_11);
    } catch (final SOAPException e) {
      final String msg = "Error in creating a webservice message wrapper";
      LOGGER.error(msg, e);
      throw new OsgpJasperException(msg, e);
    }
    return saajSoapMessageFactory;
  }

  @Bean
  Wss4jSecurityInterceptor wss4jSecurityInterceptorClient() {
    final Wss4jSecurityInterceptor wss4jSecurityInterceptor = new Wss4jSecurityInterceptor();
    wss4jSecurityInterceptor.setSecurementActions("UsernameToken");
    return wss4jSecurityInterceptor;
  }

  @Bean
  public WebServiceTemplate jasperWebServiceTemplate() throws OsgpJasperException {

    final WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
    webServiceTemplate.setMarshaller(this.marshaller());
    webServiceTemplate.setUnmarshaller(this.marshaller());
    webServiceTemplate.setDefaultUri("https://kpnapi.jasperwireless.com/ws/service/Sms");
    webServiceTemplate.setInterceptors(
        new ClientInterceptor[] {this.wss4jSecurityInterceptorClient()});
    webServiceTemplate.setMessageFactory(this.messageFactory());

    return webServiceTemplate;
  }

  @Bean
  public JasperWirelessAccess jasperWirelessAccess() {
    return new JasperWirelessAccess(
        this.uri, this.licenceKey, this.username, this.password, this.apiVersion, this.apiType);
  }

  @Bean
  public CorrelationIdProviderService correlationIdProviderService() {
    return new CorrelationIdProviderService();
  }

  @Bean
  public int jasperGetSessionRetries() {
    return Integer.parseInt(this.retries);
  }

  @Bean
  public short jasperGetValidityPeriod() {
    return Short.parseShort(this.validityPeriod);
  }

  @Bean
  public int jasperGetSessionSleepBetweenRetries() {
    return Integer.parseInt(this.sleepBetweenRetries);
  }

  @Bean
  public RestTemplate jasperWirelessRestTemplate() {

    final RestTemplate restTemplate = new RestTemplate();
    restTemplate.getMessageConverters().add(0, this.createMappingJacksonHttpMessageConverter());
    return restTemplate;
  }

  private MappingJackson2HttpMessageConverter createMappingJacksonHttpMessageConverter() {

    final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(this.createObjectMapper());
    return converter;
  }

  private ObjectMapper createObjectMapper() {

    final ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ"));
    return objectMapper;
  }

  @Bean
  public JasperWirelessSmsClient jasperWirelessSmsClient() {
    if (this.apiType.equalsIgnoreCase(ApiType.REST.name())) {
      return new JasperWirelessSmsRestClient();
    } else {
      return new JasperWirelessSmsSoapClient();
    }
  }

  @Bean
  public JasperWirelessTerminalClient jasperWirelessTerminalClient() {
    if (this.apiType.equalsIgnoreCase(ApiType.REST.name())) {
      return new JasperWirelessTerminalRestClient();
    } else {
      return new JasperWirelessTerminalSoapClient();
    }
  }
}
