//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.tariffswitching.application.services;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.ws.tariffswitching.infra.jms.TariffSwitchingRequestMessage;
import org.opensmartgridplatform.adapter.ws.tariffswitching.infra.jms.TariffSwitchingRequestMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.Schedule;
import org.opensmartgridplatform.domain.core.valueobjects.ScheduleEntry;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.validation.Identification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service(value = "wsTariffSwitchingScheduleManagementService")
@Transactional(value = "transactionManager")
@Validated
public class ScheduleManagementService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleManagementService.class);

  @Autowired private DomainHelperService domainHelperService;

  @Autowired private CorrelationIdProviderService correlationIdProviderService;

  @Autowired private TariffSwitchingRequestMessageSender tariffSwitchingRequestMessageSender;

  /** Constructor */
  public ScheduleManagementService() {
    // Parameterless constructor required for transactions...
  }

  public String enqueueSetTariffSchedule(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      @NotNull @Size(min = 1, max = 50) @Valid final List<ScheduleEntry> mapAsList,
      final DateTime scheduledTime,
      final int messagePriority)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

    this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_TARIFF_SCHEDULE);
    this.domainHelperService.isInMaintenance(device);

    LOGGER.debug(
        "enqueueSetTariffSchedule called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final Schedule schedule = new Schedule(mapAsList);

    final MessageMetadata deviceMessageMetadata =
        new MessageMetadata.Builder()
            .withDeviceIdentification(deviceIdentification)
            .withOrganisationIdentification(organisationIdentification)
            .withCorrelationUid(correlationUid)
            .withMessageType(MessageType.SET_TARIFF_SCHEDULE.name())
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduledTime == null ? null : scheduledTime.getMillis())
            .build();

    final TariffSwitchingRequestMessage message =
        new TariffSwitchingRequestMessage.Builder()
            .messageMetadata(deviceMessageMetadata)
            .request(schedule)
            .build();

    this.tariffSwitchingRequestMessageSender.send(message);

    return correlationUid;
  }
}
