/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

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
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus.GetMBusDeviceOnChannelCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus.GetMbusEncryptionKeyStatusByChannelCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus.GetMbusEncryptionKeyStatusCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.ConfigureDefinableLoadProfileCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.GetAdministrativeStatusCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.SetAdministrativeStatusCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup.SetPushSetupAlarmCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup.SetPushSetupSmsCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.security.GenerateAndReplaceKeyCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.security.ReplaceKeyCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.security.SetEncryptionKeyExchangeOnGMeterCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActivityCalendarDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AdministrativeStatusTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmNotificationsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DefinableLoadProfileConfigurationDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GMeterInfoDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetConfigurationObjectResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMBusDeviceOnChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GprsOperationModeTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupAlarmDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupSmsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetClockConfigurationRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetConfigurationObjectRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetKeysRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetMbusUserKeyByChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetRandomisationSettingsRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecialDayDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecialDaysRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecialDaysRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "dlmsConfigurationService")
public class ConfigurationService {
    private static final String VISUAL_SEPARATOR = "******************************************************";

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private FirmwareService firmwareService;

    @Autowired
    private SetSpecialDaysCommandExecutor setSpecialDaysCommandExecutor;

    @Autowired
    private SetAlarmNotificationsCommandExecutor setAlarmNotificationsCommandExecutor;

    @Autowired
    private SetConfigurationObjectCommandExecutor setConfigurationObjectCommandExecutor;

    @Autowired
    private SetPushSetupAlarmCommandExecutor setPushSetupAlarmCommandExecutor;

    @Autowired
    private SetPushSetupSmsCommandExecutor setPushSetupSmsCommandExecutor;

    @Autowired
    private SetActivityCalendarCommandExecutor setActivityCalendarCommandExecutor;

    @Autowired
    private SetEncryptionKeyExchangeOnGMeterCommandExecutor setEncryptionKeyExchangeOnGMeterCommandExecutor;

    @Autowired
    private GetMBusDeviceOnChannelCommandExecutor getMBusDeviceOnChannelCommandExecutor;

    @Autowired
    private SetAdministrativeStatusCommandExecutor setAdministrativeStatusCommandExecutor;

    @Autowired
    private GetAdministrativeStatusCommandExecutor getAdministrativeStatusCommandExecutor;

    @Autowired
    private GetFirmwareVersionsCommandExecutor getFirmwareVersionCommandExecutor;

    @Autowired
    private ReplaceKeyCommandExecutor replaceKeyCommandExecutor;

    @Autowired
    private SetClockConfigurationCommandExecutor setClockConfigurationCommandExecutor;

    @Autowired
    private GetConfigurationObjectCommandExecutor getConfigurationObjectCommandExecutor;

    @Autowired
    private GenerateAndReplaceKeyCommandExecutor generateAndReplaceKeyCommandExecutor;

    @Autowired
    private ConfigureDefinableLoadProfileCommandExecutor configureDefinableLoadProfileCommandExecutor;

    @Autowired
    private GetMbusEncryptionKeyStatusCommandExecutor getMbusEncryptionKeyStatusCommandExecutor;

    @Autowired
    private GetMbusEncryptionKeyStatusByChannelCommandExecutor getMbusEncryptionKeyStatusByChannelCommandExecutor;

    @Autowired
    private SetRandomisationSettingsCommandExecutor setRandomisationSettingsCommandExecutor;

    public void setSpecialDays(final DlmsConnectionManager conn, final DlmsDevice device,
            final SpecialDaysRequestDto specialDaysRequest) throws ProtocolAdapterException {

        // The Special days towards the Smart Meter
        final SpecialDaysRequestDataDto specialDaysRequestData = specialDaysRequest.getSpecialDaysRequestData();

        LOGGER.info(VISUAL_SEPARATOR);
        LOGGER.info("********** Set Special Days: 0-0:11.0.0.255 **********");
        LOGGER.info(VISUAL_SEPARATOR);
        final List<SpecialDayDto> specialDays = specialDaysRequestData.getSpecialDays();
        for (final SpecialDayDto specialDay : specialDays) {
            LOGGER.info("Date :{}, dayId : {} ", specialDay.getSpecialDayDate(), specialDay.getDayId());
        }
        LOGGER.info(VISUAL_SEPARATOR);

        final AccessResultCode accessResultCode = this.setSpecialDaysCommandExecutor.execute(conn, device, specialDays);
        if (!AccessResultCode.SUCCESS.equals(accessResultCode)) {
            throw new ProtocolAdapterException("Set special days reported result is: " + accessResultCode);
        }
    }

