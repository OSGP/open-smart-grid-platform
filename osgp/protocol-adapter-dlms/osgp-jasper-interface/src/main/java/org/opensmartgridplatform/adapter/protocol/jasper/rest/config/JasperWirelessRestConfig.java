/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.jasper.rest.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;
import org.opensmartgridplatform.adapter.protocol.jasper.exceptions.OsgpJasperException;
import org.opensmartgridplatform.adapter.protocol.jasper.infra.ws.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/** An application context Java configuration class for Jasper Wireless settings. */
@EnableWebMvc
@Configuration
@PropertySource("classpath:jasper-rest-interface.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/JasperInterface/config}", ignoreResourceNotFound = true)
@ComponentScan(basePackages = {"org.opensmartgridplatform.adapter.protocol.jasper"})
public class JasperWirelessRestConfig extends AbstractConfig {

  @Value("${jasperwireless.rest.url}")
  private String restURL;

  @Value("${jasperwireless.rest.username}")
  private String restUsername;

  @Value("${jasperwireless.rest.licence_key}")
  private String restLicenceKey;

  @Value("${jasperwireless.rest.api_version}")
  private String restApiVersion;

  @Bean
  public RestTemplate jasperWirelessRestTemplate() throws OsgpJasperException {

    final RestTemplate restTemplate = new RestTemplate();
    restTemplate.getMessageConverters().add(0, this.createMappingJacksonHttpMessageConverter());
    return restTemplate;
  }

  @Bean
  public JasperWirelessRestAccess jasperWirelessRestAccess() {
    return new JasperWirelessRestAccess(
        this.restURL, this.restLicenceKey, this.restUsername, this.restApiVersion);
  }

  @Bean
  public CorrelationIdProviderService correlationIdProviderService() {
    return new CorrelationIdProviderService();
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
}
