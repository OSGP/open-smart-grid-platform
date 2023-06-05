// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.shared.services;

import ma.glasnost.orika.MapperFacade;
import org.opensmartgridplatform.adapter.ws.clients.NotificationWebServiceTemplateFactory;
import org.opensmartgridplatform.adapter.ws.domain.entities.ApplicationDataLookupKey;
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
      final ApplicationDataLookupKey endpointLookupKey, final GenericNotification notification) {

    final String customUri =
        this.responseUrlService.findResponseUrl(notification.getCorrelationUid());
    if (customUri != null) {
      LOGGER.debug("Overriding configured targetUri with: {}", customUri);
    }
    return customUri;
  }
}
