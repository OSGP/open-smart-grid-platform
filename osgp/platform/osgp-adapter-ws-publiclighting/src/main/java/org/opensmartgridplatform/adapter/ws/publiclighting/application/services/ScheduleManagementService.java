/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.publiclighting.application.services;

import javax.validation.Valid;
import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.ws.publiclighting.infra.jms.PublicLightingRequestMessage;
import org.opensmartgridplatform.adapter.ws.publiclighting.infra.jms.PublicLightingRequestMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.Schedule;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.validation.Identification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service(value = "wsPublicLightingScheduleManagementService")
@Transactional(value = "transactionManager")
@Validated
public class ScheduleManagementService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleManagementService.class);

  @Autowired private DomainHelperService domainHelperService;

  @Autowired private CorrelationIdProviderService correlationIdProviderService;

  @Autowired
  @Qualifier("wsPublicLightingOutboundDomainRequestsMessageSender")
  private PublicLightingRequestMessageSender messageSender;

  /** Constructor */
  public ScheduleManagementService() {
    // Parameterless constructor required for transactions...
  }

  public String enqueueSetLightSchedule(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      @Valid final Schedule schedule,
      final DateTime scheduledTime,
      final int messagePriority)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

    this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_LIGHT_SCHEDULE);
    this.domainHelperService.isInMaintenance(device);

    LOGGER.debug(
        "enqueueSetLightSchedule called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final DeviceMessageMetadata deviceMessageMetadata =
        new DeviceMessageMetadata(
            deviceIdentification,
            organisationIdentification,
            correlationUid,
            MessageType.SET_LIGHT_SCHEDULE.name(),
            messagePriority,
            scheduledTime == null ? null : scheduledTime.getMillis());

    final PublicLightingRequestMessage message =
        new PublicLightingRequestMessage.Builder()
            .deviceMessageMetadata(deviceMessageMetadata)
            .request(schedule)
            .build();

    this.messageSender.send(message);

    return correlationUid;
  }
}
