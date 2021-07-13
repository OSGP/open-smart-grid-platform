/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.services;

import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateMidnight;
import org.opensmartgridplatform.adapter.ws.core.infra.jms.CommonRequestMessage;
import org.opensmartgridplatform.adapter.ws.core.infra.jms.CommonRequestMessageSender;
import org.opensmartgridplatform.adapter.ws.core.infra.jms.CommonResponseMessageFinder;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceAuthorizationRepository;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceRepository;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableLightMeasurementDeviceRepository;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableSsldRepository;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.exceptions.ExistingEntityException;
import org.opensmartgridplatform.domain.core.exceptions.NotAuthorizedException;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.opensmartgridplatform.domain.core.valueobjects.PlatformFunction;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.validation.Identification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@SuppressWarnings("deprecation")
@Service(value = "wsCoreDeviceInstallationService")
@Validated
public class DeviceInstallationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceInstallationService.class);

  @Autowired private Integer recentDevicesPeriod;

  @Autowired private WritableDeviceAuthorizationRepository writableAuthorizationRepository;

  @Autowired private DomainHelperService domainHelperService;

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private WritableDeviceRepository writableDeviceRepository;

  @Autowired private WritableSsldRepository writableSsldRepository;

  @Autowired
  private WritableLightMeasurementDeviceRepository writableLightMeasurementDeviceRepository;

  @Autowired private CorrelationIdProviderService correlationIdProviderService;

  @Autowired private CommonRequestMessageSender commonRequestMessageSender;

  @Autowired private CommonResponseMessageFinder commonResponseMessageFinder;

  DeviceInstallationService() {
    // Parameterless constructor required for transactions
  }

  @Transactional(value = "writableTransactionManager")
  public void addDevice(
      @Identification final String organisationIdentification,
      @Valid final Device newDevice,
      @Identification final String ownerOrganisationIdentification)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_ORGANISATIONS);
    this.domainHelperService.isOrganisationEnabled(organisation);

    // If the device already exists, throw an exception.
    final Device existingDevice =
        this.writableDeviceRepository.findByDeviceIdentification(
            newDevice.getDeviceIdentification());

    // Added additional check on device type: if device type is empty
    // there has been no communication with the device yet
    // and it should be possible to overwrite the existing device
    // It would probably be better to use isActivated for this, however this
    // would require additional changes in applications currently using this
    // field
    if (existingDevice != null && StringUtils.isNotBlank(existingDevice.getDeviceType())) {
      throw new FunctionalException(
          FunctionalExceptionType.EXISTING_DEVICE,
          ComponentType.WS_CORE,
          new ExistingEntityException(Device.class, newDevice.getDeviceIdentification()));
    }

    Ssld ssld;

    if (existingDevice != null) {
      // Update existing device
      ssld =
          this.writableSsldRepository.findByDeviceIdentification(
              newDevice.getDeviceIdentification());
      ssld.updateMetaData(
          newDevice.getAlias(), newDevice.getContainerAddress(), newDevice.getGpsCoordinates());
      ssld.getAuthorizations().clear();
    } else {
      // Create a new SSLD instance.
      ssld =
          new Ssld(
              newDevice.getDeviceIdentification(),
              newDevice.getAlias(),
              newDevice.getContainerAddress(),
              newDevice.getGpsCoordinates(),
              null);
    }
    ssld.setHasSchedule(false);
    ssld.setDeviceModel(newDevice.getDeviceModel());

    final Organisation ownerOrganisation =
        this.domainHelperService.findOrganisation(ownerOrganisationIdentification);
    ssld = this.writableSsldRepository.save(ssld);
    final DeviceAuthorization authorization =
        ssld.addAuthorization(ownerOrganisation, DeviceFunctionGroup.OWNER);
    this.writableAuthorizationRepository.save(authorization);
    LOGGER.info(
        "Created new device {} with owner {}",
        newDevice.getDeviceIdentification(),
        ownerOrganisationIdentification);
  }

  @Transactional(value = "writableTransactionManager")
  public void updateDevice(
      @Identification final String organisationIdentification, @Valid final Ssld updateDevice)
      throws FunctionalException {

    final Ssld existingDevice =
        this.writableSsldRepository.findByDeviceIdentification(
            updateDevice.getDeviceIdentification());
    if (existingDevice == null) {
      // device does not exist
      LOGGER.info("Device does not exist, nothing to update.");
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_DEVICE,
          ComponentType.WS_CORE,
          new UnknownEntityException(Device.class, updateDevice.getDeviceIdentification()));
    }

    final List<DeviceAuthorization> owners =
        this.writableAuthorizationRepository.findByDeviceAndFunctionGroup(
            existingDevice, DeviceFunctionGroup.OWNER);

    // Check organisation against owner of device
    boolean isOwner = false;
    for (final DeviceAuthorization owner : owners) {
      if (owner
          .getOrganisation()
          .getOrganisationIdentification()
          .equalsIgnoreCase(organisationIdentification)) {
        isOwner = true;
      }
    }

    if (!isOwner) {
      LOGGER.info("Device has no owner yet, or organisation is not the owner.");
      throw new FunctionalException(
          FunctionalExceptionType.UNAUTHORIZED,
          ComponentType.WS_CORE,
          new NotAuthorizedException(organisationIdentification));
    }

    // Update the device
    existingDevice.updateMetaData(
        updateDevice.getAlias(),
        updateDevice.getContainerAddress(),
        updateDevice.getGpsCoordinates());
    existingDevice.setPublicKeyPresent(updateDevice.isPublicKeyPresent());

    this.writableSsldRepository.save(existingDevice);
  }

  @Transactional(value = "writableTransactionManager")
  public void addLightMeasurementDevice(
      @Identification final String organisationIdentification,
      @Valid final LightMeasurementDevice newLightMeasurementDevice,
      @Identification final String ownerOrganisationIdentification)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_ORGANISATIONS);
    this.domainHelperService.isOrganisationEnabled(organisation);

    // If the device already exists, throw an exception.
    final Device existingDevice =
        this.writableDeviceRepository.findByDeviceIdentification(
            newLightMeasurementDevice.getDeviceIdentification());

    // Added additional check on device type: if device type is empty
    // there has been no communication with the device yet
    // and it should be possible to overwrite the existing device
    // It would probably be better to use isActivated for this, however this
    // would require additional changes in applications currently using this
    // field
    if (existingDevice != null && StringUtils.isNotBlank(existingDevice.getDeviceType())) {
      throw new FunctionalException(
          FunctionalExceptionType.EXISTING_DEVICE,
          ComponentType.WS_CORE,
          new ExistingEntityException(
              Device.class, newLightMeasurementDevice.getDeviceIdentification()));
    }

    LightMeasurementDevice lmd;

    if (existingDevice != null) {
      // Update existing light measurement device
      lmd =
          this.writableLightMeasurementDeviceRepository.findByDeviceIdentification(
              newLightMeasurementDevice.getDeviceIdentification());
      lmd.updateMetaData(
          newLightMeasurementDevice.getAlias(),
          newLightMeasurementDevice.getContainerAddress(),
          newLightMeasurementDevice.getGpsCoordinates());
      lmd.getAuthorizations().clear();
    } else {
      // Create a new LMD instance.
      lmd =
          new LightMeasurementDevice(
              newLightMeasurementDevice.getDeviceIdentification(),
              newLightMeasurementDevice.getAlias(),
              newLightMeasurementDevice.getContainerAddress(),
              newLightMeasurementDevice.getGpsCoordinates(),
              null);
    }
    lmd.setDeviceModel(newLightMeasurementDevice.getDeviceModel());
    lmd.setDescription(newLightMeasurementDevice.getDescription());
    lmd.setCode(newLightMeasurementDevice.getCode());
    lmd.setColor(newLightMeasurementDevice.getColor());
    lmd.setDigitalInput(newLightMeasurementDevice.getDigitalInput());

    final Organisation ownerOrganisation =
        this.domainHelperService.findOrganisation(ownerOrganisationIdentification);
    lmd = this.writableLightMeasurementDeviceRepository.save(lmd);
    final DeviceAuthorization authorization =
        lmd.addAuthorization(ownerOrganisation, DeviceFunctionGroup.OWNER);
    this.writableAuthorizationRepository.save(authorization);
    LOGGER.info(
        "Created new light measurement device {} with owner {}",
        newLightMeasurementDevice.getDeviceIdentification(),
        ownerOrganisationIdentification);
  }

  @Transactional(value = "writableTransactionManager")
  public void updateLightMeasurementDevice(
      @Identification final String organisationIdentification,
      @Valid final LightMeasurementDevice updateLightMeasurementDevice)
      throws FunctionalException {

    final LightMeasurementDevice existingLmd =
        this.writableLightMeasurementDeviceRepository.findByDeviceIdentification(
            updateLightMeasurementDevice.getDeviceIdentification());
    if (existingLmd == null) {
      // device does not exist
      LOGGER.info("Device does not exist, nothing to update.");
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_DEVICE,
          ComponentType.WS_CORE,
          new UnknownEntityException(
              Device.class, updateLightMeasurementDevice.getDeviceIdentification()));
    }

    final List<DeviceAuthorization> owners =
        this.writableAuthorizationRepository.findByDeviceAndFunctionGroup(
            existingLmd, DeviceFunctionGroup.OWNER);

    // Check organisation against owner of device
    boolean isOwner = false;
    for (final DeviceAuthorization owner : owners) {
      if (owner
          .getOrganisation()
          .getOrganisationIdentification()
          .equalsIgnoreCase(organisationIdentification)) {
        isOwner = true;
      }
    }

    if (!isOwner) {
      LOGGER.info("Device has no owner yet, or organisation is not the owner.");
      throw new FunctionalException(
          FunctionalExceptionType.UNAUTHORIZED,
          ComponentType.WS_CORE,
          new NotAuthorizedException(organisationIdentification));
    }

    // Update LMD
    existingLmd.updateMetaData(
        updateLightMeasurementDevice.getAlias(),
        updateLightMeasurementDevice.getContainerAddress(),
        updateLightMeasurementDevice.getGpsCoordinates());
    existingLmd.setDeviceModel(updateLightMeasurementDevice.getDeviceModel());
    existingLmd.setDescription(updateLightMeasurementDevice.getDescription());
    existingLmd.setCode(updateLightMeasurementDevice.getCode());
    existingLmd.setColor(updateLightMeasurementDevice.getColor());
    existingLmd.setDigitalInput(updateLightMeasurementDevice.getDigitalInput());

    this.writableLightMeasurementDeviceRepository.save(existingLmd);
  }

  @Transactional(value = "transactionManager")
  public List<Device> findRecentDevices(@Identification final String organisationIdentification)
      throws FunctionalException {
    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);

    final Date fromDate = new DateMidnight().minusDays(this.recentDevicesPeriod).toDate();
    return this.deviceRepository.findRecentDevices(organisation, fromDate);
  }

  // === GET STATUS ===

  @Transactional(value = "transactionManager")
  public String enqueueGetStatusRequest(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      final int messagePriority)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

    this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_STATUS);

    LOGGER.debug(
        "enqueueGetStatusRequest called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final MessageMetadata messageMetadata =
        new MessageMetadata.Builder()
            .withDeviceIdentification(deviceIdentification)
            .withOrganisationIdentification(organisationIdentification)
            .withCorrelationUid(correlationUid)
            .withMessageType(MessageType.GET_STATUS.name())
            .withMessagePriority(messagePriority)
            .build();

    final CommonRequestMessage message =
        new CommonRequestMessage.Builder().messageMetadata(messageMetadata).build();

    this.commonRequestMessageSender.send(message);

    return correlationUid;
  }

  @Transactional(value = "transactionManager")
  public ResponseMessage dequeueGetStatusResponse(final String correlationUid)
      throws OsgpException {
    return this.commonResponseMessageFinder.findMessage(correlationUid);
  }

  // === START DEVICE TEST ===

  @Transactional(value = "transactionManager")
  public String enqueueStartDeviceTestRequest(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      final int messagePriority)
      throws FunctionalException {

    LOGGER.debug("Queue start device test request");

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

    this.domainHelperService.isAllowed(organisation, device, DeviceFunction.START_SELF_TEST);

    LOGGER.debug(
        "enqueueStartDeviceTestRequest called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final MessageMetadata messageMetadata =
        new MessageMetadata.Builder()
            .withDeviceIdentification(deviceIdentification)
            .withOrganisationIdentification(organisationIdentification)
            .withCorrelationUid(correlationUid)
            .withMessageType(MessageType.START_SELF_TEST.name())
            .withMessagePriority(messagePriority)
            .build();

    final CommonRequestMessage message =
        new CommonRequestMessage.Builder().messageMetadata(messageMetadata).build();

    this.commonRequestMessageSender.send(message);

    return correlationUid;
  }

  @Transactional(value = "transactionManager")
  public ResponseMessage dequeueStartDeviceTestResponse(final String correlationUid)
      throws OsgpException {
    LOGGER.debug("Dequeue Start Device Test response");

    return this.commonResponseMessageFinder.findMessage(correlationUid);
  }

  // === STOP DEVICE TEST ===

  @Transactional(value = "transactionManager")
  public String enqueueStopDeviceTestRequest(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      final int messagePriority)
      throws FunctionalException {

    LOGGER.debug("Queue stop device test request");

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

    this.domainHelperService.isAllowed(organisation, device, DeviceFunction.STOP_SELF_TEST);

    LOGGER.debug(
        "enqueueStopDeviceTestRequest called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final MessageMetadata messageMetadata =
        new MessageMetadata.Builder()
            .withDeviceIdentification(deviceIdentification)
            .withOrganisationIdentification(organisationIdentification)
            .withCorrelationUid(correlationUid)
            .withMessageType(MessageType.STOP_SELF_TEST.name())
            .withMessagePriority(messagePriority)
            .build();

    final CommonRequestMessage message =
        new CommonRequestMessage.Builder().messageMetadata(messageMetadata).build();

    this.commonRequestMessageSender.send(message);

    return correlationUid;
  }

  @Transactional(value = "transactionManager")
  public ResponseMessage dequeueStopDeviceTestResponse(final String correlationUid)
      throws OsgpException {
    LOGGER.debug("Dequeue Stop Device Test response");

    return this.commonResponseMessageFinder.findMessage(correlationUid);
  }
}
