// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.ws;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.security.support.KeyStoreFactoryBean;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

public class DefaultWebServiceTemplateFactory implements WebserviceTemplateFactory {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DefaultWebServiceTemplateFactory.class);

  private final Map<String, WebServiceTemplate> webServiceTemplates;
  private final Lock lock = new ReentrantLock();

  private static final String PROXY_SERVER = "proxy-server";

  private Jaxb2Marshaller marshaller;
  private SaajSoapMessageFactory messageFactory;
  private boolean isSecurityEnabled;
  private String targetUri;
  private String keyStoreType;
  private String keyStoreLocation;
  private String keyStorePassword;
  private KeyStoreFactoryBean trustStoreFactory;
  private String applicationName;
  private int maxConnectionsPerRoute;
  private int maxConnectionsTotal;
  private int connectionTimeout;
  private CircuitBreaker circuitBreaker;
  private WebServiceTemplateHostnameVerificationStrategy
      webServiceTemplateHostnameVerificationStrategy;

  private DefaultWebServiceTemplateFactory() {
    this.webServiceTemplates = new HashMap<>();
  }

  public WebServiceTemplate getTemplate(
      final String organisationIdentification, final String userName)
      throws WebServiceSecurityException {
    return this.getTemplate(organisationIdentification, userName, this.applicationName);
  }

  @Override
  public WebServiceTemplate getTemplate(
      final String organisationIdentification, final String userName, final URL targetUri)
      throws WebServiceSecurityException {
    this.targetUri = targetUri.toString();
    return this.getTemplate(organisationIdentification, userName, this.applicationName);
  }

  public static class Builder {
    private String applicationName;
    private Jaxb2Marshaller marshaller;
    private SaajSoapMessageFactory messageFactory;
    private String targetUri;
    private boolean isSecurityEnabled = true;
    private String keyStoreType;
    private String keyStoreLocation;
    private String keyStorePassword;
    private KeyStoreFactoryBean trustStoreFactory;
    private int maxConnectionsPerRoute = 2;
    private int maxConnectionsTotal = 20;
    private int connectionTimeout = 120000;
    private CircuitBreaker circuitBreaker;
    private WebServiceTemplateHostnameVerificationStrategy
        webServiceTemplateHostnameVerificationStrategy =
            WebServiceTemplateHostnameVerificationStrategy.ALLOW_ALL_HOSTNAMES;

    public Builder setApplicationName(final String applicationName) {
      this.applicationName = applicationName;
      return this;
    }

    public Builder setMarshaller(final Jaxb2Marshaller marshaller) {
      this.marshaller = marshaller;
      return this;
    }

    public Builder setMessageFactory(final SaajSoapMessageFactory messageFactory) {
      this.messageFactory = messageFactory;
      return this;
    }

    public Builder setTargetUri(final String targetUri) {
      this.targetUri = targetUri;
      return this;
    }

    public Builder setSecurityEnabled(final boolean enabled) {
      this.isSecurityEnabled = enabled;
      return this;
    }

    public Builder setKeyStoreType(final String keyStoreType) {
      this.keyStoreType = keyStoreType;
      return this;
    }

    public Builder setKeyStoreLocation(final String keyStoreLocation) {
      this.keyStoreLocation = keyStoreLocation;
      return this;
    }

    public Builder setKeyStorePassword(final String keyStorePassword) {
      this.keyStorePassword = keyStorePassword;
      return this;
    }

    public Builder setTrustStoreFactory(final KeyStoreFactoryBean trustStoreFactory) {
      this.trustStoreFactory = trustStoreFactory;
      return this;
    }

    public Builder setMaxConnectionsPerRoute(final int maxConnectionsPerRoute) {
      this.maxConnectionsPerRoute = maxConnectionsPerRoute;
      return this;
    }

    public Builder setMaxConnectionsTotal(final int maxConnectionsTotal) {
      this.maxConnectionsTotal = maxConnectionsTotal;
      return this;
    }

    public Builder setConnectionTimeout(final int connectionTimeout) {
      this.connectionTimeout = connectionTimeout;
      return this;
    }

    public Builder setCircuitBreaker(final CircuitBreaker circuitBreaker) {
      this.circuitBreaker = circuitBreaker;
      return this;
    }

    public Builder setWebServiceTemplateHostnameVerificationStrategy(
        final WebServiceTemplateHostnameVerificationStrategy
            webServiceTemplateHostnameVerificationStrategy) {
      this.webServiceTemplateHostnameVerificationStrategy =
          webServiceTemplateHostnameVerificationStrategy;
      return this;
    }

    public DefaultWebServiceTemplateFactory build() {
      final DefaultWebServiceTemplateFactory webServiceTemplateFactory =
          new DefaultWebServiceTemplateFactory();
      webServiceTemplateFactory.marshaller = this.marshaller;
      webServiceTemplateFactory.messageFactory = this.messageFactory;
      webServiceTemplateFactory.targetUri = this.targetUri;
      webServiceTemplateFactory.isSecurityEnabled = this.isSecurityEnabled;
      if (this.isSecurityEnabled) {
        webServiceTemplateFactory.keyStoreType = this.keyStoreType;
        webServiceTemplateFactory.keyStoreLocation = this.keyStoreLocation;
        webServiceTemplateFactory.keyStorePassword = this.keyStorePassword;
        webServiceTemplateFactory.trustStoreFactory = this.trustStoreFactory;
      }
      webServiceTemplateFactory.applicationName = this.applicationName;
      webServiceTemplateFactory.maxConnectionsPerRoute = this.maxConnectionsPerRoute;
      webServiceTemplateFactory.maxConnectionsTotal = this.maxConnectionsTotal;
      webServiceTemplateFactory.connectionTimeout = this.connectionTimeout;
      webServiceTemplateFactory.circuitBreaker = this.circuitBreaker;
      webServiceTemplateFactory.webServiceTemplateHostnameVerificationStrategy =
          this.webServiceTemplateHostnameVerificationStrategy;
      return webServiceTemplateFactory;
    }
  }

  @Override
  public WebServiceTemplate getTemplate(
      final String organisationIdentification, final String userName, final String applicationName)
      throws WebServiceSecurityException {

    if (StringUtils.isEmpty(organisationIdentification)) {
      LOGGER.error("organisationIdentification is empty or null");
    }
    if (StringUtils.isEmpty(userName)) {
      LOGGER.error("userName is empty or null");
    }
    if (StringUtils.isEmpty(applicationName)) {
      LOGGER.error("applicationName is empty or null");
    }

    WebServiceTemplate webServiceTemplate = null;
    try {
      this.lock.lock();

      // Create new webservice template, if not yet available for
      // a combination of organisation, username, applicationName and
      // targetUri
      final String url = (this.targetUri == null) ? "" : "-" + this.targetUri;
      final String key =
          organisationIdentification
              .concat("-")
              .concat(userName)
              .concat(applicationName)
              .concat(url);

      if (!this.webServiceTemplates.containsKey(key)) {
        this.webServiceTemplates.put(
            key, this.createTemplate(organisationIdentification, userName, applicationName));
      }

      webServiceTemplate = this.webServiceTemplates.get(key);
    } finally {
      this.lock.unlock();
    }

    return webServiceTemplate;
  }

  private WebServiceTemplate createTemplate(
      final String organisationIdentification, final String userName, final String applicationName)
      throws WebServiceSecurityException {
    final WebServiceTemplate webServiceTemplate = new WebServiceTemplate(this.messageFactory);

    webServiceTemplate.setCheckConnectionForFault(true);
    if (this.targetUri != null) {
      webServiceTemplate.setDefaultUri(this.targetUri);
      if (this.targetUri.contains(PROXY_SERVER)) {
        webServiceTemplate.setCheckConnectionForFault(false);
      }
    }

    webServiceTemplate.setMarshaller(this.marshaller);
    webServiceTemplate.setUnmarshaller(this.marshaller);

    final ClientInterceptor organisationIdentificationClientInterceptor =
        OrganisationIdentificationClientInterceptor.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withUserName(userName)
            .withApplicationName(applicationName)
            .build();

    final List<ClientInterceptor> interceptors = new ArrayList<>();
    interceptors.add(organisationIdentificationClientInterceptor);

    if (this.circuitBreaker != null) {
      final ClientInterceptor circuitBreakerInterceptor =
          new CircuitBreakerInterceptor(this.circuitBreaker);
      interceptors.add(circuitBreakerInterceptor);
    }

    webServiceTemplate.setInterceptors(
        interceptors.toArray(new ClientInterceptor[interceptors.size()]));
    webServiceTemplate.setMessageSender(this.webServiceMessageSender(organisationIdentification));

    return webServiceTemplate;
  }

  private HttpComponentsMessageSender webServiceMessageSender(final String keystore)
      throws WebServiceSecurityException {

    final HttpClientBuilder clientbuilder = HttpClientBuilder.create();
    if (this.isSecurityEnabled) {
      try {
        clientbuilder.setSSLSocketFactory(this.getSSLConnectionSocketFactory(keystore));
      } catch (GeneralSecurityException | IOException e) {
        LOGGER.error("Webservice exception occurred: Certificate not available", e);
        throw new WebServiceSecurityException("Certificate not available", e);
      }
    }

    clientbuilder.setMaxConnPerRoute(this.maxConnectionsPerRoute);
    clientbuilder.setMaxConnTotal(this.maxConnectionsTotal);

    // Add intercepter to prevent issue with duplicate headers.
    // See also:
    // http://forum.spring.io/forum/spring-projects/web-services/118857-spring-ws-2-1-4-0-httpclient-proxy-content-length-header-already-present
    clientbuilder.addInterceptorFirst(
        new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor());

    final RequestConfig requestConfig =
        RequestConfig.custom().setConnectTimeout(this.connectionTimeout).build();
    clientbuilder.setDefaultRequestConfig(requestConfig);
    return new HttpComponentsMessageSender(clientbuilder.build());
  }

  private SSLConnectionSocketFactory getSSLConnectionSocketFactory(final String keystore)
      throws GeneralSecurityException, IOException {
    // Open key store, assuming same identity
    final KeyStoreFactoryBean keyStoreFactory = new KeyStoreFactoryBean();
    keyStoreFactory.setType(this.keyStoreType);
    keyStoreFactory.setLocation(
        new FileSystemResource(this.keyStoreLocation + "/" + keystore + ".pfx"));
    keyStoreFactory.setPassword(this.keyStorePassword);
    keyStoreFactory.afterPropertiesSet();

    final KeyStore keyStore = keyStoreFactory.getObject();
    if ((keyStore == null) || (keyStore.size() == 0)) {
      throw new KeyStoreException("Key store is empty");
    }

    // Setup SSL context, load trust and key store and build the message
    // sender
    final SSLContext sslContext =
        SSLContexts.custom()
            .loadKeyMaterial(keyStore, this.keyStorePassword.toCharArray())
            .loadTrustMaterial(this.trustStoreFactory.getObject(), new TrustSelfSignedStrategy())
            .build();

    final HostnameVerifier hostnameVerifier = this.getHostnameVerifier();

    return new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
  }

  private HostnameVerifier getHostnameVerifier() throws GeneralSecurityException {
    switch (this.webServiceTemplateHostnameVerificationStrategy) {
      case ALLOW_ALL_HOSTNAMES:
        return new NoopHostnameVerifier();
      case BROWSER_COMPATIBLE_HOSTNAMES:
        return new DefaultHostnameVerifier();
      default:
        throw new GeneralSecurityException("No hostname verification stategy set.");
    }
  }
}
