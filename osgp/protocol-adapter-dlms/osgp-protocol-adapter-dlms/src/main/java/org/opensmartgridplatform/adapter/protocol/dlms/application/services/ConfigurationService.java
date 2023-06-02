//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.io.Serializable;
import java.util.List;
import org.openmuc.jdlms.AccessResultCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm.SetAlarmNotificationsCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.GetConfigurationObjectCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.SetConfigurationObjectCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.SetRandomisationSettingsCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.datetime.SetActivityCalendarCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.datetime.SetClockConfigurationCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.datetime.SetSpecialDaysCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.GetFirmwareVersionsCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.GetFirmwareVersionsGasCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus.GetMBusDeviceOnChannelCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus.GetMbusEncryptionKeyStatusByChannelCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus.GetMbusEncryptionKeyStatusCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.ConfigureDefinableLoadProfileCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.GetAdministrativeStatusCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.SetAdministrativeStatusCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup.SetPushSetupAlarmCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup.SetPushSetupLastGaspCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup.SetPushSetupSmsCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.security.GenerateAndReplaceKeyCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.security.ReplaceKeyCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.security.SetKeyOnGMeterCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActivityCalendarDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AdministrativeStatusTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmNotificationsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DefinableLoadProfileConfigurationDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetConfigurationObjectResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetFirmwareVersionQueryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetKeysRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetKeysResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMBusDeviceOnChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GprsOperationModeTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupAlarmDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupLastGaspDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupSmsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SecretTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetClockConfigurationRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetConfigurationObjectRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetKeyOnGMeterRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetKeysRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetMbusUserKeyByChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetRandomisationSettingsRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecialDayDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecialDaysRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecialDaysRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "dlmsConfigurationService")
public class ConfigurationService {
  private static final String VISUAL_SEPARATOR =
      "******************************************************";

  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);

  @Autowired private DomainHelperService domainHelperService;

  @Autowired private FirmwareService firmwareService;

  @Autowired private SetSpecialDaysCommandExecutor setSpecialDaysCommandExecutor;

  @Autowired private SetAlarmNotificationsCommandExecutor setAlarmNotificationsCommandExecutor;

  @Autowired private SetConfigurationObjectCommandExecutor setConfigurationObjectCommandExecutor;

  @Autowired private SetPushSetupAlarmCommandExecutor setPushSetupAlarmCommandExecutor;

  @Autowired private SetPushSetupLastGaspCommandExecutor setPushSetupLastGaspCommandExecutor;

  @Autowired private SetPushSetupSmsCommandExecutor setPushSetupSmsCommandExecutor;

  @Autowired private SetActivityCalendarCommandExecutor setActivityCalendarCommandExecutor;

  @Autowired private SetKeyOnGMeterCommandExecutor setKeyOnGMeterCommandExecutor;

  @Autowired private GetMBusDeviceOnChannelCommandExecutor getMBusDeviceOnChannelCommandExecutor;

  @Autowired private SetAdministrativeStatusCommandExecutor setAdministrativeStatusCommandExecutor;

  @Autowired private GetAdministrativeStatusCommandExecutor getAdministrativeStatusCommandExecutor;

  @Autowired private GetFirmwareVersionsCommandExecutor getFirmwareVersionCommandExecutor;

  @Autowired private GetFirmwareVersionsGasCommandExecutor getFirmwareVersionGasCommandExecutor;

  @Autowired private ReplaceKeyCommandExecutor replaceKeyCommandExecutor;

  @Autowired private SetClockConfigurationCommandExecutor setClockConfigurationCommandExecutor;

  @Autowired private GetConfigurationObjectCommandExecutor getConfigurationObjectCommandExecutor;

  @Autowired private GenerateAndReplaceKeyCommandExecutor generateAndReplaceKeyCommandExecutor;

  @Autowired
  private ConfigureDefinableLoadProfileCommandExecutor configureDefinableLoadProfileCommandExecutor;

  @Autowired
  private GetMbusEncryptionKeyStatusCommandExecutor getMbusEncryptionKeyStatusCommandExecutor;

  @Autowired
  private GetMbusEncryptionKeyStatusByChannelCommandExecutor
      getMbusEncryptionKeyStatusByChannelCommandExecutor;

  @Autowired
  private SetRandomisationSettingsCommandExecutor setRandomisationSettingsCommandExecutor;

  @Autowired private GetKeysService getKeysService;

  public void setSpecialDays(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final SpecialDaysRequestDto specialDaysRequest,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    // The Special days towards the Smart Meter
    final SpecialDaysRequestDataDto specialDaysRequestData =
        specialDaysRequest.getSpecialDaysRequestData();

    LOGGER.debug(VISUAL_SEPARATOR);
    LOGGER.debug("********** Set Special Days: 0-0:11.0.0.255 **********");
    LOGGER.debug(VISUAL_SEPARATOR);
    final List<SpecialDayDto> specialDays = specialDaysRequestData.getSpecialDays();
    for (final SpecialDayDto specialDay : specialDays) {
      LOGGER.debug("Date :{}, dayId : {} ", specialDay.getSpecialDayDate(), specialDay.getDayId());
    }
    LOGGER.debug(VISUAL_SEPARATOR);

    final AccessResultCode accessResultCode =
        this.setSpecialDaysCommandExecutor.execute(conn, device, specialDays, messageMetadata);
    if (!AccessResultCode.SUCCESS.equals(accessResultCode)) {
      throw new ProtocolAdapterException(
          "Set special days reported result is: " + accessResultCode);
    }
  }

  // === REQUEST Configuration Object DATA ===

  public void requestSetConfiguration(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final SetConfigurationObjectRequestDto setConfigurationObjectRequest,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    // Configuration Object towards the Smart Meter
    final ConfigurationObjectDto configurationObject =
        setConfigurationObjectRequest
            .getSetConfigurationObjectRequestData()
            .getConfigurationObject();

    final GprsOperationModeTypeDto gprsOperationModeType =
        configurationObject.getGprsOperationMode();
    final ConfigurationFlagsDto configurationFlags = configurationObject.getConfigurationFlags();

    LOGGER.debug(VISUAL_SEPARATOR);
    LOGGER.debug("******** Configuration Object: 0-1:94.31.3.255 *******");
    LOGGER.debug(VISUAL_SEPARATOR);
    LOGGER.debug(
        "Operation mode: {}",
        gprsOperationModeType == null ? "not altered by this request" : gprsOperationModeType);
    if (configurationFlags == null) {
      LOGGER.debug("Flags: none enabled or disabled by this request");
    } else {
      LOGGER.debug("{}", configurationFlags);
    }
    LOGGER.debug(VISUAL_SEPARATOR);

    final AccessResultCode accessResultCode =
        this.setConfigurationObjectCommandExecutor.execute(
            conn, device, configurationObject, messageMetadata);
    if (!AccessResultCode.SUCCESS.equals(accessResultCode)) {
      throw new ProtocolAdapterException(
          "Set configuration object reported result is: " + accessResultCode);
    }
  }

  public void requestSetAdministrativeStatus(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final AdministrativeStatusTypeDto administrativeStatusType,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    LOGGER.info("Device for Set Administrative Status is: {}", device);

    final AccessResultCode accessResultCode =
        this.setAdministrativeStatusCommandExecutor.execute(
            conn, device, administrativeStatusType, messageMetadata);
    if (AccessResultCode.SUCCESS != accessResultCode) {
      throw new ProtocolAdapterException(
          "AccessResultCode for set administrative status was not SUCCESS: " + accessResultCode);
    }
  }

  public void setAlarmNotifications(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final AlarmNotificationsDto alarmNotifications,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    LOGGER.info("Alarm Notifications to set on the device: {}", alarmNotifications);

    final AccessResultCode accessResultCode =
        this.setAlarmNotificationsCommandExecutor.execute(
            conn, device, alarmNotifications, messageMetadata);
    if (AccessResultCode.SUCCESS != accessResultCode) {
      throw new ProtocolAdapterException(
          "AccessResultCode for set alarm notifications was not SUCCESS: " + accessResultCode);
    }
  }

  public AdministrativeStatusTypeDto requestGetAdministrativeStatus(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    return this.getAdministrativeStatusCommandExecutor.execute(conn, device, null, messageMetadata);
  }

  public String setKeyOnGMeter(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final SetKeyOnGMeterRequestDto setEncryptionKeyRequest,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    LOGGER.info("Device for Set Key On G-Meter is: {}", device);
    this.setKeyOnGMeterCommandExecutor.execute(
        conn, device, setEncryptionKeyRequest, messageMetadata);
    return "Set Key On G-Meter Result is OK for device id: " + device.getDeviceIdentification();
  }

  public String setMbusUserKeyByChannel(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final SetMbusUserKeyByChannelRequestDataDto setMbusUserKeyByChannelRequestDataDto,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    LOGGER.info("Device for Set M-Bus User Key By Channel is: {}", device);

    final SetKeyOnGMeterRequestDto gMeterInfo =
        this.getMbusKeyExchangeData(
            conn, device, setMbusUserKeyByChannelRequestDataDto, messageMetadata);

    this.setKeyOnGMeterCommandExecutor.execute(conn, device, gMeterInfo, messageMetadata);

    return "Set M-Bus User Key By Channel Result is OK for device id: "
        + device.getDeviceIdentification();
  }

  public SetKeyOnGMeterRequestDto getMbusKeyExchangeData(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final SetMbusUserKeyByChannelRequestDataDto setMbusUserKeyByChannelRequestData,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    final GetMBusDeviceOnChannelRequestDataDto mbusDeviceOnChannelRequest =
        new GetMBusDeviceOnChannelRequestDataDto(
            device.getDeviceIdentification(), setMbusUserKeyByChannelRequestData.getChannel());
    final ChannelElementValuesDto channelElementValues =
        this.getMBusDeviceOnChannelCommandExecutor.execute(
            conn, device, mbusDeviceOnChannelRequest, messageMetadata);

    final DlmsDevice mbusDevice =
        this.domainHelperService.findMbusDevice(
            channelElementValues.getIdentificationNumber(),
            channelElementValues.getManufacturerIdentification());

    return new SetKeyOnGMeterRequestDto(
        mbusDevice.getDeviceIdentification(),
        setMbusUserKeyByChannelRequestData.getChannel(),
        SecretTypeDto.G_METER_ENCRYPTION_KEY,
        false);
  }

  public String setActivityCalendar(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ActivityCalendarDto activityCalendar,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException, FunctionalException {

    LOGGER.info("Device for Activity Calendar is: {}", device);

    this.setActivityCalendarCommandExecutor.execute(
        conn, device, activityCalendar, messageMetadata);

    return "Set Activity Calendar Result is OK for device id: "
        + device.getDeviceIdentification()
        + " calendar name: "
        + activityCalendar.getCalendarName();
  }

  public void setPushSetupAlarm(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final PushSetupAlarmDto pushSetupAlarm,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    LOGGER.info("Push Setup Alarm to set on the device: {}", pushSetupAlarm);

    final AccessResultCode accessResultCode =
        this.setPushSetupAlarmCommandExecutor.execute(
            conn, device, pushSetupAlarm, messageMetadata);

    if (AccessResultCode.SUCCESS != accessResultCode) {
      throw new ProtocolAdapterException(
          "AccessResultCode for set push setup alarm was not SUCCESS: " + accessResultCode);
    }
  }

  public void setPushSetupLastGasp(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final PushSetupLastGaspDto pushSetupLastGasp,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    LOGGER.info("Push Setup LastGasp to set on the device: {}", pushSetupLastGasp);

    final AccessResultCode accessResultCode =
        this.setPushSetupLastGaspCommandExecutor.execute(
            conn, device, pushSetupLastGasp, messageMetadata);

    if (AccessResultCode.SUCCESS != accessResultCode) {
      throw new ProtocolAdapterException(
          "AccessResultCode for set push setup last gasp was not SUCCESS: " + accessResultCode);
    }
  }

  public void setPushSetupSms(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final PushSetupSmsDto pushSetupSms,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    LOGGER.info("Push Setup Sms to set on the device: {}", pushSetupSms);

    final AccessResultCode accessResultCode =
        this.setPushSetupSmsCommandExecutor.execute(conn, device, pushSetupSms, messageMetadata);

    if (AccessResultCode.SUCCESS != accessResultCode) {
      throw new ProtocolAdapterException(
          "AccessResultCode for set push setup sms was not SUCCESS: " + accessResultCode);
    }
  }

  public Serializable requestFirmwareVersion(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final GetFirmwareVersionQueryDto queryDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException, FunctionalException {

    if (queryDto.isMbusQuery()) {
      return this.getFirmwareVersionGasCommandExecutor.execute(
          conn, device, queryDto, messageMetadata);
    }

    return (Serializable)
        this.getFirmwareVersionCommandExecutor.execute(conn, device, queryDto, messageMetadata);
  }

  public void generateAndEncrypt(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final MessageMetadata messageMetadata)
      throws OsgpException {
    try {

      this.generateAndReplaceKeyCommandExecutor.execute(conn, device, null, messageMetadata);

    } catch (final ProtocolAdapterException e) {
      LOGGER.error("Unexpected exception during replaceKeys.", e);
      throw e;
    }
  }

  public void replaceKeys(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final SetKeysRequestDto keySet,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    try {

      keySet.setGeneratedKeys(false);
      this.replaceKeyCommandExecutor.execute(conn, device, keySet, messageMetadata);

    } catch (final ProtocolAdapterException e) {
      LOGGER.error("Unexpected exception during replaceKeys.", e);
      throw e;
    }
  }

  public void setClockConfiguration(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final SetClockConfigurationRequestDto clockConfiguration,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    try {
      this.setClockConfigurationCommandExecutor.execute(
          conn, device, clockConfiguration, messageMetadata);
    } catch (final ProtocolAdapterException e) {
      LOGGER.error("Unexpected exception while setting clock configuration.", e);
      throw e;
    }
  }

  public UpdateFirmwareResponseDto updateFirmware(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final UpdateFirmwareRequestDto updateFirmwareRequestDto,
      final MessageMetadata messageMetadata)
      throws OsgpException {
    LOGGER.info(
        "Updating firmware of device {} to firmware with identification {}",
        device,
        updateFirmwareRequestDto.getFirmwareIdentification());

    return this.firmwareService.updateFirmware(
        conn, device, updateFirmwareRequestDto, messageMetadata);
  }

  public GetConfigurationObjectResponseDto requestGetConfigurationObject(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    return new GetConfigurationObjectResponseDto(
        this.getConfigurationObjectCommandExecutor.execute(conn, device, null, messageMetadata));
  }

  public void configureDefinableLoadProfile(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final DefinableLoadProfileConfigurationDto definableLoadProfileConfiguration,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {
    try {
      this.configureDefinableLoadProfileCommandExecutor.execute(
          conn, device, definableLoadProfileConfiguration, messageMetadata);
    } catch (final ProtocolAdapterException e) {
      LOGGER.error("Unexpected exception while configuring definable load profile.", e);
      throw e;
    }
  }

  public GetMbusEncryptionKeyStatusResponseDto requestGetMbusEncryptionKeyStatus(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final GetMbusEncryptionKeyStatusRequestDto getMbusEncryptionKeyStatusRequest,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    return this.getMbusEncryptionKeyStatusCommandExecutor.execute(
        conn, device, getMbusEncryptionKeyStatusRequest, messageMetadata);
  }

  public GetMbusEncryptionKeyStatusByChannelResponseDto requestGetMbusEncryptionKeyStatusByChannel(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final GetMbusEncryptionKeyStatusByChannelRequestDataDto
          getMbusEncryptionKeyStatusByChannelRequest,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    return this.getMbusEncryptionKeyStatusByChannelCommandExecutor.execute(
        conn, device, getMbusEncryptionKeyStatusByChannelRequest, messageMetadata);
  }

  public void requestSetRandomizationSettings(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final SetRandomisationSettingsRequestDataDto setRandomisationSettingsRequestDataDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final AccessResultCode accessResultCode =
        this.setRandomisationSettingsCommandExecutor.execute(
            conn, device, setRandomisationSettingsRequestDataDto, messageMetadata);

    if (AccessResultCode.SUCCESS != accessResultCode) {
      throw new ProtocolAdapterException(
          "AccessResultCode for set randomisation settings was not SUCCESS: " + accessResultCode);
    }
  }

  public GetKeysResponseDto requestGetKeys(
      final DlmsDevice device,
      final GetKeysRequestDto getKeysRequestDto,
      final MessageMetadata messageMetadata) {

    return this.getKeysService.getKeys(device, getKeysRequestDto, messageMetadata);
  }
}
