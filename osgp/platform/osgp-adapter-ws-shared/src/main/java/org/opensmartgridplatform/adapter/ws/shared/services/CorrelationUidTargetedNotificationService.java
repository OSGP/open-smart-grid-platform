/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.shared.services;

import ma.glasnost.orika.MapperFacade;
import org.opensmartgridplatform.adapter.ws.clients.NotificationWebServiceTemplateFactory;
import org.opensmartgridplatform.adapter.ws.domain.entities.NotificationWebServiceLookupKey;
import org.opensmartgridplatform.adapter.ws.schema.shared.notification.GenericNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Transactional(value = "transactionManager")
@Validated
public class CorrelationUidTargetedNotificationService<T> extends DefaultNotificationService<T> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(CorrelationUidTargetedNotificationService.class);

  private final ResponseUrlService responseUrlService;

  public CorrelationUidTargetedNotificationService(
      final NotificationWebServiceTemplateFactory templateFactory,
      final Class<T> sendNotificationRequestType,
      final MapperFacade mapper,
      final ResponseUrlService responseUrlService,
      final String applicationName) {

    super(templateFactory, sendNotificationRequestType, mapper, applicationName);
    this.responseUrlService = responseUrlService;
  }

  @Override
  public String getCustomTargetUri(
      final NotificationWebServiceLookupKey endpointLookupKey,
      final GenericNotification notification) {

    final String customUri =
        this.responseUrlService.findResponseUrl(notification.getCorrelationUid());
    if (customUri != null) {
      LOGGER.debug("Overriding configured targetUri with: {}", customUri);
    }
    return customUri;
  }
}
