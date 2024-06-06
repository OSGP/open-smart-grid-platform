// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.publiclighting.application.config;

import java.util.Arrays;
import org.opensmartgridplatform.adapter.ws.clients.NotificationWebServiceTemplateFactory;
import org.opensmartgridplatform.adapter.ws.domain.repositories.NotificationWebServiceConfigurationRepository;
import org.opensmartgridplatform.adapter.ws.mapping.NotificationMapper;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.notification.SendNotificationRequest;
import org.opensmartgridplatform.adapter.ws.shared.services.CorrelationUidTargetedNotificationService;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationServiceBlackHole;
import org.opensmartgridplatform.adapter.ws.shared.services.ResponseUrlService;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.infra.ws.OrganisationIdentificationClientInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

@Configuration
@PropertySource("classpath:osgp-adapter-ws-publiclighting.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(
    value = "file:${osgp/AdapterWsPublicLighting/config}",
    ignoreResourceNotFound = true)
public class PublicLightingNotificationClientConfig extends AbstractConfig {

  @Value("${web.service.notification.enabled:false}")
  private boolean webserviceNotificationEnabled;

  @Value("${web.service.notification.username:OSGP}")
  private String webserviceNotificationUsername;

  @Value("${web.service.notification.organisation:OSGP}")
  private String webserviceNotificationOrganisation;

  @Value("${web.service.notification.application.name:OSGP}")
  private String webserviceNotificationApplicationName;

  @Value("${web.service.notification.supported.tls.protocols:TLSv1.2,TLSv1.3}")
  private String[] webserviceNotificationSupportedTlsProtocols;

  @Bean
  public NotificationService publicLightingNotificationService(
      final NotificationWebServiceTemplateFactory templateFactory,
      final NotificationMapper mapper,
      final ResponseUrlService responseUrlService) {

    if (!this.webserviceNotificationEnabled) {
      return new NotificationServiceBlackHole();
    }
    final Class<SendNotificationRequest> notificationRequestType = SendNotificationRequest.class;
    return new CorrelationUidTargetedNotificationService<>(
        templateFactory,
        notificationRequestType,
        mapper,
        responseUrlService,
        this.webserviceNotificationApplicationName);
  }

  @Bean
  public NotificationWebServiceTemplateFactory notificationWebServiceTemplateFactory(
      final NotificationWebServiceConfigurationRepository configRepository) {

    final ClientInterceptor addOsgpHeadersInterceptor =
        OrganisationIdentificationClientInterceptor.newBuilder()
            .withOrganisationIdentification(this.webserviceNotificationOrganisation)
            .withUserName(this.webserviceNotificationUsername)
            .withApplicationName(this.webserviceNotificationApplicationName)
            .build();

    return new NotificationWebServiceTemplateFactory(
        configRepository,
        this.messageFactory(),
        Arrays.asList(addOsgpHeadersInterceptor),
        this.webserviceNotificationSupportedTlsProtocols);
  }

  @Bean
  public SaajSoapMessageFactory messageFactory() {
    return new SaajSoapMessageFactory();
  }
}
