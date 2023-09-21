// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.util.DriverDataSource;
import java.text.SimpleDateFormat;
import java.util.Properties;
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
import org.opensmartgridplatform.adapter.protocol.jasper.service.DeviceSessionService;
import org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders.SessionProvider;
import org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders.SessionProviderKpnPollJasper;
import org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders.SessionProviderKpnPushAlarm;
import org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders.SessionProviderMap;
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
  private int retries;

  @Value("${jwcc.getsession.sleep.between.retries}")
  private int sleepBetweenRetries;

  @Value("${jwcc.uri.sms}")
  private String uri;

  @Value("${jwcc.licensekey}")
  private String licenceKey;

  @Value("${jwcc.apikey}")
  private String apiKey;

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

  @Value("${jwcc.getsession.poll.jasper:false}")
  private boolean pollJasper;

  @Value("${push.alarm.max-wait-in-ms:60000}")
  private int maxWaitInMs;

  @Value("${db.driver}")
  private String databaseDriver;

  @Value("${db.host}")
  private String databaseHost;

  @Value("${db.protocol}")
  private String databaseProtocol;

  @Value("${db.port}")
  private int databasePort;

  @Value("${db.name}")
  private String databaseName;

  @Value("${db.username}")
  private String databaseUsername;

  @Value("${db.password}")
  private String databasePassword;

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
        this.uri,
        this.licenceKey,
        this.apiKey,
        this.username,
        this.password,
        this.apiVersion,
        this.apiType);
  }

  @Bean
  public CorrelationIdProviderService correlationIdProviderService() {
    return new CorrelationIdProviderService();
  }

  @Bean
  public short jasperGetValidityPeriod() {
    return Short.parseShort(this.validityPeriod);
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

  @Bean
  public DeviceSessionService deviceSessionService() {
    final String jdbcUrl =
        String.format(
            "%s%s:%s/%s",
            this.databaseProtocol, this.databaseHost, this.databasePort, this.databaseName);
    LOGGER.info("Created jdbcUrl {} for deviceSessionService", jdbcUrl);
    final DriverDataSource dataSource =
        new DriverDataSource(
            jdbcUrl,
            this.databaseDriver,
            new Properties(),
            this.databaseUsername,
            this.databasePassword);

    return new DeviceSessionService(dataSource, this.maxWaitInMs);
  }

  @Bean
  public SessionProvider sessionProviderKpn(
      final SessionProviderMap sessionProviderMap,
      final JasperWirelessTerminalClient jasperWirelessTerminalClient,
      final JasperWirelessSmsClient jasperWirelessSmsClient,
      final DeviceSessionService deviceSessionService) {
    if (this.pollJasper) {
      return new SessionProviderKpnPollJasper(
          sessionProviderMap,
          jasperWirelessTerminalClient,
          jasperWirelessSmsClient,
          this.retries,
          this.sleepBetweenRetries);
    } else {
      return new SessionProviderKpnPushAlarm(
          sessionProviderMap, jasperWirelessSmsClient, deviceSessionService);
    }
  }
}
