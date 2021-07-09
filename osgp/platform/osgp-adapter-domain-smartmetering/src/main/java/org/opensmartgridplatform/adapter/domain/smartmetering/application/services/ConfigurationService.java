/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActivityCalendar;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AdministrativeStatusType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmNotifications;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DefinableLoadProfileConfigurationData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EncryptionKeyStatusType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.FirmwareVersionGasResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.FirmwareVersionResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetConfigurationObjectRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetConfigurationObjectResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetFirmwareVersionQuery;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetKeysRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetKeysResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetKeysResponseData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SecretType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetClockConfigurationRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetKeysRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetMbusUserKeyByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetRandomisationSettingsRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.UpdateFirmwareRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.UpdateFirmwareResponse;
import org.opensmartgridplatform.domain.smartmetering.exceptions.GatewayDeviceNotSetForMbusDeviceException;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionDto;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionGasDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActivityCalendarDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AdministrativeStatusTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmNotificationsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DefinableLoadProfileConfigurationDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GMeterInfoDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetConfigurationObjectRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetConfigurationObjectResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetFirmwareVersionQueryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetKeysRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetKeysResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.KeyDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupAlarmDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupSmsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SecretTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetClockConfigurationRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetConfigurationObjectRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetKeysRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetMbusUserKeyByChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetRandomisationSettingsRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecialDaysRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service(value = "domainSmartMeteringConfigurationService")
@Transactional(value = "transactionManager")
public class ConfigurationService {

  private static final String DEVICE_RESPONSE_NOT_OK_LOG_MSG =
      "Device Response not ok. Unexpected Exception";

  @Autowired
  @Qualifier(value = "domainSmartMeteringOutboundOsgpCoreRequestsMessageSender")
  private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

  @Autowired private ConfigurationMapper configurationMapper;

  @Autowired
  @Qualifier(value = "domainSmartMeteringOutboundWebServiceResponsesMessageSender")
  private WebServiceResponseMessageSender webServiceResponseMessageSender;

  @Autowired private DomainHelperService domainHelperService;

  @Autowired private FirmwareService firmwareService;

  public ConfigurationService() {
    // Parameterless constructor required for transactions...
  }

