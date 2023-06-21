// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.publiclighting.application.services;

import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.valueobjects.Schedule;
import org.opensmartgridplatform.dto.valueobjects.ScheduleDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "domainPublicLightingScheduleManagementService")
@Transactional(value = "transactionManager")
public class ScheduleManagementService extends AbstractService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleManagementService.class);

  /** Constructor */
  public ScheduleManagementService() {
    // Parameterless constructor required for transactions...
  }

  // === SET LIGHT SCHEDULE ===

  /** Set a light schedule. */
  public void setLightSchedule(
      final CorrelationIds ids,
      final Schedule schedule,
      final Long scheduleTime,
      final String messageType,
      final int messagePriority)
      throws FunctionalException {

    LOGGER.info(
        "setSchedule called with organisation {} and device {}.",
        ids.getOrganisationIdentification(),
        ids.getDeviceIdentification());

    this.findOrganisation(ids.getOrganisationIdentification());
    final Device device = this.findActiveDevice(ids.getDeviceIdentification());

    final ScheduleDto scheduleDto = this.domainCoreMapper.map(schedule, ScheduleDto.class);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(ids, scheduleDto),
        messageType,
        messagePriority,
        device.getIpAddress(),
        scheduleTime);
  }

  // === SET HAS SCHEDULE ===

  /**
   * Method for setting the 'hasSchedule' boolean for a device.
   *
   * @throws FunctionalException
   */
  public void setHasSchedule(final String deviceIdentification, final Boolean hasSchedule)
      throws FunctionalException {

    LOGGER.info(
        "setHasSchedule called for device {} with hasSchedule: {}.",
        deviceIdentification,
        hasSchedule);

    final Device device = this.findActiveDevice(deviceIdentification);
    final Ssld ssld = this.findSsldForDevice(device);
    ssld.setHasSchedule(hasSchedule);
    this.ssldRepository.save(ssld);
  }
}
