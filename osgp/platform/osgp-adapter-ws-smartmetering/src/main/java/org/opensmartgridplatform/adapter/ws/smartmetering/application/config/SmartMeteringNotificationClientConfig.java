/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.config;

import java.util.Collections;

import org.opensmartgridplatform.adapter.ws.clients.NotificationWebServiceTemplateFactory;
import org.opensmartgridplatform.adapter.ws.domain.repositories.NotificationWebServiceConfigurationRepository;
import org.opensmartgridplatform.adapter.ws.mapping.NotificationMapper;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.notification.SendNotificationRequest;
import org.opensmartgridplatform.adapter.ws.shared.services.CorrelationUidTargetedNotificationService;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationServiceBlackHole;
import org.opensmartgridplatform.adapter.ws.shared.services.ResponseUrlService;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.ApplicationConstants;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.infra.ws.OrganisationIdentificationClientInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

@Configuration
@PropertySource("classpath:osgp-adapter-ws-smartmetering.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsSmartMetering/config}", ignoreResourceNotFound = true)
public class SmartMeteringNotificationClientConfig extends AbstractConfig {

    @Value("${web.service.notification.enabled}")
    private boolean webserviceNotificationEnabled;

    @Value("${web.service.notification.username:#{null}}")
    private String webserviceNotificationUsername;

    @Value("${web.service.notification.organisation:OSGP}")
    private String webserviceNotificationOrganisation;


    @Bean
    public NotificationService smartMeteringNotificationService(
            final NotificationWebServiceTemplateFactory templateFactory, final NotificationMapper mapper,
            final ResponseUrlService responseUrlService) {

        if (!this.webserviceNotificationEnabled) {
            return new NotificationServiceBlackHole();
        }
        final Class<SendNotificationRequest> notificationRequestType = SendNotificationRequest.class;
        return new CorrelationUidTargetedNotificationService<>(templateFactory, notificationRequestType, mapper,
                responseUrlService, ApplicationConstants.APPLICATION_NAME);
    }

    @Bean
    public NotificationWebServiceTemplateFactory notificationWebServiceTemplateFactory(
            final NotificationWebServiceConfigurationRepository configRepository) {

        final ClientInterceptor addOsgpHeadersInterceptor = OrganisationIdentificationClientInterceptor.newBuilder()
                .withOrganisationIdentification(this.webserviceNotificationOrganisation)
                .withUserName(this.webserviceNotificationUsername)
                .withApplicationName(ApplicationConstants.APPLICATION_NAME).build();

        return new NotificationWebServiceTemplateFactory(configRepository, this.messageFactory(),
                Collections.singletonList(addOsgpHeadersInterceptor));
    }

    @Bean
    public SaajSoapMessageFactory messageFactory() {
        return new SaajSoapMessageFactory();
    }
}
