// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.tariffswitching.application.services;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.adapter.domain.shared.FilterLightAndTariffValuesHelper;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceOutputSetting;
import org.opensmartgridplatform.domain.core.entities.RelayStatus;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceStatus;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceStatusMapped;
import org.opensmartgridplatform.domain.core.valueobjects.DomainType;
import org.opensmartgridplatform.domain.core.valueobjects.RelayType;
import org.opensmartgridplatform.domain.core.valueobjects.TariffValue;
import org.opensmartgridplatform.dto.valueobjects.DeviceStatusDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.NoDeviceResponseException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "domainTariffSwitchingAdHocManagementService")
@Transactional(value = "transactionManager")
public class AdHocManagementService extends AbstractService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdHocManagementService.class);

  /** Constructor */
  public AdHocManagementService() {
    // Parameterless constructor required for transactions...
  }

  // === GET STATUS ===

  /**
   * Retrieve status of device and provide a mapped response (PublicLighting or TariffSwitching)
   *
   * @param organisationIdentification identification of organization
   * @param deviceIdentification identification of device
   * @param allowedDomainType domain type performing requesting the status
   * @param messageType the type of the message
   * @param messagePriority the priority of the message
   * @throws FunctionalException in case the organization is not authorized or the device is not
   *     active
   */
  public void getStatus(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final DomainType allowedDomainType,
      final String messageType,
      final int messagePriority)
      throws FunctionalException {

    this.findOrganisation(organisationIdentification);
    final Device device = this.findActiveDevice(deviceIdentification);

    final org.opensmartgridplatform.dto.valueobjects.DomainTypeDto allowedDomainTypeDto =
        this.domainCoreMapper.map(
            allowedDomainType, org.opensmartgridplatform.dto.valueobjects.DomainTypeDto.class);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            correlationUid, organisationIdentification, deviceIdentification, allowedDomainTypeDto),
        messageType,
        messagePriority,
        device.getNetworkAddress());
  }

  public void handleGetStatusResponse(
      final DeviceStatusDto deviceStatusDto,
      final DomainType allowedDomainType,
      final CorrelationIds ids,
      final int messagePriority,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    ResponseMessageResultType result = deviceResult;
    OsgpException osgpException = exception;
    DeviceStatusMapped deviceStatusMapped = null;

    if (deviceResult == ResponseMessageResultType.NOT_OK || exception != null) {
      LOGGER.error("Device Response not ok.", osgpException);
    } else {
      final DeviceStatus status = this.domainCoreMapper.map(deviceStatusDto, DeviceStatus.class);

      final Ssld ssld =
          this.ssldRepository.findByDeviceIdentification(ids.getDeviceIdentification());

      final List<DeviceOutputSetting> deviceOutputSettings = ssld.getOutputSettings();

      final Map<Integer, DeviceOutputSetting> dosMap = new HashMap<>();
      for (final DeviceOutputSetting dos : deviceOutputSettings) {
        dosMap.put(dos.getExternalId(), dos);
      }

      if (status != null) {
        deviceStatusMapped =
            new DeviceStatusMapped(
                FilterLightAndTariffValuesHelper.filterTariffValues(
                    status.getLightValues(), dosMap, allowedDomainType),
                FilterLightAndTariffValuesHelper.filterLightValues(
                    status.getLightValues(), dosMap, allowedDomainType),
                status.getPreferredLinkType(),
                status.getActualLinkType(),
                status.getLightType(),
                status.getEventNotificationsMask());

        this.updateDeviceRelayOverview(ssld, deviceStatusMapped);
      } else {
        result = ResponseMessageResultType.NOT_OK;
        osgpException =
            new TechnicalException(
                ComponentType.DOMAIN_TARIFF_SWITCHING,
                "Device was not able to report status",
                new NoDeviceResponseException());
      }
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withIds(ids)
            .withResult(result)
            .withOsgpException(osgpException)
            .withDataObject(deviceStatusMapped)
            .withMessagePriority(messagePriority)
            .build();
    this.webServiceResponseMessageSender.send(responseMessage);
  }

  /** Updates the relay overview from a device based on the given device status. */
  private void updateDeviceRelayOverview(
      final Ssld device, final DeviceStatusMapped deviceStatusMapped) {
    final List<RelayStatus> relayStatuses = device.getRelayStatuses();

    for (final TariffValue tariffValue : deviceStatusMapped.getTariffValues()) {
      final Integer externalIndex = tariffValue.getIndex();
      final boolean state = this.isRelayOn(tariffValue.isHigh(), device, externalIndex);

      final RelayStatus oldRelayStatus = device.getRelayStatusByIndex(externalIndex);
      if (oldRelayStatus != null) {
        // Update the old relay status value
        oldRelayStatus.updateLastKnownState(state, Instant.now());
      } else {
        // Create a new relay status value
        final RelayStatus newRelayStatus =
            new RelayStatus.Builder(device, externalIndex)
                .withLastKnownState(state, Instant.now())
                .build();
        relayStatuses.add(newRelayStatus);
      }
    }

    this.ssldRepository.save(device);
  }

  private boolean isRelayOn(
      final boolean isHighTariff, final Ssld device, final Integer externalIndex) {
    // The relay state is on, during the LOW tariff period
    boolean state = !isHighTariff;

    final DeviceOutputSetting setting = device.getOutputSetting(externalIndex);

    if (setting != null && setting.getOutputType() == RelayType.TARIFF_REVERSED) {
      // Invert the state because TARIFF_REVERSED uses inverse values.
      state = !state;
    }

    return state;
  }
}
