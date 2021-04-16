/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.shared.services;

import org.opensmartgridplatform.adapter.ws.clients.SendNotificationServiceClient;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.security.support.KeyStoreFactoryBean;

/** Base class for NotificationClientConfig classes for components of OSGP. */
public class NotificationClientConfigBase extends AbstractConfig {

  // Notification web service global properties.

  @Value("${web.service.notification.enabled:true}")
  protected boolean webserviceNotificationEnabled;

  @Value("${web.service.notification.security.enabled:false}")
  protected boolean webserviceNotificationSecurityEnabled;

  @Value(
      "${web.service.notification.url:http://localhost:8080/web-api-net-management/soap/osgp/notificationService}")
  protected String webserviceNotificationUrl;

  @Value(
      "${jaxb2.marshaller.context.path.notification:org.opensmartgridplatform.adapter.ws.schema.core.notification}")
  protected String marshallerContextPathNotification;

  @Value("${apache.client.max.connections.per.route:20}")
  protected int maxConnectionsPerRoute;

  @Value("${apache.client.max.connections.total:400}")
  protected int maxConnectionsTotal;

  // Notification web service application name, user name and organization
  // properties.

  @Value("${web.service.notification.application.name:OSGP}")
  protected String applicationName;

  @Value("${web.service.notification.username:OSGP}")
  protected String webserviceNotificationUsername;

  @Value("${web.service.notification.organisation:OSGP}")
  protected String webserviceNotificationOrganisation;

  // Notification web service security properties.

  @Value("${web.service.truststore.location:/etc/ssl/certs/trust.jks}")
  protected String webserviceTruststoreLocation;

  @Value("${web.service.truststore.password:123456}")
  protected String webserviceTruststorePassword;

  @Value("${web.service.truststore.type:jks}")
  protected String webserviceTruststoreType;

  @Value("${web.service.keystore.location:/etc/ssl/certs}")
  protected String webserviceKeystoreLocation;

  @Value("${web.service.keystore.password:1234}")
  protected String webserviceKeystorePassword;

  @Value("${web.service.keystore.type:pkcs12}")
  protected String webserviceKeystoreType;

  @Bean
  public SaajSoapMessageFactory messageFactory() {
    return new SaajSoapMessageFactory();
  }

  @Bean
  public KeyStoreFactoryBean webServiceTrustStoreFactory() {
    final KeyStoreFactoryBean factory = new KeyStoreFactoryBean();
    factory.setType(this.webserviceTruststoreType);
    factory.setLocation(new FileSystemResource(this.webserviceTruststoreLocation));
    factory.setPassword(this.webserviceTruststorePassword);
    return factory;
  }

  public Jaxb2Marshaller notificationSenderMarshaller() {
    final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setContextPath(this.marshallerContextPathNotification);
    return marshaller;
  }

  @Bean
  public SendNotificationServiceClient sendNotificationServiceClient() {
    return new SendNotificationServiceClient(
        this.createWebServiceTemplateFactory(this.notificationSenderMarshaller()),
        this.webserviceNotificationOrganisation,
        this.webserviceNotificationUsername);
  }

  @Bean
  public DefaultWebServiceTemplateFactory createWebServiceTemplateFactory(
      final Jaxb2Marshaller marshaller) {
    final DefaultWebServiceTemplateFactory.Builder builder =
        new DefaultWebServiceTemplateFactory.Builder();
    builder
        .setMarshaller(marshaller)
        .setMessageFactory(this.messageFactory())
        .setTargetUri(this.webserviceNotificationUrl)
        .setMaxConnectionsPerRoute(this.maxConnectionsPerRoute)
        .setMaxConnectionsTotal(this.maxConnectionsTotal)
        .setApplicationName(this.applicationName)
        .setSecurityEnabled(this.webserviceNotificationSecurityEnabled);
    if (this.webserviceNotificationSecurityEnabled) {
      builder
          .setKeyStoreType(this.webserviceKeystoreType)
          .setKeyStoreLocation(this.webserviceKeystoreLocation)
          .setKeyStorePassword(this.webserviceKeystorePassword)
          .setTrustStoreFactory(this.webServiceTrustStoreFactory());
    }

    return builder.build();
  }

  public boolean isWebserviceNotificationEnabled() {
    return this.webserviceNotificationEnabled;
  }
}
