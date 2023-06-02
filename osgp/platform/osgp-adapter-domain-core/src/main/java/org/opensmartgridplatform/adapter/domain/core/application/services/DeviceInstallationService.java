//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.core.application.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.adapter.domain.shared.FilterLightAndTariffValuesHelper;
import org.opensmartgridplatform.adapter.domain.shared.GetStatusResponse;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceOutputSetting;
import org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceStatus;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceStatusMapped;
import org.opensmartgridplatform.domain.core.valueobjects.DomainType;
import org.opensmartgridplatform.dto.valueobjects.DeviceStatusDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.NoDeviceResponseException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.validation.Identification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "domainCoreDeviceInstallationService")
@Transactional(value = "transactionManager")
public class DeviceInstallationService extends AbstractService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceInstallationService.class);

  /** Constructor */
  public DeviceInstallationService() {
    // Parameterless constructor required for transactions...
  }

  // === GET STATUS ===

  public void getStatus(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final String messageType,
      final int messagePriority)
      throws FunctionalException {

    this.findOrganisation(organisationIdentification);
    final Device device = this.findActiveDevice(deviceIdentification);

    final String actualMessageType =
        LightMeasurementDevice.LMD_TYPE.equals(device.getDeviceType())
            ? DeviceFunction.GET_LIGHT_SENSOR_STATUS.name()
            : messageType;

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, null),
        actualMessageType,
        messagePriority,
        device.getIpAddress());
  }

  public void handleGetStatusResponse(
      final DeviceStatusDto deviceStatusDto,
      final CorrelationIds ids,
      final String messageType,
      final int messagePriority,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    LOGGER.info("handleResponse for MessageType: {}", messageType);
    final GetStatusResponse response = new GetStatusResponse();
    response.setOsgpException(exception);
    response.setResult(deviceResult);

    if (deviceResult == ResponseMessageResultType.NOT_OK || exception != null) {
      LOGGER.error("Device Response not ok.", exception);
    } else {
      final DeviceStatus status = this.domainCoreMapper.map(deviceStatusDto, DeviceStatus.class);
      try {
        final Device dev = this.deviceDomainService.searchDevice(ids.getDeviceIdentification());
        if (LightMeasurementDevice.LMD_TYPE.equals(dev.getDeviceType())) {
          this.handleLmd(status, response);
        } else {
          this.handleSsld(ids.getDeviceIdentification(), status, response);
        }
      } catch (final FunctionalException e) {
        LOGGER.error("Caught FunctionalException", e);
      }
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withIds(ids)
            .withResult(response.getResult())
            .withOsgpException(response.getOsgpException())
            .withDataObject(response.getDeviceStatusMapped())
            .withMessagePriority(messagePriority)
            .withMessageType(MessageType.GET_STATUS.name())
            .build();
    this.webServiceResponseMessageSender.send(responseMessage);
  }

  private void handleLmd(final DeviceStatus status, final GetStatusResponse response) {
    if (status != null) {
      final DeviceStatusMapped deviceStatusMapped =
          new DeviceStatusMapped(
              null,
              status.getLightValues(),
              status.getPreferredLinkType(),
              status.getActualLinkType(),
              status.getLightType(),
              status.getEventNotificationsMask());
      // Return mapped status using GetStatusResponse instance.
      response.setDeviceStatusMapped(deviceStatusMapped);
    } else {
      // No status received, create bad response.
      response.setDeviceStatusMapped(null);
      response.setOsgpException(
          new TechnicalException(
              ComponentType.DOMAIN_CORE,
              "Light measurement device was not able to report light sensor status",
              new NoDeviceResponseException()));
      response.setResult(ResponseMessageResultType.NOT_OK);
    }
  }

  private void handleSsld(
      final String deviceIdentification,
      final DeviceStatus status,
      final GetStatusResponse response) {

    // Find device and output settings.
    final Ssld ssld = this.ssldRepository.findByDeviceIdentification(deviceIdentification);
    final List<DeviceOutputSetting> deviceOutputSettings = ssld.getOutputSettings();

    // Create map with external relay number as key set.
    final Map<Integer, DeviceOutputSetting> dosMap = new HashMap<>();
    for (final DeviceOutputSetting dos : deviceOutputSettings) {
      dosMap.put(dos.getExternalId(), dos);
    }

    if (status != null) {
      // Map the DeviceStatus for SSLD.
      final DeviceStatusMapped deviceStatusMapped =
          new DeviceStatusMapped(
              FilterLightAndTariffValuesHelper.filterTariffValues(
                  status.getLightValues(), dosMap, DomainType.TARIFF_SWITCHING),
              FilterLightAndTariffValuesHelper.filterLightValues(
                  status.getLightValues(), dosMap, DomainType.PUBLIC_LIGHTING),
              status.getPreferredLinkType(),
              status.getActualLinkType(),
              status.getLightType(),
              status.getEventNotificationsMask());

      deviceStatusMapped.setBootLoaderVersion(status.getBootLoaderVersion());
      deviceStatusMapped.setCurrentConfigurationBackUsed(status.getCurrentConfigurationBackUsed());
      deviceStatusMapped.setCurrentIp(status.getCurrentIp());
      deviceStatusMapped.setCurrentTime(status.getCurrentTime());
      deviceStatusMapped.setDcOutputVoltageCurrent(status.getDcOutputVoltageCurrent());
      deviceStatusMapped.setDcOutputVoltageMaximum(status.getDcOutputVoltageMaximum());
      deviceStatusMapped.setEventNotificationsMask(status.getEventNotificationsMask());
      deviceStatusMapped.setExternalFlashMemSize(status.getExternalFlashMemSize());
      deviceStatusMapped.setFirmwareVersion(status.getFirmwareVersion());
      deviceStatusMapped.setHardwareId(status.getHardwareId());
      deviceStatusMapped.setInternalFlashMemSize(status.getInternalFlashMemSize());
      deviceStatusMapped.setLastInternalTestResultCode(status.getLastInternalTestResultCode());
      deviceStatusMapped.setMacAddress(status.getMacAddress());
      deviceStatusMapped.setMaximumOutputPowerOnDcOutput(status.getMaximumOutputPowerOnDcOutput());
      deviceStatusMapped.setName(status.getName());
      deviceStatusMapped.setNumberOfOutputs(status.getNumberOfOutputs());
      deviceStatusMapped.setSerialNumber(status.getSerialNumber());
      deviceStatusMapped.setStartupCounter(status.getStartupCounter());

      // Return mapped status using GetStatusResponse instance.
      response.setDeviceStatusMapped(deviceStatusMapped);
    } else {
      // No status received, create bad response.
      response.setDeviceStatusMapped(null);
      response.setOsgpException(
          new TechnicalException(
              ComponentType.DOMAIN_CORE,
              "SSLD was not able to report relay status",
              new NoDeviceResponseException()));
      response.setResult(ResponseMessageResultType.NOT_OK);
    }
  }

  // === START DEVICE TEST ===

  public void startSelfTest(
      @Identification final String deviceIdentification,
      @Identification final String organisationIdentification,
      final String correlationUid,
      final String messageType,
      final int messagePriority)
      throws FunctionalException {

    LOGGER.debug(
        "startSelfTest called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    this.findOrganisation(organisationIdentification);
    final Device device = this.findActiveDevice(deviceIdentification);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, null),
        messageType,
        messagePriority,
        device.getIpAddress());
  }

  // === STOP DEVICE TEST ===

  public void stopSelfTest(
      @Identification final String deviceIdentification,
      @Identification final String organisationIdentification,
      final String correlationUid,
      final String messageType,
      final int messagePriority)
      throws FunctionalException {

    LOGGER.debug(
        "stopSelfTest called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    this.findOrganisation(organisationIdentification);
    final Device device = this.findActiveDevice(deviceIdentification);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, null),
        messageType,
        messagePriority,
        device.getIpAddress());
  }
}
