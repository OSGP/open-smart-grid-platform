/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import java.util.List;

import org.openmuc.jdlms.AccessResultCode;
import org.osgp.adapter.protocol.dlms.application.models.ProtocolMeterInfo;
import org.osgp.adapter.protocol.dlms.domain.commands.GetAdministrativeStatusCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetFirmwareVersionsCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.ReplaceKeyCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetActivityCalendarCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetAdministrativeStatusCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetAlarmNotificationsCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetClockConfigurationCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetConfigurationObjectCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetEncryptionKeyExchangeOnGMeterCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetPushSetupAlarmCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetPushSetupSmsCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetSpecialDaysCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.UpdateFirmwareCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.FirmwareVersionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendarDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotificationsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlagsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GMeterInfoDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GprsOperationModeTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupAlarmDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupSmsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetClockConfigurationRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetConfigurationObjectRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetKeysRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDayDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequestDto;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

@Service(value = "dlmsConfigurationService")
public class ConfigurationService {
    private static final String VISUAL_SEPARATOR = "******************************************************";

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);

    @Autowired
    private DomainHelperService domainHelperService;

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
    private SetAdministrativeStatusCommandExecutor setAdministrativeStatusCommandExecutor;

    @Autowired
    private GetAdministrativeStatusCommandExecutor getAdministrativeStatusCommandExecutor;

    @Autowired
    private GetFirmwareVersionsCommandExecutor getFirmwareVersionCommandExecutor;

    @Autowired
    private ReplaceKeyCommandExecutor replaceKeyCommandExecutor;

    @Autowired
    private UpdateFirmwareCommandExecutor updateFirmwareCommandExecutor;

    @Autowired
    private SetClockConfigurationCommandExecutor clockConfigurationCommandExecutor;

    public void setSpecialDays(final DlmsConnectionHolder conn, final DlmsDevice device,
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

    public void requestSetConfiguration(final DlmsConnectionHolder conn, final DlmsDevice device,
            final SetConfigurationObjectRequestDto setConfigurationObjectRequest) throws ProtocolAdapterException {

        // Configuration Object towards the Smart Meter
        final ConfigurationObjectDto configurationObject = setConfigurationObjectRequest
                .getSetConfigurationObjectRequestData().getConfigurationObject();

        final GprsOperationModeTypeDto gprsOperationModeType = configurationObject.getGprsOperationMode();
        final ConfigurationFlagsDto configurationFlags = configurationObject.getConfigurationFlags();

        LOGGER.info(VISUAL_SEPARATOR);
        LOGGER.info("******** Configuration Object: 0-1:94.31.3.255 *******");
        LOGGER.info(VISUAL_SEPARATOR);
        LOGGER.info("Operation mode:{} ", gprsOperationModeType.name());
        LOGGER.info("Flags:");
        for (final ConfigurationFlagDto configurationFlag : configurationFlags.getConfigurationFlag()) {
            LOGGER.info("Flag : {}, enabled = {}", configurationFlag.getConfigurationFlagType().toString(),
                    configurationFlag.isEnabled());
        }
        LOGGER.info(VISUAL_SEPARATOR);

        final AccessResultCode accessResultCode = this.setConfigurationObjectCommandExecutor.execute(conn, device,
                configurationObject);
        if (!AccessResultCode.SUCCESS.equals(accessResultCode)) {
            throw new ProtocolAdapterException("Set configuration object reported result is: " + accessResultCode);
        }

    }

    public void requestSetAdministrativeStatus(final DlmsConnectionHolder conn, final DlmsDevice device,
            final AdministrativeStatusTypeDto administrativeStatusType) throws ProtocolAdapterException {

        LOGGER.info("Device for Set Administrative Status is: {}", device);

        final AccessResultCode accessResultCode = this.setAdministrativeStatusCommandExecutor.execute(conn, device,
                administrativeStatusType);
        if (AccessResultCode.SUCCESS != accessResultCode) {
            throw new ProtocolAdapterException(
                    "AccessResultCode for set administrative status was not SUCCESS: " + accessResultCode);
        }
    }

    public void setAlarmNotifications(final DlmsConnectionHolder conn, final DlmsDevice device,
            final AlarmNotificationsDto alarmNotifications) throws ProtocolAdapterException {

        LOGGER.info("Alarm Notifications to set on the device: {}", alarmNotifications);

        final AccessResultCode accessResultCode = this.setAlarmNotificationsCommandExecutor.execute(conn, device,
                alarmNotifications);
        if (AccessResultCode.SUCCESS != accessResultCode) {
            throw new ProtocolAdapterException(
                    "AccessResultCode for set alarm notifications was not SUCCESS: " + accessResultCode);
        }
    }

    public AdministrativeStatusTypeDto requestGetAdministrativeStatus(final DlmsConnectionHolder conn,
            final DlmsDevice device) throws ProtocolAdapterException {

        return this.getAdministrativeStatusCommandExecutor.execute(conn, device, null);
    }

    public String setEncryptionKeyExchangeOnGMeter(final DlmsConnectionHolder conn, final DlmsDevice device,
            final GMeterInfoDto gMeterInfo) throws ProtocolAdapterException {

        LOGGER.info("Device for Set Encryption Key Exchange On G-Meter is: {}", device);

        // Get G-Meter
        DlmsDevice gMeterDevice;
        try {
            gMeterDevice = this.domainHelperService.findDlmsDevice(gMeterInfo.getDeviceIdentification());
        } catch (final FunctionalException e) {
            throw new ProtocolAdapterException("Error while looking up G-Meter", e);
        }
        final ProtocolMeterInfo protocolMeterInfo = new ProtocolMeterInfo(gMeterInfo.getChannel(),
                gMeterInfo.getDeviceIdentification(),
                gMeterDevice.getValidSecurityKey(SecurityKeyType.G_METER_ENCRYPTION).getKey(),
                gMeterDevice.getValidSecurityKey(SecurityKeyType.G_METER_MASTER).getKey());

        this.setEncryptionKeyExchangeOnGMeterCommandExecutor.execute(conn, device, protocolMeterInfo);

        return "Set Encryption Key Exchange On G-Meter Result is OK for device id: " + device.getDeviceIdentification();
    }

    public String setActivityCalendar(final DlmsConnectionHolder conn, final DlmsDevice device,
            final ActivityCalendarDto activityCalendar) throws ProtocolAdapterException {

        LOGGER.info("Device for Activity Calendar is: {}", device);

        this.setActivityCalendarCommandExecutor.execute(conn, device, activityCalendar);

        return "Set Activity Calendar Result is OK for device id: " + device.getDeviceIdentification()
                + " calendar name: " + activityCalendar.getCalendarName();

    }

    public void setPushSetupAlarm(final DlmsConnectionHolder conn, final DlmsDevice device,
            final PushSetupAlarmDto pushSetupAlarm) throws ProtocolAdapterException {

        LOGGER.info("Push Setup Alarm to set on the device: {}", pushSetupAlarm);

        final AccessResultCode accessResultCode = this.setPushSetupAlarmCommandExecutor.execute(conn, device,
                pushSetupAlarm);

        if (AccessResultCode.SUCCESS != accessResultCode) {
            throw new ProtocolAdapterException(
                    "AccessResultCode for set push setup alarm was not SUCCESS: " + accessResultCode);
        }
    }

    public void setPushSetupSms(final DlmsConnectionHolder conn, final DlmsDevice device,
            final PushSetupSmsDto pushSetupSms) throws ProtocolAdapterException {

        LOGGER.info("Push Setup Sms to set on the device: {}", pushSetupSms);

        final AccessResultCode accessResultCode = this.setPushSetupSmsCommandExecutor.execute(conn, device,
                pushSetupSms);

        if (AccessResultCode.SUCCESS != accessResultCode) {
            throw new ProtocolAdapterException(
                    "AccessResultCode for set push setup sms was not SUCCESS: " + accessResultCode);
        }

    }

    public List<FirmwareVersionDto> requestFirmwareVersion(final DlmsConnectionHolder conn, final DlmsDevice device)
            throws ProtocolAdapterException {

        return this.getFirmwareVersionCommandExecutor.execute(conn, device, null);
    }

    public void replaceKeys(final DlmsConnectionHolder conn, final DlmsDevice device, final SetKeysRequestDto keySet)
            throws ProtocolAdapterException {

        try {
            /*
             * Call executeBundleAction, since it knows to deal with the
             * SetKeysRequestDto containing authentication and encryption key,
             * while execute deals with a single key only.
             */
            this.replaceKeyCommandExecutor.executeBundleAction(conn, device, keySet);
        } catch (final ProtocolAdapterException e) {
            LOGGER.error("Unexpected exception during replaceKeys.", e);
            throw e;
        }
    }

    public void setClockConfiguration(final DlmsConnectionHolder conn, final DlmsDevice device,
            final SetClockConfigurationRequestDto clockConfiguration) throws ProtocolAdapterException {

        try {
            this.clockConfigurationCommandExecutor.execute(conn, device, clockConfiguration);
        } catch (final ProtocolAdapterException e) {
            LOGGER.error("Unexpected exception during replaceKeys.", e);
            throw e;
        }
    }

    public List<FirmwareVersionDto> updateFirmware(final DlmsConnectionHolder conn, final DlmsDevice device,
            final String firmwareIdentifier) throws ProtocolAdapterException {
        LOGGER.info("Updating firmware of device {} to firmware with identifier {}", device, firmwareIdentifier);

        return this.updateFirmwareCommandExecutor.execute(conn, device, firmwareIdentifier);
    }

}
