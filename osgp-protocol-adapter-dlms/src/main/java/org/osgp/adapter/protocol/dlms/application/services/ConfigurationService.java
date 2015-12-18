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
import org.openmuc.jdlms.LnClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.SetActivityCalendarCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetAlarmNotificationsCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetConfigurationObjectCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetSpecialDaysCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionFactory;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsDeviceMessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendar;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotifications;
import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlag;
import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlags;
import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationObject;
import com.alliander.osgp.dto.valueobjects.smartmetering.GprsOperationModeType;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetConfigurationObjectRequest;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDay;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequest;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequestData;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "dlmsConfigurationService")
public class ConfigurationService extends DlmsApplicationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private DlmsConnectionFactory dlmsConnectionFactory;

    @Autowired
    private SetSpecialDaysCommandExecutor setSpecialDaysCommandExecutor;

    @Autowired
    private SetAlarmNotificationsCommandExecutor setAlarmNotificationsCommandExecutor;

    @Autowired
    private SetConfigurationObjectCommandExecutor setConfigurationObjectCommandExecutor;

    @Autowired
    private SetActivityCalendarCommandExecutor setActivityCalendarCommandExecutor;

    // === REQUEST Special Days DATA ===

    public void requestSpecialDays(final DlmsDeviceMessageMetadata messageMetadata,
            final SpecialDaysRequest specialDaysRequest, final DeviceResponseMessageSender responseMessageSender) {

        this.logStart(LOGGER, messageMetadata, "requestSpecialDays");

        LnClientConnection conn = null;
        try {
            // The Special days towards the Smart Meter
            final SpecialDaysRequestData specialDaysRequestData = specialDaysRequest.getSpecialDaysRequestData();

            LOGGER.info("******************************************************");
            LOGGER.info("********** Set Special Days: 0-0:11.0.0.255 **********");
            LOGGER.info("******************************************************");
            final List<SpecialDay> specialDays = specialDaysRequestData.getSpecialDays();
            for (final SpecialDay specialDay : specialDays) {
                LOGGER.info("Date :{}, dayId : {} ", specialDay.getSpecialDayDate(), specialDay.getDayId());
            }
            LOGGER.info("******************************************************");

            final DlmsDevice device = this.domainHelperService
                    .findDlmsDevice(messageMetadata.getDeviceIdentification());
            conn = this.dlmsConnectionFactory.getConnection(device);

            final AccessResultCode accessResultCode = this.setSpecialDaysCommandExecutor.execute(conn, specialDays);
            if (!AccessResultCode.SUCCESS.equals(accessResultCode)) {
                throw new ProtocolAdapterException("Set special days reported result is: " + accessResultCode);
            }

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during set special days", e);
            final OsgpException ex = this.ensureOsgpException(e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    // === REQUEST Configuration Object DATA ===

    public void requestSetConfiguration(final DlmsDeviceMessageMetadata messageMetadata,
            final SetConfigurationObjectRequest setConfigurationObjectRequest,
            final DeviceResponseMessageSender responseMessageSender) {

        this.logStart(LOGGER, messageMetadata, "requestSetConfiguration");

        LnClientConnection conn = null;
        try {
            // Configuration Object towards the Smart Meter
            final ConfigurationObject configurationObject = setConfigurationObjectRequest
                    .getSetConfigurationObjectRequestData().getConfigurationObject();

            final GprsOperationModeType GprsOperationModeType = configurationObject.getGprsOperationMode();
            final ConfigurationFlags configurationFlags = configurationObject.getConfigurationFlags();

            LOGGER.info("******************************************************");
            LOGGER.info("******** Configuration Object: 0-0:94.31.3.255 *******");
            LOGGER.info("******************************************************");
            LOGGER.info("Operation mode:{} ", GprsOperationModeType.value());
            LOGGER.info("Flags:");

            for (final ConfigurationFlag configurationFlag : configurationFlags.getConfigurationFlag()) {
                LOGGER.info("Flag : {}, enabled = {}", configurationFlag.getConfigurationFlagType().toString(),
                        configurationFlag.isEnabled());
            }
            LOGGER.info("******************************************************");

            final DlmsDevice device = this.domainHelperService
                    .findDlmsDevice(messageMetadata.getDeviceIdentification());
            conn = this.dlmsConnectionFactory.getConnection(device);

            final AccessResultCode accessResultCode = this.setConfigurationObjectCommandExecutor.execute(conn,
                    configurationObject);
            if (!AccessResultCode.SUCCESS.equals(accessResultCode)) {
                throw new ProtocolAdapterException("Set configuration object reported result is: " + accessResultCode);
            }

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during set Configuration Object", e);
            final OsgpException ex = this.ensureOsgpException(e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    public void setActivityCalendar(final DlmsDeviceMessageMetadata messageMetadata,
            final ActivityCalendar activityCalendar, final DeviceResponseMessageSender responseMessageSender) {

        this.logStart(LOGGER, messageMetadata, "setActivityCalendar");

        try {
            LOGGER.info("**************************************");
            LOGGER.info("**********In protocol adapter*********");
            LOGGER.info("**************************************");
            LOGGER.info("*************0-0:13.0.0.255***********");
            LOGGER.info("**************************************");
            LOGGER.info("Activity Calendar to set on the device: {}", activityCalendar.getCalendarName());
            LOGGER.info("********** activityCalendar " + activityCalendar);

            final String deviceIdentification = messageMetadata.getDeviceIdentification();
            final DlmsDevice device = this.domainHelperService.findDlmsDevice(deviceIdentification);

            LOGGER.info("device for Activity Calendar is: {}", device);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender,
                    "Set Activity Calendar Result is OK for device id: " + deviceIdentification + " calendar name: "
                            + activityCalendar.getCalendarName());

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during setActivityCalendar", e);
            final OsgpException ex = this.ensureOsgpException(e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
        }
    }

    public void setAlarmNotifications(final DlmsDeviceMessageMetadata messageMetadata,
            final AlarmNotifications alarmNotifications, final DeviceResponseMessageSender responseMessageSender) {

        this.logStart(LOGGER, messageMetadata, "setAlarmNotifications");

        LnClientConnection conn = null;
        try {

            LOGGER.info("Alarm Notifications to set on the device: {}", alarmNotifications);

            final DlmsDevice device = this.domainHelperService
                    .findDlmsDevice(messageMetadata.getDeviceIdentification());

            conn = this.dlmsConnectionFactory.getConnection(device);

            final AccessResultCode accessResultCode = this.setAlarmNotificationsCommandExecutor.execute(conn,
                    alarmNotifications);
            if (AccessResultCode.SUCCESS != accessResultCode) {
                throw new ProtocolAdapterException("AccessResultCode for set alarm notifications was not SUCCESS: "
                        + accessResultCode);
            }

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during setAlarmNotifications", e);
            final OsgpException ex = this.ensureOsgpException(e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

}