    // === REQUEST Configuration Object DATA ===

    public void requestSetConfiguration(final DlmsConnectionManager conn, final DlmsDevice device,
            final SetConfigurationObjectRequestDto setConfigurationObjectRequest) throws ProtocolAdapterException {

        // Configuration Object towards the Smart Meter
        final ConfigurationObjectDto configurationObject =
                setConfigurationObjectRequest.getSetConfigurationObjectRequestData().getConfigurationObject();

        final GprsOperationModeTypeDto gprsOperationModeType = configurationObject.getGprsOperationMode();
        final ConfigurationFlagsDto configurationFlags = configurationObject.getConfigurationFlags();

        LOGGER.info(VISUAL_SEPARATOR);
        LOGGER.info("******** Configuration Object: 0-1:94.31.3.255 *******");
        LOGGER.info(VISUAL_SEPARATOR);
        LOGGER.info("Operation mode: {}",
                gprsOperationModeType == null ? "not altered by this request" : gprsOperationModeType);
        if (configurationFlags == null) {
            LOGGER.info("Flags: none enabled or disabled by this request");
        } else {
            LOGGER.info("{}", configurationFlags);
        }
        LOGGER.info(VISUAL_SEPARATOR);

        final AccessResultCode accessResultCode = this.setConfigurationObjectCommandExecutor.execute(conn, device,
                configurationObject);
        if (!AccessResultCode.SUCCESS.equals(accessResultCode)) {
            throw new ProtocolAdapterException("Set configuration object reported result is: " + accessResultCode);
        }

    }

    public void requestSetAdministrativeStatus(final DlmsConnectionManager conn, final DlmsDevice device,
            final AdministrativeStatusTypeDto administrativeStatusType) throws ProtocolAdapterException {

        LOGGER.info("Device for Set Administrative Status is: {}", device);

        final AccessResultCode accessResultCode = this.setAdministrativeStatusCommandExecutor.execute(conn, device,
                administrativeStatusType);
        if (AccessResultCode.SUCCESS != accessResultCode) {
            throw new ProtocolAdapterException(
                    "AccessResultCode for set administrative status was not SUCCESS: " + accessResultCode);
        }
    }

    public void setAlarmNotifications(final DlmsConnectionManager conn, final DlmsDevice device,
            final AlarmNotificationsDto alarmNotifications) throws ProtocolAdapterException {

        LOGGER.info("Alarm Notifications to set on the device: {}", alarmNotifications);

        final AccessResultCode accessResultCode = this.setAlarmNotificationsCommandExecutor.execute(conn, device,
                alarmNotifications);
        if (AccessResultCode.SUCCESS != accessResultCode) {
            throw new ProtocolAdapterException(
                    "AccessResultCode for set alarm notifications was not SUCCESS: " + accessResultCode);
        }
    }

    public AdministrativeStatusTypeDto requestGetAdministrativeStatus(final DlmsConnectionManager conn,
            final DlmsDevice device) throws ProtocolAdapterException {

        return this.getAdministrativeStatusCommandExecutor.execute(conn, device, null);
    }

    public String setEncryptionKeyExchangeOnGMeter(final DlmsConnectionManager conn, final DlmsDevice device,
            final GMeterInfoDto gMeterInfo) throws ProtocolAdapterException {

        LOGGER.info("Device for Set Encryption Key Exchange On G-Meter is: {}", device);
        this.setEncryptionKeyExchangeOnGMeterCommandExecutor.execute(conn, device, gMeterInfo);
        return "Set Encryption Key Exchange On G-Meter Result is OK for device id: " + device.getDeviceIdentification();
    }

