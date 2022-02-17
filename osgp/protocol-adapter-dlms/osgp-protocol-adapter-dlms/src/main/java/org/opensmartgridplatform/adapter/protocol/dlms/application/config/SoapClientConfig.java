/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.config;

import java.io.File;
import java.io.IOException;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.opensmartgridplatform.shared.security.RsaEncrypter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.security.support.KeyManagersFactoryBean;
import org.springframework.ws.soap.security.support.KeyStoreFactoryBean;
import org.springframework.ws.soap.security.support.TrustManagersFactoryBean;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;

@Configuration
public class SoapClientConfig {

  private static final String XSD_SCHEMA_PACKAGE =
      "org.opensmartgridplatform.ws.schema.core.secret.management";

  @Value("${soapclient.use.client.auth:false}")
  private String useClientAuth;

  @Value("${soapclient.use.hostname.verifier:true}")
  private String useHostNameVerifier;

  @Value("${soapclient.default-uri}")
  private String defaultUri;

  @Value("${soapclient.ssl.trust-store}")
  private Resource trustStore;

  @Value("${soapclient.ssl.trust-store-password}")
  private String trustStorePassword;

  @Value("${soapclient.ssl.key-store}")
  private Resource keyStore;

  @Value("${soapclient.ssl.key-store-password}")
  private String keyStorePassword;

  @Value("${soapclient.ssl.key-password}")
  private String keyPassword;

  @Value("${encryption.rsa.private.key.protocol.adapter.dlms}")
  private Resource rsaPrivateKeyProtocolAdapterDlms;

  @Value("${encryption.rsa.public.key.secret.management}")
  private Resource rsaPublicKeySecretManagement;

  @Value("${encryption.rsa.private.key.gxf.smartmetering}")
  private Resource rsaPrivateKeyGxfSmartMetering;

  @Value("${encryption.rsa.public.key.gxf.smartmetering}")
  private Resource rsaPublicKeyGxfSmartMetering;

  @Bean
  Jaxb2Marshaller soapClientJaxb2Marshaller() {
    final Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
    jaxb2Marshaller.setContextPath(XSD_SCHEMA_PACKAGE);
    return jaxb2Marshaller;
  }

  @Bean
  public WebServiceTemplate webServiceTemplate() throws Exception {

    final WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
    webServiceTemplate.setMarshaller(this.soapClientJaxb2Marshaller());
    webServiceTemplate.setUnmarshaller(this.soapClientJaxb2Marshaller());
    webServiceTemplate.setDefaultUri(this.defaultUri);

    if (Boolean.parseBoolean(this.useClientAuth)) {
      webServiceTemplate.setMessageSender(this.httpsUrlConnectionMessageSender());
    }

    return webServiceTemplate;
  }

  @Bean
  public HttpsUrlConnectionMessageSender httpsUrlConnectionMessageSender() throws Exception {
    final HttpsUrlConnectionMessageSender httpsUrlConnectionMessageSender =
        new HttpsUrlConnectionMessageSender();
    // set the trust store(s)
    httpsUrlConnectionMessageSender.setTrustManagers(this.trustManagersFactoryBean().getObject());
    // set the key store(s)
    httpsUrlConnectionMessageSender.setKeyManagers(this.keyManagersFactoryBean().getObject());

    if (!Boolean.parseBoolean(this.useHostNameVerifier)) {
      httpsUrlConnectionMessageSender.setHostnameVerifier(new NoopHostnameVerifier());
    }

    return httpsUrlConnectionMessageSender;
  }

  @Bean
  public KeyStoreFactoryBean trustStore() {
    final KeyStoreFactoryBean keyStoreFactoryBean = new KeyStoreFactoryBean();
    keyStoreFactoryBean.setLocation(this.trustStore);
    keyStoreFactoryBean.setPassword(this.trustStorePassword);

    return keyStoreFactoryBean;
  }

  @Bean
  public TrustManagersFactoryBean trustManagersFactoryBean() {
    final TrustManagersFactoryBean trustManagersFactoryBean = new TrustManagersFactoryBean();
    trustManagersFactoryBean.setKeyStore(this.trustStore().getObject());

    return trustManagersFactoryBean;
  }

  @Bean
  public KeyStoreFactoryBean keyStore() {
    final KeyStoreFactoryBean keyStoreFactoryBean = new KeyStoreFactoryBean();
    keyStoreFactoryBean.setLocation(this.keyStore);
    keyStoreFactoryBean.setPassword(this.keyStorePassword);

    return keyStoreFactoryBean;
  }

  @Bean
  public KeyManagersFactoryBean keyManagersFactoryBean() {
    final KeyManagersFactoryBean keyManagersFactoryBean = new KeyManagersFactoryBean();
    keyManagersFactoryBean.setKeyStore(this.keyStore().getObject());
    keyManagersFactoryBean.setPassword(this.keyPassword);
    return keyManagersFactoryBean;
  }

  // RsaEncrypter for encrypting secrets to be sent to Secret Management.
  @Bean(name = "encrypterForSecretManagement")
  public RsaEncrypter encrypterForSecretManagement() {
    try {
      final File publicKeySecretManagementFile = this.rsaPublicKeySecretManagement.getFile();
      final RsaEncrypter rsaEncrypter = new RsaEncrypter();
      rsaEncrypter.setPublicKeyStore(publicKeySecretManagementFile);
      return rsaEncrypter;
    } catch (final IOException e) {
      throw new IllegalStateException("Could not initialize encrypterForSecretManagement", e);
    }
  }

  // RsaEncrypter for decrypting secrets from Secret Management received by the DLMS protocol
  // adapter.
  @Bean(name = "decrypterForProtocolAdapterDlms")
  public RsaEncrypter decrypterForProtocolAdapterDlms() {
    try {
      final File privateKeyProtocolAdapterFile = this.rsaPrivateKeyProtocolAdapterDlms.getFile();
      final RsaEncrypter rsaEncrypter = new RsaEncrypter();
      rsaEncrypter.setPrivateKeyStore(privateKeyProtocolAdapterFile);
      return rsaEncrypter;
    } catch (final IOException e) {
      throw new IllegalStateException("Could not initialize decrypterForProtocolAdapterDlms", e);
    }
  }

  // RsaEncrypter for encrypting secrets to be sent to other GXF components.
  @Bean(name = "encrypterForGxfSmartMetering")
  public RsaEncrypter encrypterForGxfSmartMetering() {
    try {
      final File publicKeyGxfSmartMeteringFile = this.rsaPublicKeyGxfSmartMetering.getFile();
      final RsaEncrypter rsaEncrypter = new RsaEncrypter();
      rsaEncrypter.setPublicKeyStore(publicKeyGxfSmartMeteringFile);
      return rsaEncrypter;
    } catch (final IOException e) {
      throw new IllegalStateException("Could not initialize encrypterForGxfSmartMetering", e);
    }
  }

  // RsaEncrypter for decrypting secrets from applications received by the DLMS protocol adapter.
  @Bean(name = "decrypterForGxfSmartMetering")
  public RsaEncrypter decrypterForGxfSmartMetering() {
    try {
      final File privateKeyGxfSmartMeteringFile = this.rsaPrivateKeyGxfSmartMetering.getFile();
      final RsaEncrypter rsaEncrypter = new RsaEncrypter();
      rsaEncrypter.setPrivateKeyStore(privateKeyGxfSmartMeteringFile);
      return rsaEncrypter;
    } catch (final IOException e) {
      throw new IllegalStateException("Could not initialize decrypterForGxfSmartMetering", e);
    }
  }
}
