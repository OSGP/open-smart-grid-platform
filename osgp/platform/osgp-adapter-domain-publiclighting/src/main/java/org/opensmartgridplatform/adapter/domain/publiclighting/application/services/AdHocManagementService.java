/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.services;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaRun;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.OsgpSystemCorrelationUid;
import org.opensmartgridplatform.adapter.domain.shared.FilterLightAndTariffValuesHelper;
import org.opensmartgridplatform.adapter.domain.shared.GetLightSensorStatusResponse;
import org.opensmartgridplatform.adapter.domain.shared.GetStatusResponse;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceOutputSetting;
import org.opensmartgridplatform.domain.core.entities.Event;
import org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice;
import org.opensmartgridplatform.domain.core.entities.RelayStatus;
import org.opensmartgridplatform.domain.core.entities.RtuDevice;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.exceptions.ValidationException;
import org.opensmartgridplatform.domain.core.repositories.EventRepository;
import org.opensmartgridplatform.domain.core.repositories.RtuDeviceRepository;
import org.opensmartgridplatform.domain.core.valueobjects.CdmaDevice;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceStatus;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceStatusMapped;
import org.opensmartgridplatform.domain.core.valueobjects.DomainType;
import org.opensmartgridplatform.domain.core.valueobjects.EventType;
import org.opensmartgridplatform.domain.core.valueobjects.LightSensorStatus;
import org.opensmartgridplatform.domain.core.valueobjects.LightSensorStatusType;
import org.opensmartgridplatform.domain.core.valueobjects.LightValue;
import org.opensmartgridplatform.domain.core.valueobjects.TransitionType;
import org.opensmartgridplatform.dto.valueobjects.DeviceStatusDto;
import org.opensmartgridplatform.dto.valueobjects.LightSensorStatusDto;
import org.opensmartgridplatform.dto.valueobjects.LightValueMessageDataContainerDto;
import org.opensmartgridplatform.dto.valueobjects.ResumeScheduleMessageDataContainerDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.NoDeviceResponseException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "domainPublicLightingAdHocManagementService")
@Transactional(value = "transactionManager")
public class AdHocManagementService extends AbstractService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdHocManagementService.class);

  @Autowired private EventRepository eventRepository;

  @Autowired private RtuDeviceRepository rtuDeviceRepository;

  @Autowired private SetTransitionService setTransitionService;

  /** Constructor */
  public AdHocManagementService() {
    // Parameterless constructor required for transactions...
  }

  // === SET LIGHT ===

  public void setLight(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final List<LightValue> lightValues,
      final String messageType,
      final int messagePriority)
      throws FunctionalException {

    LOGGER.debug(
        "setLight called for device {} with organisation {}",
        deviceIdentification,
        organisationIdentification);

    this.findOrganisation(organisationIdentification);
    final Device device = this.findActiveDevice(deviceIdentification);

    final List<org.opensmartgridplatform.dto.valueobjects.LightValueDto> lightValuesDto =
        this.domainCoreMapper.mapAsList(
            lightValues, org.opensmartgridplatform.dto.valueobjects.LightValueDto.class);
    final LightValueMessageDataContainerDto lightValueMessageDataContainer =
        new LightValueMessageDataContainerDto(lightValuesDto);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            correlationUid,
            organisationIdentification,
            deviceIdentification,
            lightValueMessageDataContainer),
        messageType,
        messagePriority,
        device.getIpAddress());
  }

  // === GET LIGHT SENSOR STATUS ===

  public void handleGetLightSensorStatusResponse(
      final LightSensorStatusDto lightSensorStatusDto,
      final CorrelationIds ids,
      final String messageType,
      final int messagePriority,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    LOGGER.info("handleResponse for MessageType: {}", messageType);
    final GetLightSensorStatusResponse response = new GetLightSensorStatusResponse();
    response.setOsgpException(exception);
    response.setResult(deviceResult);
    if (lightSensorStatusDto != null) {
      response.setLightSensorStatus(
          new LightSensorStatus(
              LightSensorStatusType.valueOf(lightSensorStatusDto.getStatus().name())));
      this.updateLastCommunicationTime(ids.getDeviceIdentification());
    }

    if (deviceResult == ResponseMessageResultType.NOT_OK || exception != null) {
      LOGGER.error("Device Response not ok.", exception);
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withIds(ids)
            .withResult(response.getResult())
            .withOsgpException(response.getOsgpException())
            .withDataObject(response.getLightSensorStatus())
            .withMessagePriority(messagePriority)
            .build();

    if (!OsgpSystemCorrelationUid.CORRELATION_UID.equals(ids.getCorrelationUid())) {
      this.webServiceResponseMessageSender.send(responseMessage);
    } else {
      LOGGER.info(
          "We used sensor status to keep 104 LMDs connected, ignore response: {}", responseMessage);
    }
  }

  public void updateLastCommunicationTime(final String deviceIdentification) {
    final LightMeasurementDevice lightMeasurementDevice =
        this.lightMeasurementDeviceRepository.findByDeviceIdentification(deviceIdentification);
    if (lightMeasurementDevice == null) {
      LOGGER.warn(
          "Device identification for update last communication time does not identify a known light measurement device: {}",
          deviceIdentification);
      return;
    }
    final Device gateway = lightMeasurementDevice.getGatewayDevice();
    final Instant lastCommunicationTime =
        this.updateLmdLastCommunicationTime(lightMeasurementDevice).getLastCommunicationTime();
    if (gateway != null) {
      this.rtuDeviceRepository
          .findById(gateway.getId())
          .ifPresent(rtu -> this.updateGatewayLastCommunicationTime(rtu, lastCommunicationTime));
    }
  }

  private void updateGatewayLastCommunicationTime(
      final RtuDevice rtuDevice, final Instant lastCommunicationTime) {
    rtuDevice.setLastSuccessfulConnectionTimestamp(lastCommunicationTime);
    rtuDevice.messageReceived(lastCommunicationTime);
    this.rtuDeviceRepository.save(rtuDevice);
  }

  // === GET STATUS ===

  /**
   * Retrieve status of device and provide a mapped response (PublicLighting or TariffSwitching)
   *
   * @param organisationIdentification identification of organization
   * @param deviceIdentification identification of device
   * @param correlationUid the correlation UID of the message
   * @param allowedDomainType domain type performing requesting the status
   * @param messageType the message type
   * @param messagePriority the priority of the message
   * @throws FunctionalException in case the organization is not authorized for this action or the
   *     device is not active.
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

    final String actualMessageType =
        LightMeasurementDevice.LMD_TYPE.equals(device.getDeviceType())
            ? DeviceFunction.GET_LIGHT_SENSOR_STATUS.name()
            : messageType;

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            correlationUid, organisationIdentification, deviceIdentification, allowedDomainTypeDto),
        actualMessageType,
        messagePriority,
        this.getIpAddress(device));
  }

  private String getIpAddress(final Device device) {
    String ipAddress = null;
    if (device.getGatewayDevice() != null) {
      ipAddress = device.getGatewayDevice().getIpAddress();
    }
    if (ipAddress == null) {
      ipAddress = device.getIpAddress();
    }
    return ipAddress;
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
          this.handleSsld(
              ids.getDeviceIdentification(), status, DomainType.PUBLIC_LIGHTING, response);
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
              ComponentType.DOMAIN_PUBLIC_LIGHTING,
              "Light measurement device was not able to report light sensor status",
              new NoDeviceResponseException()));
      response.setResult(ResponseMessageResultType.NOT_OK);
    }
  }

  private void handleSsld(
      final String deviceIdentification,
      final DeviceStatus status,
      final DomainType allowedDomainType,
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
                  status.getLightValues(), dosMap, allowedDomainType),
              FilterLightAndTariffValuesHelper.filterLightValues(
                  status.getLightValues(), dosMap, allowedDomainType),
              status.getPreferredLinkType(),
              status.getActualLinkType(),
              status.getLightType(),
              status.getEventNotificationsMask());

      // Update the relay overview with the relay information.
      this.updateDeviceRelayStatusForGetStatus(ssld, deviceStatusMapped);

      // Return mapped status using GetStatusResponse instance.
      response.setDeviceStatusMapped(deviceStatusMapped);
    } else {
      // No status received, create bad response.
      response.setDeviceStatusMapped(null);
      response.setOsgpException(
          new TechnicalException(
              ComponentType.DOMAIN_PUBLIC_LIGHTING,
              "SSLD was not able to report relay status",
              new NoDeviceResponseException()));
      response.setResult(ResponseMessageResultType.NOT_OK);
    }
  }

  // === RESUME SCHEDULE ===

  public void resumeSchedule(
      final CorrelationIds ids,
      final Integer index,
      final boolean isImmediate,
      final String messageType,
      final int messagePriority)
      throws FunctionalException {

    this.findOrganisation(ids.getOrganisationIdentification());
    final Device device = this.findActiveDevice(ids.getDeviceIdentification());
    final Ssld ssld = this.findSsldForDevice(device);

    if (!ssld.getHasSchedule()) {
      throw new FunctionalException(
          FunctionalExceptionType.UNSCHEDULED_DEVICE,
          ComponentType.DOMAIN_PUBLIC_LIGHTING,
          new ValidationException(
              String.format(
                  "Device %1$s does not have a schedule.", ids.getDeviceIdentification())));
    }

    final ResumeScheduleMessageDataContainerDto resumeScheduleMessageDataContainerDto =
        new ResumeScheduleMessageDataContainerDto(index, isImmediate);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(ids, resumeScheduleMessageDataContainerDto),
        messageType,
        messagePriority,
        device.getIpAddress());
  }

  // === TRANSITION MESSAGE FROM LIGHT MEASUREMENT DEVICE ===

  /**
   * Send transition message to SSLD's based on light measurement device trigger.
   *
   * @param organisationIdentification Organization issuing the request.
   * @param correlationUid The generated correlation UID.
   * @param event The light measurement {@link Event} for which to trigger transition messages.
   */
  public void handleLightMeasurementDeviceTransition(
      final String organisationIdentification, final String correlationUid, final Event event) {

    LightMeasurementDevice lmd =
        this.lightMeasurementDeviceRepository.findByDeviceIdentification(
            event.getDeviceIdentification());
    if (lmd == null) {
      LOGGER.error(
          "No light measurement device found for device identification: {}",
          event.getDeviceIdentification());
      return;
    }

    // Update last communication time for the LMD.
    lmd = this.updateLmdLastCommunicationTime(lmd);

    // Determine if the event is a duplicate. If so, quit.
    if (this.isDuplicateEvent(event, lmd)) {
      LOGGER.info(
          "Duplicate event detected for light measurement device: {}. Event[id:{} {} {} {} {}]",
          lmd.getDeviceIdentification(),
          event.getId(),
          event.getDateTime(),
          event.getDescription(),
          event.getEventType(),
          event.getIndex());
      return;
    }

    // Retrieve the devices per mast/segment
    final CdmaRun cdmaRun = this.getCdmaDevicesPerMastSegment(lmd);

    // Determine the transition type based on the event of the LMD.
    final TransitionType transitionType = this.determineTransitionTypeForEvent(event);

    // Send SET_TRANSITION messages to the SSLDs.
    this.setTransitionService.setTransitionForCdmaRun(
        cdmaRun, organisationIdentification, correlationUid, transitionType);
  }

  private CdmaRun getCdmaDevicesPerMastSegment(final LightMeasurementDevice lmd) {

    // Find all SSLDs which need to receive a SET_TRANSITION message.
    final List<CdmaDevice> devicesForLmd =
        this.ssldRepository.findCdmaBatchDevicesInUseForLmd(lmd, DeviceLifecycleStatus.IN_USE);

    final CdmaRun cdmaRun = new CdmaRun();
    devicesForLmd.forEach(cdmaRun::add);

    return cdmaRun;
  }

  private LightMeasurementDevice updateLmdLastCommunicationTime(final LightMeasurementDevice lmd) {
    final Instant now = Instant.now();
    LOGGER.info(
        "Trying to update lastCommunicationTime for light measurement device: {} at DateTime: {}",
        lmd.getDeviceIdentification(),
        Date.from(now));
    lmd.setLastCommunicationTime(now);

    final Device gateway = lmd.getGatewayDevice();
    if (gateway != null) {
      this.rtuDeviceRepository
          .findById(gateway.getId())
          .ifPresent(rtu -> this.updateGatewayLastCommunicationTime(rtu, now));
    }

    return this.lightMeasurementDeviceRepository.save(lmd);
  }

  private boolean isDuplicateEvent(final Event event, final LightMeasurementDevice lmd) {
    final List<Event> events =
        this.eventRepository.findTop2ByDeviceIdentificationOrderByDateTimeDesc(
            lmd.getDeviceIdentification());

    // Returns true if one of the events differs in date/time and has the
    // same event type
    return events.stream()
        .anyMatch(
            e ->
                !event.getDateTime().equals(e.getDateTime())
                    && event.getEventType().equals(e.getEventType()));
  }

  private TransitionType determineTransitionTypeForEvent(final Event event) {
    final TransitionType transitionType;
    if (EventType.LIGHT_SENSOR_REPORTS_DARK == event.getEventType()) {
      transitionType = TransitionType.DAY_NIGHT;
    } else {
      transitionType = TransitionType.NIGHT_DAY;
    }
    return transitionType;
  }

  // === SET LIGHT MEASUREMENT DEVICE ===

  /**
   * Couple an SSLD with a light measurement device.
   *
   * @param organisationIdentification Organization issuing the request.
   * @param deviceIdentification The SSLD.
   * @param correlationUid The generated correlation UID.
   * @param lightMeasurementDeviceIdentification The light measurement device.
   * @param messageType SET_LIGHTMEASUREMENT_DEVICE
   * @throws FunctionalException In case the organisation can not be found.
   */
  public void coupleLightMeasurementDeviceForSsld(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final String lightMeasurementDeviceIdentification,
      final String messageType)
      throws FunctionalException {

    LOGGER.debug(
        "setLightMeasurementDevice called for device {} with organisation {} and light measurement device {}, message type: {}, correlationUid: {}",
        deviceIdentification,
        organisationIdentification,
        lightMeasurementDeviceIdentification,
        messageType,
        correlationUid);

    this.findOrganisation(organisationIdentification);
    final Ssld ssld = this.ssldRepository.findByDeviceIdentification(deviceIdentification);
    if (ssld == null) {
      LOGGER.error("Unable to find ssld: {}", deviceIdentification);
      return;
    }

    final LightMeasurementDevice lightMeasurementDevice =
        this.lightMeasurementDeviceRepository.findByDeviceIdentification(
            lightMeasurementDeviceIdentification);
    if (lightMeasurementDevice == null) {
      LOGGER.error(
          "Unable to find light measurement device: {}", lightMeasurementDeviceIdentification);
      return;
    }

    ssld.setLightMeasurementDevice(lightMeasurementDevice);
    this.ssldRepository.save(ssld);

    LOGGER.info(
        "Set light measurement device: {} for ssld: {}",
        lightMeasurementDeviceIdentification,
        deviceIdentification);
  }

  /**
   * Updates the relay overview from a device for a get status response, based on the given device
   * status.
   *
   * @param device The device to update.
   */
  private void updateDeviceRelayStatusForGetStatus(
      final Ssld device, final DeviceStatusMapped deviceStatusMapped) {
    final List<RelayStatus> relayStatuses = device.getRelayStatuses();
    for (final LightValue lightValue : deviceStatusMapped.getLightValues()) {
      boolean updated = false;
      for (final RelayStatus relayStatus : relayStatuses) {
        if (relayStatus.getIndex() == lightValue.getIndex()) {
          relayStatus.updateLastKnownState(lightValue.isOn(), new Date());
          updated = true;
          break;
        }
      }
      if (!updated) {
        final RelayStatus newRelayStatus =
            new RelayStatus.Builder(device, lightValue.getIndex())
                .withLastKnownState(lightValue.isOn(), new Date())
                .build();
        relayStatuses.add(newRelayStatus);
      }
    }

    this.ssldRepository.save(device);
  }

  /** Logs the response of SET_TRANSITION calls. */
  public void handleSetTransitionResponse(
      final CorrelationIds ids,
      final String messageType,
      final int messagePriority,
      final ResponseMessageResultType responseMessageResultType,
      final OsgpException osgpException) {

    if (osgpException == null) {
      LOGGER.info(
          "Received response: {} for messageType: {}, messagePriority: {}, deviceIdentification: {}, organisationIdentification: {}, correlationUid: {}",
          responseMessageResultType.getValue(),
          messageType,
          messagePriority,
          ids.getDeviceIdentification(),
          ids.getOrganisationIdentification(),
          ids.getCorrelationUid());
    } else {
      LOGGER.error(
          "Exception: {} for response: {} for messageType: {}, messagePriority: {}, deviceIdentification: {}, organisationIdentification: {}, correlationUid: {}",
          osgpException.getMessage(),
          responseMessageResultType.getValue(),
          messageType,
          messagePriority,
          ids.getDeviceIdentification(),
          ids.getOrganisationIdentification(),
          ids.getCorrelationUid());
    }
  }
}