  public void setSpecialDays(
      final DeviceMessageMetadata deviceMessageMetadata,
      final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecialDaysRequest
          specialDaysRequestValueObject)
      throws FunctionalException {

    log.info(
        "setSpecialDays for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    final SpecialDaysRequestDto specialDaysRequestDto =
        this.configurationMapper.map(specialDaysRequestValueObject, SpecialDaysRequestDto.class);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            deviceMessageMetadata.getDeviceIdentification(),
            smartMeteringDevice.getIpAddress(),
            specialDaysRequestDto),
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void setConfigurationObject(
      final DeviceMessageMetadata deviceMessageMetadata,
      final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
              .SetConfigurationObjectRequest
          setConfigurationObjectRequestValueObject)
      throws FunctionalException {

    log.info(
        "setConfigurationObject for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    final SetConfigurationObjectRequestDto setConfigurationObjectRequestDto =
        this.configurationMapper.map(
            setConfigurationObjectRequestValueObject, SetConfigurationObjectRequestDto.class);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            deviceMessageMetadata.getDeviceIdentification(),
            smartMeteringDevice.getIpAddress(),
            setConfigurationObjectRequestDto),
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void setPushSetupAlarm(
      final DeviceMessageMetadata deviceMessageMetadata,
      final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupAlarm
          pushSetupAlarm)
      throws FunctionalException {

    log.info(
        "setPushSetupAlarm for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    final PushSetupAlarmDto pushSetupAlarmDto =
        this.configurationMapper.map(pushSetupAlarm, PushSetupAlarmDto.class);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            deviceMessageMetadata.getDeviceIdentification(),
            smartMeteringDevice.getIpAddress(),
            pushSetupAlarmDto),
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void setPushSetupSms(
      final DeviceMessageMetadata deviceMessageMetadata,
      final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupSms
          pushSetupSms)
      throws FunctionalException {

    log.info(
        "setPushSetupSms for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    final PushSetupSmsDto pushSetupSmsDto =
        this.configurationMapper.map(pushSetupSms, PushSetupSmsDto.class);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            deviceMessageMetadata.getDeviceIdentification(),
            smartMeteringDevice.getIpAddress(),
            pushSetupSmsDto),
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void handleSpecialDaysResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    log.info(
        "handleSpecialDaysresponse for MessageType: {}", deviceMessageMetadata.getMessageType());

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      log.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(exception)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  public void setAlarmNotifications(
      final DeviceMessageMetadata deviceMessageMetadata,
      final AlarmNotifications alarmNotifications)
      throws FunctionalException {

    log.info(
        "setAlarmNotifications for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    final AlarmNotificationsDto alarmNotificationsDto =
        this.configurationMapper.map(alarmNotifications, AlarmNotificationsDto.class);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            deviceMessageMetadata.getDeviceIdentification(),
            smartMeteringDevice.getIpAddress(),
            alarmNotificationsDto),
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void setAdministrativeStatus(
      final DeviceMessageMetadata deviceMessageMetadata,
      final AdministrativeStatusType administrativeStatusType)
      throws FunctionalException {

    log.info(
        "Set Administrative Status for organisationIdentification: {} for deviceIdentification: {} to status:"
            + " {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification(),
        administrativeStatusType);

    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    final AdministrativeStatusTypeDto administrativeStatusTypeDto =
        this.configurationMapper.map(administrativeStatusType, AdministrativeStatusTypeDto.class);

    final RequestMessage requestMessage =
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            deviceMessageMetadata.getDeviceIdentification(),
            smartMeteringDevice.getIpAddress(),
            administrativeStatusTypeDto);
    this.osgpCoreRequestMessageSender.send(
        requestMessage,
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void handleSetAdministrativeStatusResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    log.info(
        "handleSetAdministrativeStatusResponse for MessageType: {}, with result: {}",
        deviceMessageMetadata.getMessageType(),
        deviceResult);

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      log.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(exception)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  public void getAdministrativeStatus(
      final DeviceMessageMetadata deviceMessageMetadata,
      final AdministrativeStatusType administrativeStatusType)
      throws FunctionalException {

    log.info(
        "Get Administrative Status for organisationIdentification: {} for deviceIdentification: {} to status:"
            + " {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification(),
        administrativeStatusType);

    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    final RequestMessage requestMessage =
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            deviceMessageMetadata.getDeviceIdentification(),
            smartMeteringDevice.getIpAddress(),
            this.configurationMapper.map(
                administrativeStatusType, AdministrativeStatusTypeDto.class));
    this.osgpCoreRequestMessageSender.send(
        requestMessage,
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void handleGetAdministrativeStatusResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType responseMessageResultType,
      final OsgpException osgpException,
      final AdministrativeStatusTypeDto administrativeStatusTypeDto) {

    log.info(
        "handleGetAdministrativeStatusResponse for MessageType: {}, with result: {}",
        deviceMessageMetadata.getMessageType(),
        responseMessageResultType);

    ResponseMessageResultType result = responseMessageResultType;
    if (osgpException != null) {
      log.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, osgpException);
      result = ResponseMessageResultType.NOT_OK;
    }

    final AdministrativeStatusType administrativeStatusType =
        this.configurationMapper.map(administrativeStatusTypeDto, AdministrativeStatusType.class);

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(osgpException)
            .withDataObject(administrativeStatusType)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  public void setActivityCalendar(
      final DeviceMessageMetadata deviceMessageMetadata, final ActivityCalendar activityCalendar)
      throws FunctionalException {

    log.info(
        "set Activity Calendar for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    final ActivityCalendarDto activityCalendarDto =
        this.configurationMapper.map(activityCalendar, ActivityCalendarDto.class);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            deviceMessageMetadata.getDeviceIdentification(),
            smartMeteringDevice.getIpAddress(),
            activityCalendarDto),
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void handleSetAlarmNotificationsResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    log.info(
        "handleSetAlarmNotificationsResponse for MessageType: {}",
        deviceMessageMetadata.getMessageType());

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      log.error("Set Alarm Notifications Response not ok. Unexpected Exception", exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(exception)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  public void handleSetConfigurationObjectResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    log.info(
        "handle SetConfigurationObject response for MessageType: {}",
        deviceMessageMetadata.getMessageType());

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      log.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(exception)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  public void handleSetPushSetupAlarmResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    log.info(
        "handleSetPushSetupAlarmResponse for MessageType: {}",
        deviceMessageMetadata.getMessageType());

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      log.error("Set Push Setup Alarm Response not ok. Unexpected Exception", exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(exception)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  public void handleSetPushSetupSmsResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    log.info(
        "handleSetPushSetupSmsResponse for MessageType: {}",
        deviceMessageMetadata.getMessageType());

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      log.error("Set Push Setup Sms Response not ok. Unexpected Exception", exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(exception)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  public void handleSetActivityCalendarResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType responseMessageResultType,
      final OsgpException exception,
      final String resultString) {
    log.info(
        "handleSetActivityCalendarResponse for MessageType: {}",
        deviceMessageMetadata.getMessageType());

    ResponseMessageResultType result = responseMessageResultType;
    if (exception != null) {
      log.error("Set Activity Calendar Response not ok. Unexpected Exception", exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(exception)
            .withDataObject(resultString)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  public void setEncryptionKeyExchangeOnGMeter(final DeviceMessageMetadata deviceMessageMetadata)
      throws FunctionalException {

    log.info(
        "set Encryption Key Exchange On G-Meter for organisationIdentification: {} for deviceIdentification: "
            + "{}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter gasDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    final Device gatewayDevice = gasDevice.getGatewayDevice();
    if (gatewayDevice == null) {
      /*
       * For now throw a FunctionalException, based on the same reasoning
       * as with the channel a couple of lines up. As soon as we have
       * scenario's with direct communication with gas meters this will
       * have to be changed.
       */
      throw new FunctionalException(
          FunctionalExceptionType.GATEWAY_DEVICE_NOT_SET_FOR_MBUS_DEVICE,
          ComponentType.DOMAIN_SMART_METERING,
          new GatewayDeviceNotSetForMbusDeviceException());
    }

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            gatewayDevice.getDeviceIdentification(),
            gatewayDevice.getIpAddress(),
            new GMeterInfoDto(gasDevice.getChannel(), gasDevice.getDeviceIdentification())),
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void handleSetEncryptionKeyExchangeOnGMeterResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType responseMessageResultType,
      final OsgpException exception) {
    log.info(
        "handleSetEncryptionKeyExchangeOnGMeterResponse for MessageType: {}",
        deviceMessageMetadata.getMessageType());

    ResponseMessageResultType result = responseMessageResultType;
    if (exception != null) {
      log.error(
          "Set Encryption Key Exchange On G-Meter Response not ok. Unexpected Exception",
          exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(exception)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  public void setMbusUserKeyByChannel(
      final DeviceMessageMetadata deviceMessageMetadata,
      final SetMbusUserKeyByChannelRequestData setMbusUserKeyByChannelRequestData)
      throws FunctionalException {

    log.info(
        "Set M-Bus User Key By Channel for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter gatewayDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            deviceMessageMetadata.getDeviceIdentification(),
            gatewayDevice.getIpAddress(),
            new SetMbusUserKeyByChannelRequestDataDto(
                setMbusUserKeyByChannelRequestData.getChannel())),
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void handleSetMbusUserKeyByChannelResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType responseMessageResultType,
      final OsgpException exception) {
    log.info(
        "handleSetMbusUserKeyByChannelResponse for MessageType: {}",
        deviceMessageMetadata.getMessageType());

    ResponseMessageResultType result = responseMessageResultType;
    if (exception != null) {
      log.error("Set M-Bus User Key By Channel Response not ok. Unexpected Exception", exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(exception)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  public void replaceKeys(
      final DeviceMessageMetadata deviceMessageMetadata, final SetKeysRequestData keySet)
      throws FunctionalException {

    log.info(
        "replaceKeys for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    final SetKeysRequestDto keySetDto =
        this.configurationMapper.map(keySet, SetKeysRequestDto.class);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            deviceMessageMetadata.getDeviceIdentification(),
            smartMeteringDevice.getIpAddress(),
            keySetDto),
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void handleReplaceKeysResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    log.info(
        "handleReplaceKeysResponse for MessageType: {}", deviceMessageMetadata.getMessageType());

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      log.error("Replace Keys Response not ok. Unexpected Exception", exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(exception)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  public void generateAndReplaceKeys(final DeviceMessageMetadata deviceMessageMetadata)
      throws FunctionalException {
    log.info(
        "Generate and replace keys for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            deviceMessageMetadata.getDeviceIdentification(),
            smartMeteringDevice.getIpAddress(),
            null),
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void handleGenerateAndReplaceKeysResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    log.info(
        "Handle generate and replace keys response for MessageType: {}",
        deviceMessageMetadata.getMessageType());

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      log.error("Generate and replace keys response not ok. Unexpected Exception", exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(exception)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  /**
   * Delegates the requests of the retrieval of the firmware version(s) from the protocol adapter
   * layer to the core layer
   *
   * @param deviceMessageMetadata contains the message meta data
   * @param getFirmwareVersionQuery
   * @throws FunctionalException is thrown when the device cannot be found in the database or when
   *     the device is a G meter and the channel and/or gateway is not configured
   */
  public void requestFirmwareVersion(
      final DeviceMessageMetadata deviceMessageMetadata,
      final GetFirmwareVersionQuery getFirmwareVersionQuery)
      throws FunctionalException {

    log.info(
        "requestFirmwareVersion for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter smartMeter =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    if (getFirmwareVersionQuery.isMbusDevice()) {
      if (smartMeter.getChannel() == null) {
        /*
         * For now, throw a FunctionalException. As soon as we can
         * communicate with some types of gas meters directly, and not
         * through an M-Bus port of an energy meter, this will have to
         * be changed.
         */
        throw new FunctionalException(
            FunctionalExceptionType.VALIDATION_ERROR,
            ComponentType.DOMAIN_SMART_METERING,
            new AssertionError(
                "Retrieving firmware version for gas meter. No channel configured."));
      }

      final Device gatewayDevice = smartMeter.getGatewayDevice();

      if (gatewayDevice == null) {
        /*
         * For now throw a FunctionalException, based on the same
         * reasoning as with the channel a couple of lines up. As soon
         * as we have scenario's with direct communication with gas
         * meters this will have to be changed.
         */
        throw new FunctionalException(
            FunctionalExceptionType.VALIDATION_ERROR,
            ComponentType.DOMAIN_SMART_METERING,
            new AssertionError(
                "Retrieving firmware version for gas meter. No gateway device found."));
      }

      this.osgpCoreRequestMessageSender.send(
          new RequestMessage(
              deviceMessageMetadata.getCorrelationUid(),
              deviceMessageMetadata.getOrganisationIdentification(),
              gatewayDevice.getDeviceIdentification(),
              gatewayDevice.getIpAddress(),
              new GetFirmwareVersionQueryDto(
                  ChannelDto.fromNumber(smartMeter.getChannel()),
                  deviceMessageMetadata.getDeviceIdentification())),
          deviceMessageMetadata.getMessageType(),
          deviceMessageMetadata.getMessagePriority(),
          deviceMessageMetadata.getScheduleTime(),
          deviceMessageMetadata.bypassRetry());
    } else {
      this.osgpCoreRequestMessageSender.send(
          new RequestMessage(
              deviceMessageMetadata.getCorrelationUid(),
              deviceMessageMetadata.getOrganisationIdentification(),
              deviceMessageMetadata.getDeviceIdentification(),
              smartMeter.getIpAddress(),
              new GetFirmwareVersionQueryDto()),
          deviceMessageMetadata.getMessageType(),
          deviceMessageMetadata.getMessagePriority(),
          deviceMessageMetadata.getScheduleTime(),
          deviceMessageMetadata.bypassRetry());
    }
  }

  /**
   * Maps the firmware Dto's to value objects and sends it back to the ws-adapter layer
   *
   * @param deviceMessageMetadata contains the message meta data
   * @param deviceResult indicates whether the execution was successful
   * @param exception contains the exception if one was thrown
   * @param firmwareVersionList contains the firmware result list
   */
  public void handleGetFirmwareVersionResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception,
      final List<FirmwareVersionDto> firmwareVersionList) {

    log.info(
        "handleGetFirmwareVersionResponse for MessageType: {}",
        deviceMessageMetadata.getMessageType());

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      log.error("Get firmware version response not ok. Unexpected Exception", exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final List<FirmwareVersion> firmwareVersions =
        this.configurationMapper.mapAsList(firmwareVersionList, FirmwareVersion.class);

    final FirmwareVersionResponse firmwareVersionResponse =
        new FirmwareVersionResponse(firmwareVersions);

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(exception)
            .withDataObject(firmwareVersionResponse)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());

    this.firmwareService.saveFirmwareVersionsReturnedFromDevice(
        deviceMessageMetadata.getDeviceIdentification(), firmwareVersions);
  }

  public void handleGetFirmwareVersionGasResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception,
      final FirmwareVersionGasDto firmwareVersionGas) {
    log.info(
        "handleGetFirmwareVersionGasResponse for MessageType: {}",
        deviceMessageMetadata.getMessageType());

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      log.error("Get firmware version response not ok. Unexpected Exception", exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final FirmwareVersion firmwareVersion =
        this.configurationMapper.map(firmwareVersionGas, FirmwareVersion.class);

    final FirmwareVersionGasResponse firmwareVersionGasResponse =
        new FirmwareVersionGasResponse(firmwareVersion);

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(exception)
            .withDataObject(firmwareVersionGasResponse)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());

    this.firmwareService.saveFirmwareVersionsReturnedFromDevice(
        firmwareVersionGas.getMbusDeviceIdentification(), Arrays.asList(firmwareVersion));
  }

  public void requestUpdateFirmware(
      final DeviceMessageMetadata deviceMessageMetadata,
      final UpdateFirmwareRequestData updateFirmwareRequestData)
      throws FunctionalException {

    log.info(
        "requestUpdateFirmware for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter smartMeter =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    final String firmwareFileIdentifier =
        this.firmwareService.determineFirmwareFileIdentifier(
            smartMeter, updateFirmwareRequestData.getVersionByModuleType());

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            deviceMessageMetadata.getDeviceIdentification(),
            smartMeter.getIpAddress(),
            firmwareFileIdentifier),
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void handleUpdateFirmwareResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception,
      final UpdateFirmwareResponseDto updateFirmwareResponseDto) {

    log.info(
        "handleUpdateFirmwareResponse for MessageType: {}", deviceMessageMetadata.getMessageType());

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      log.error("Update firmware response not ok. Unexpected Exception", exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final UpdateFirmwareResponse updateFirmwareResponse =
        this.configurationMapper.map(updateFirmwareResponseDto, UpdateFirmwareResponse.class);

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(exception)
            .withDataObject(updateFirmwareResponse)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  public void setClockConfiguration(
      final DeviceMessageMetadata deviceMessageMetadata,
      final SetClockConfigurationRequestData setClockConfigurationRequest)
      throws FunctionalException {

    log.info(
        "setClockConfiguration for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    final SetClockConfigurationRequestDto setClockConfigurationRequestDto =
        this.configurationMapper.map(
            setClockConfigurationRequest, SetClockConfigurationRequestDto.class);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            deviceMessageMetadata.getDeviceIdentification(),
            smartMeteringDevice.getIpAddress(),
            setClockConfigurationRequestDto),
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void handleSetClockConfigurationResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    log.info(
        "handleSetClockConfigurationResponse for MessageType: {}",
        deviceMessageMetadata.getMessageType());

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      log.error("Set Clock Configuration Response not ok. Unexpected Exception", exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(exception)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  public void getConfigurationObject(
      final DeviceMessageMetadata deviceMessageMetadata,
      final GetConfigurationObjectRequest getConfigurationObjectRequest)
      throws FunctionalException {

    log.info(
        "getConfigurationObject for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    final GetConfigurationObjectRequestDto getConfigurationObjectRequestDto =
        this.configurationMapper.map(
            getConfigurationObjectRequest, GetConfigurationObjectRequestDto.class);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            deviceMessageMetadata.getDeviceIdentification(),
            smartMeteringDevice.getIpAddress(),
            getConfigurationObjectRequestDto),
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void handleGetConfigurationObjectResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception,
      final GetConfigurationObjectResponseDto resultData) {

    log.info(
        "handle GetConfigurationObject response for MessageType: {}",
        deviceMessageMetadata.getMessageType());

    final GetConfigurationObjectResponse response =
        this.configurationMapper.map(resultData, GetConfigurationObjectResponse.class);

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      log.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(exception)
            .withDataObject(response)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  public void configureDefinableLoadProfile(
      final DeviceMessageMetadata deviceMessageMetadata,
      final DefinableLoadProfileConfigurationData definableLoadProfileConfigurationData)
      throws FunctionalException {

    log.info(
        "configureDefinableLoadProfile for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    final DefinableLoadProfileConfigurationDto definableLoadProfileConfigurationDto =
        this.configurationMapper.map(
            definableLoadProfileConfigurationData, DefinableLoadProfileConfigurationDto.class);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            deviceMessageMetadata.getDeviceIdentification(),
            smartMeteringDevice.getIpAddress(),
            definableLoadProfileConfigurationDto),
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void handleConfigureDefinableLoadProfileResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    log.info(
        "handleConfigureDefinableLoadProfileResponse for MessageType: {}",
        deviceMessageMetadata.getMessageType());

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      log.error(
          "Configure Definable Load Profile Response not ok. Unexpected Exception", exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(exception)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  public void getMbusEncryptionKeyStatus(final DeviceMessageMetadata deviceMessageMetadata)
      throws FunctionalException {

    log.info(
        "getMbusEncryptionKeyStatus for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter mbusDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    final Device gatewayDevice = mbusDevice.getGatewayDevice();
    if (gatewayDevice == null) {
      throw new FunctionalException(
          FunctionalExceptionType.GATEWAY_DEVICE_NOT_SET_FOR_MBUS_DEVICE,
          ComponentType.DOMAIN_SMART_METERING,
          new GatewayDeviceNotSetForMbusDeviceException());
    }

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            gatewayDevice.getDeviceIdentification(),
            gatewayDevice.getIpAddress(),
            new GetMbusEncryptionKeyStatusRequestDto(
                mbusDevice.getDeviceIdentification(), mbusDevice.getChannel())),
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void handleGetMbusEncryptionKeyStatusResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType resultType,
      final OsgpException exception,
      final GetMbusEncryptionKeyStatusResponseDto getMbusEncryptionKeyStatusResponseDto) {

    log.info(
        "handleGetMbusEncryptionKeyStatusResponse for MessageType: {}",
        deviceMessageMetadata.getMessageType());

    final String mbusDeviceIdentification =
        getMbusEncryptionKeyStatusResponseDto.getMbusDeviceIdentification();
    final EncryptionKeyStatusType encryptionKeyStatusType =
        EncryptionKeyStatusType.valueOf(
            getMbusEncryptionKeyStatusResponseDto.getEncryptionKeyStatus().name());

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(mbusDeviceIdentification)
            .withResult(resultType)
            .withOsgpException(exception)
            .withDataObject(encryptionKeyStatusType)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  public void getMbusEncryptionKeyStatusByChannel(
      final DeviceMessageMetadata deviceMessageMetadata,
      final GetMbusEncryptionKeyStatusByChannelRequestData
          getMbusEncryptionKeyStatusByChannelRequestData)
      throws FunctionalException {

    log.info(
        "getMbusEncryptionKeyStatusByChannel for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter gatewayDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            gatewayDevice.getDeviceIdentification(),
            gatewayDevice.getIpAddress(),
            new GetMbusEncryptionKeyStatusByChannelRequestDataDto(
                getMbusEncryptionKeyStatusByChannelRequestData.getChannel())),
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void handleGetMbusEncryptionKeyStatusByChannelResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType resultType,
      final OsgpException exception,
      final GetMbusEncryptionKeyStatusByChannelResponseDto
          getMbusEncryptionKeyStatusByChannelResponseDto) {

    log.info(
        "handleGetMbusEncryptionKeyStatusByChannelResponse for MessageType: {}",
        deviceMessageMetadata.getMessageType());

    final EncryptionKeyStatusType encryptionKeyStatusType =
        EncryptionKeyStatusType.valueOf(
            getMbusEncryptionKeyStatusByChannelResponseDto.getEncryptionKeyStatus().name());

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(resultType)
            .withOsgpException(exception)
            .withDataObject(encryptionKeyStatusType)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  public void setRandomisationSettings(
      final DeviceMessageMetadata deviceMessageMetadata,
      final SetRandomisationSettingsRequestData data)
      throws FunctionalException {

    log.info(
        "setRandomisationSettings for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    final SetRandomisationSettingsRequestDataDto setRandomisationSettingsRequestDataDto =
        new SetRandomisationSettingsRequestDataDto(
            data.getDirectAttach(),
            data.getRandomisationStartWindow(),
            data.getMultiplicationFactor(),
            data.getNumberOfRetries());

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            deviceMessageMetadata.getDeviceIdentification(),
            smartMeteringDevice.getIpAddress(),
            setRandomisationSettingsRequestDataDto),
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void handleSetRandomisationSettingsResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    log.info(
        "handle SetRandomisationSettings response for MessageType: {}",
        deviceMessageMetadata.getMessageType());

    ResponseMessageResultType result = deviceResult;
    if (exception != null) {
      log.error(DEVICE_RESPONSE_NOT_OK_LOG_MSG, exception);
      result = ResponseMessageResultType.NOT_OK;
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(result)
            .withOsgpException(exception)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }

  public void getKeys(
      final DeviceMessageMetadata deviceMessageMetadata,
      final GetKeysRequestData getKeysRequestData)
      throws FunctionalException {

    log.info(
        "getKeys for organisationIdentification: {} for deviceIdentification: {}",
        deviceMessageMetadata.getOrganisationIdentification(),
        deviceMessageMetadata.getDeviceIdentification());

    final SmartMeter smartMeteringDevice =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    final List<SecretTypeDto> secretTypes =
        getKeysRequestData.getSecretTypes().stream()
            .map(secretType -> SecretTypeDto.valueOf(secretType.name()))
            .collect(Collectors.toList());

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            deviceMessageMetadata.getCorrelationUid(),
            deviceMessageMetadata.getOrganisationIdentification(),
            smartMeteringDevice.getDeviceIdentification(),
            smartMeteringDevice.getIpAddress(),
            new GetKeysRequestDto(secretTypes)),
        deviceMessageMetadata.getMessageType(),
        deviceMessageMetadata.getMessagePriority(),
        deviceMessageMetadata.getScheduleTime(),
        deviceMessageMetadata.bypassRetry());
  }

  public void handleGetKeysResponse(
      final DeviceMessageMetadata deviceMessageMetadata,
      final ResponseMessageResultType resultType,
      final OsgpException exception,
      final GetKeysResponseDto getKeysResponseDto) {

    log.info("handleGetKeysResponse for MessageType: {}", deviceMessageMetadata.getMessageType());

    final List<KeyDto> keys = getKeysResponseDto.getKeys();

    final List<GetKeysResponseData> getKeysResponseData =
        keys.stream()
            .map(
                key ->
                    new GetKeysResponseData(
                        SecretType.valueOf(key.getSecretType().name()), key.getSecret()))
            .collect(Collectors.toList());

    final GetKeysResponse getKeysResponse = new GetKeysResponse(getKeysResponseData);

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(deviceMessageMetadata.getCorrelationUid())
            .withOrganisationIdentification(deviceMessageMetadata.getOrganisationIdentification())
            .withDeviceIdentification(deviceMessageMetadata.getDeviceIdentification())
            .withResult(resultType)
            .withOsgpException(exception)
            .withDataObject(getKeysResponse)
            .withMessagePriority(deviceMessageMetadata.getMessagePriority())
            .build();
    this.webServiceResponseMessageSender.send(
        responseMessage, deviceMessageMetadata.getMessageType());
  }
}