    public String setMbusUserKeyByChannel(final DlmsConnectionManager conn, final DlmsDevice device,
            final SetMbusUserKeyByChannelRequestDataDto setMbusUserKeyByChannelRequestDataDto) throws OsgpException {

        LOGGER.info("Device for Set M-Bus User Key By Channel is: {}", device);

        final GMeterInfoDto gMeterInfo = this.getMbusKeyExchangeData(conn, device,
                setMbusUserKeyByChannelRequestDataDto);

        this.setEncryptionKeyExchangeOnGMeterCommandExecutor.execute(conn, device, gMeterInfo);

        return "Set M-Bus User Key By Channel Result is OK for device id: " + device.getDeviceIdentification();
    }

    public GMeterInfoDto getMbusKeyExchangeData(final DlmsConnectionManager conn, final DlmsDevice device,
            final SetMbusUserKeyByChannelRequestDataDto setMbusUserKeyByChannelRequestData) throws OsgpException {

        final GetMBusDeviceOnChannelRequestDataDto mbusDeviceOnChannelRequest =
                new GetMBusDeviceOnChannelRequestDataDto(
                device.getDeviceIdentification(), setMbusUserKeyByChannelRequestData.getChannel());
        final ChannelElementValuesDto channelElementValues = this.getMBusDeviceOnChannelCommandExecutor.execute(conn,
                device, mbusDeviceOnChannelRequest);

        final DlmsDevice mbusDevice = this.domainHelperService.findMbusDevice(
                Long.valueOf(channelElementValues.getIdentificationNumber()),
                channelElementValues.getManufacturerIdentification());

        return new GMeterInfoDto(setMbusUserKeyByChannelRequestData.getChannel(), mbusDevice.getDeviceIdentification());
    }

    public String setActivityCalendar(final DlmsConnectionManager conn, final DlmsDevice device,
            final ActivityCalendarDto activityCalendar) throws ProtocolAdapterException {

        LOGGER.info("Device for Activity Calendar is: {}", device);

        this.setActivityCalendarCommandExecutor.execute(conn, device, activityCalendar);

        return "Set Activity Calendar Result is OK for device id: " + device.getDeviceIdentification()
                + " calendar name: " + activityCalendar.getCalendarName();

    }

    public void setPushSetupAlarm(final DlmsConnectionManager conn, final DlmsDevice device,
            final PushSetupAlarmDto pushSetupAlarm) throws ProtocolAdapterException {

        LOGGER.info("Push Setup Alarm to set on the device: {}", pushSetupAlarm);

        final AccessResultCode accessResultCode = this.setPushSetupAlarmCommandExecutor.execute(conn, device,
                pushSetupAlarm);

        if (AccessResultCode.SUCCESS != accessResultCode) {
            throw new ProtocolAdapterException(
                    "AccessResultCode for set push setup alarm was not SUCCESS: " + accessResultCode);
        }
    }

    public void setPushSetupSms(final DlmsConnectionManager conn, final DlmsDevice device,
            final PushSetupSmsDto pushSetupSms) throws ProtocolAdapterException {

        LOGGER.info("Push Setup Sms to set on the device: {}", pushSetupSms);

        final AccessResultCode accessResultCode = this.setPushSetupSmsCommandExecutor.execute(conn, device,
                pushSetupSms);

        if (AccessResultCode.SUCCESS != accessResultCode) {
            throw new ProtocolAdapterException(
                    "AccessResultCode for set push setup sms was not SUCCESS: " + accessResultCode);
        }

    }

    public List<FirmwareVersionDto> requestFirmwareVersion(final DlmsConnectionManager conn, final DlmsDevice device)
            throws ProtocolAdapterException {

        return this.getFirmwareVersionCommandExecutor.execute(conn, device, null);
    }

