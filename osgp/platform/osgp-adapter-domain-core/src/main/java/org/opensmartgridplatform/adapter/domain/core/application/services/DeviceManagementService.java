/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.application.services;

import java.util.List;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.valueobjects.CdmaSettings;
import org.opensmartgridplatform.domain.core.valueobjects.Certification;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.opensmartgridplatform.domain.core.valueobjects.EventNotificationType;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationMessageDataContainerDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "domainCoreDeviceManagementService")
public class DeviceManagementService extends AbstractService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementService.class);

  @Autowired private TransactionalDeviceService transactionalDeviceService;

  @Transactional(value = "transactionManager")
  public void setEventNotifications(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final List<EventNotificationType> eventNotifications,
      final String messageType,
      final int messagePriority)
      throws FunctionalException {

    LOGGER.debug(
        "setEventNotifications called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    this.findOrganisation(organisationIdentification);
    final Device device = this.findActiveDevice(deviceIdentification);

    final List<org.opensmartgridplatform.dto.valueobjects.EventNotificationTypeDto>
        eventNotificationsDto =
            this.domainCoreMapper.mapAsList(
                eventNotifications,
                org.opensmartgridplatform.dto.valueobjects.EventNotificationTypeDto.class);
    final EventNotificationMessageDataContainerDto eventNotificationMessageDataContainer =
        new EventNotificationMessageDataContainerDto(eventNotificationsDto);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            correlationUid,
            organisationIdentification,
            deviceIdentification,
            eventNotificationMessageDataContainer),
        messageType,
        messagePriority,
        device.getIpAddress());
  }

  @Transactional(value = "transactionManager")
  public void updateDeviceSslCertification(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final Certification certification,
      final String messageType,
      final int messagePriority)
      throws FunctionalException {
    LOGGER.debug(
        "UpdateDeviceSslCertification called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    this.findOrganisation(organisationIdentification);
    final Device device = this.findActiveDevice(deviceIdentification);

    if (certification == null) {
      LOGGER.info("Certification is empty, skip sending a request to device");
      return;
    }

    final org.opensmartgridplatform.dto.valueobjects.CertificationDto certificationDto =
        this.domainCoreMapper.map(
            certification, org.opensmartgridplatform.dto.valueobjects.CertificationDto.class);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            correlationUid, organisationIdentification, deviceIdentification, certificationDto),
        messageType,
        messagePriority,
        device.getIpAddress());
  }

  @Transactional(value = "transactionManager")
  public void setDeviceVerificationKey(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final String verificationKey,
      final String messageType,
      final int messagePriority)
      throws FunctionalException {
    LOGGER.debug(
        "SetDeviceVerificationKey called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    this.findOrganisation(organisationIdentification);
    final Device device = this.findActiveDevice(deviceIdentification);

    if (verificationKey == null) {
      LOGGER.info("Verification key is empty, skip sending a request to device");
      return;
    }

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            correlationUid, organisationIdentification, deviceIdentification, verificationKey),
        messageType,
        messagePriority,
        device.getIpAddress());
  }

  public void setDeviceLifecycleStatus(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final DeviceLifecycleStatus deviceLifecycleStatus)
      throws FunctionalException {

    LOGGER.debug(
        "SetDeviceLifecycleStatus called with organisation {}, deviceLifecycleStatus {} and deviceIdentification {}",
        organisationIdentification,
        deviceLifecycleStatus,
        deviceIdentification);

    this.findOrganisation(organisationIdentification);

    this.transactionalDeviceService.updateDeviceLifecycleStatus(
        deviceIdentification, deviceLifecycleStatus);

    final ResponseMessageResultType result = ResponseMessageResultType.OK;

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(correlationUid)
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(deviceIdentification)
            .withResult(result)
            .build();
    this.webServiceResponseMessageSender.send(responseMessage);
  }

  public void updateDeviceCdmaSettings(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final CdmaSettings cdmaSettings)
      throws FunctionalException {
    LOGGER.debug(
        "UpdateDeviceCdmaSettings called with organisation {}, deviceIdentification {}, and {}",
        organisationIdentification,
        deviceIdentification,
        cdmaSettings);

    this.findOrganisation(organisationIdentification);

    this.transactionalDeviceService.updateDeviceCdmaSettings(deviceIdentification, cdmaSettings);

    final ResponseMessageResultType result = ResponseMessageResultType.OK;

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(correlationUid)
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(deviceIdentification)
            .withResult(result)
            .build();
    this.webServiceResponseMessageSender.send(responseMessage);
  }
}
