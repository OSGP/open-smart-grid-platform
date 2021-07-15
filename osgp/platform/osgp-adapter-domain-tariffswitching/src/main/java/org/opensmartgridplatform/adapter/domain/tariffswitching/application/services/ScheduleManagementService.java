/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.tariffswitching.application.services;

import java.util.List;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceOutputSetting;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.exceptions.ValidationException;
import org.opensmartgridplatform.domain.core.valueobjects.LightValue;
import org.opensmartgridplatform.domain.core.valueobjects.RelayType;
import org.opensmartgridplatform.domain.core.valueobjects.ScheduleEntry;
import org.opensmartgridplatform.dto.valueobjects.ScheduleDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "domainTariffSwitchingScheduleManagementService")
@Transactional(value = "transactionManager")
public class ScheduleManagementService extends AbstractService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleManagementService.class);

  /** Constructor */
  public ScheduleManagementService() {
    // Parameterless constructor required for transactions...
  }

  // === SET TARIFF SCHEDULE ===

  /** Set a tariff schedule. */
  public void setTariffSchedule(
      final CorrelationIds ids,
      final List<ScheduleEntry> schedules,
      final Long scheduleTime,
      final String messageType,
      final int messagePriority)
      throws FunctionalException {

    LOGGER.info(
        "setTariffSchedule called with organisation {} and device {}.",
        ids.getOrganisationIdentification(),
        ids.getDeviceIdentification());

    this.findOrganisation(ids.getOrganisationIdentification());
    final Device device = this.findActiveDevice(ids.getDeviceIdentification());
    if (Ssld.PSLD_TYPE.equals(device.getDeviceType())) {
      throw new FunctionalException(
          FunctionalExceptionType.TARIFF_SCHEDULE_NOT_ALLOWED_FOR_PSLD,
          ComponentType.DOMAIN_TARIFF_SWITCHING,
          new ValidationException("Set tariff schedule is not allowed for PSLD."));
    }

    // Reverse schedule switching for TARIFF_REVERSED relays.
    for (final DeviceOutputSetting dos : this.getSsldForDevice(device).getOutputSettings()) {
      if (dos.getOutputType().equals(RelayType.TARIFF_REVERSED)) {
        for (final ScheduleEntry schedule : schedules) {
          for (final LightValue lightValue : schedule.getLightValue()) {
            lightValue.invertIsOn();
          }
        }
      }
    }

    LOGGER.info("Mapping to schedule DTO");

    final List<org.opensmartgridplatform.dto.valueobjects.ScheduleEntryDto> schedulesDto =
        this.domainCoreMapper.mapAsList(
            schedules, org.opensmartgridplatform.dto.valueobjects.ScheduleEntryDto.class);
    final ScheduleDto scheduleDto = new ScheduleDto(schedulesDto);

    LOGGER.info("Sending message");

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(ids, scheduleDto),
        messageType,
        messagePriority,
        device.getIpAddress(),
        scheduleTime);
  }
}