    public void generateAndEncrypt(final DlmsConnectionManager conn, final DlmsDevice device) throws OsgpException {
        try {

            this.generateAndReplaceKeyCommandExecutor.executeBundleAction(conn, device, null);
        } catch (final ProtocolAdapterException e) {
            LOGGER.error("Unexpected exception during replaceKeys.", e);
            throw e;
        }
    }

    public void replaceKeys(final DlmsConnectionManager conn, final DlmsDevice device, final SetKeysRequestDto keySet)
            throws OsgpException {

        try {
            /*
             * Call executeBundleAction, since it knows to deal with the
             * SetKeysRequestDto containing authentication and encryption key,
             * while execute deals with a single key only.
             */
            keySet.setGeneratedKeys(false);
            this.replaceKeyCommandExecutor.executeBundleAction(conn, device, keySet);

        } catch (final ProtocolAdapterException e) {
            LOGGER.error("Unexpected exception during replaceKeys.", e);
            throw e;
        }
    }

    public void setClockConfiguration(final DlmsConnectionManager conn, final DlmsDevice device,
            final SetClockConfigurationRequestDto clockConfiguration) throws ProtocolAdapterException {

        try {
            this.setClockConfigurationCommandExecutor.execute(conn, device, clockConfiguration);
        } catch (final ProtocolAdapterException e) {
            LOGGER.error("Unexpected exception while setting clock configuration.", e);
            throw e;
        }
    }

    public UpdateFirmwareResponseDto updateFirmware(final DlmsConnectionManager conn, final DlmsDevice device,
            final String firmwareIdentifier) throws OsgpException {
        LOGGER.info("Updating firmware of device {} to firmware with identifier {}", device, firmwareIdentifier);

        return this.firmwareService.updateFirmware(conn, device, firmwareIdentifier);
    }

    public GetConfigurationObjectResponseDto requestGetConfigurationObject(final DlmsConnectionManager conn,
            final DlmsDevice device) throws ProtocolAdapterException {

        return new GetConfigurationObjectResponseDto(
                this.getConfigurationObjectCommandExecutor.execute(conn, device, null));
    }

    public void configureDefinableLoadProfile(final DlmsConnectionManager conn, final DlmsDevice device,
            final DefinableLoadProfileConfigurationDto definableLoadProfileConfiguration)
            throws ProtocolAdapterException {
        try {
            this.configureDefinableLoadProfileCommandExecutor.execute(conn, device, definableLoadProfileConfiguration);
        } catch (final ProtocolAdapterException e) {
            LOGGER.error("Unexpected exception while configuring definable load profile.", e);
            throw e;
        }
    }

    public GetMbusEncryptionKeyStatusResponseDto requestGetMbusEncryptionKeyStatus(final DlmsConnectionManager conn,
            final DlmsDevice device, final GetMbusEncryptionKeyStatusRequestDto getMbusEncryptionKeyStatusRequest)
            throws ProtocolAdapterException {

        return this.getMbusEncryptionKeyStatusCommandExecutor.execute(conn, device, getMbusEncryptionKeyStatusRequest);
    }

    public GetMbusEncryptionKeyStatusByChannelResponseDto requestGetMbusEncryptionKeyStatusByChannel(
            final DlmsConnectionManager conn, final DlmsDevice device,
            final GetMbusEncryptionKeyStatusByChannelRequestDataDto getMbusEncryptionKeyStatusByChannelRequest)
            throws OsgpException {

        return this.getMbusEncryptionKeyStatusByChannelCommandExecutor.execute(conn, device,
                getMbusEncryptionKeyStatusByChannelRequest);
    }

    public void requestSetRandomizationSettings(DlmsConnectionManager conn, DlmsDevice device,
            SetRandomisationSettingsRequestDataDto setRandomisationSettingsRequestDataDto)
            throws ProtocolAdapterException {
        
        AccessResultCode accessResultCode = this.setRandomisationSettingsCommandExecutor.execute(conn, device,
                setRandomisationSettingsRequestDataDto);

        if (AccessResultCode.SUCCESS != accessResultCode) {
            throw new ProtocolAdapterException(
                    "AccessResultCode for set randomisation settings was not SUCCESS: " + accessResultCode);
        }

    }
}
