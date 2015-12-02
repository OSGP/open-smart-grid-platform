/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.SetActivityCalendarCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetAlarmNotificationsCommandExecutor;
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
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "dlmsConfigurationService")
public class ConfigurationService extends DlmsApplicationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private DlmsConnectionFactory dlmsConnectionFactory;

    @Autowired
    private SetAlarmNotificationsCommandExecutor setAlarmNotificationsCommandExecutor;

    @Autowired
    private SetActivityCalendarCommandExecutor setActicityCalendarCommandExecutor;

    private static final String LOG_SEPERATOR = "******************************************************";

    // === REQUEST Special Days DATA ===

    public void requestSpecialDays(final DlmsDeviceMessageMetadata messageMetadata,
            final SpecialDaysRequest specialDaysRequest, final DeviceResponseMessageSender responseMessageSender) {

        this.logStart(LOGGER, messageMetadata, "requestSpecialDays");

        try {
            // The Special days towards the Smart Meter
            final SpecialDaysRequestData specialDaysRequestData = specialDaysRequest.getSpecialDaysRequestData();

            LOGGER.info("SpecialDaysRequest : {}", specialDaysRequest.getSpecialDaysRequestData());
            for (final SpecialDay specialDay : specialDaysRequestData.getSpecialDays()) {
                LOGGER.info(LOG_SEPERATOR);
                LOGGER.info("Special Day date :{} ", specialDay.getSpecialDayDate());
                LOGGER.info("Special Day dayId :{} ", specialDay.getDayId());
                LOGGER.info(LOG_SEPERATOR);
            }

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during set special days", e);
            final TechnicalException ex = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception during set special days", e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
        }
    }

    // === REQUEST Configuration Object DATA ===

    public void requestSetConfiguration(final DlmsDeviceMessageMetadata messageMetadata,
            final SetConfigurationObjectRequest setConfigurationObjectRequest,
            final DeviceResponseMessageSender responseMessageSender) {

        this.logStart(LOGGER, messageMetadata, "requestSetConfiguration");

        try {
            // Configuration Object towards the Smart Meter
            final ConfigurationObject configurationObject = setConfigurationObjectRequest
                    .getSetConfigurationObjectRequestData().getConfigurationObject();

            final GprsOperationModeType gprsOperationModeType = configurationObject.getGprsOperationMode();
            final ConfigurationFlags configurationFlags = configurationObject.getConfigurationFlags();

            LOGGER.info(LOG_SEPERATOR);
            LOGGER.info("Configuration Object   ******************************");
            LOGGER.info(LOG_SEPERATOR);
            LOGGER.info("Configuration Object operation mode:{} ", gprsOperationModeType.value());
            LOGGER.info(LOG_SEPERATOR);
            LOGGER.info("Flags:   ********************************************");

            for (final ConfigurationFlag configurationFlag : configurationFlags.getConfigurationFlag()) {
                LOGGER.info("Configuration Object configuration flag :{} ", configurationFlag
                        .getConfigurationFlagType().toString());
                LOGGER.info("Configuration Object configuration flag enabled:{} ", configurationFlag.isEnabled());
                LOGGER.info(LOG_SEPERATOR);
            }

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during set Configuration Object", e);
            final TechnicalException ex = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception during set Configuration Object", e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
        }
    }

    public void setActivityCalendar(final DlmsDeviceMessageMetadata messageMetadata,
            final ActivityCalendar activityCalendar, final DeviceResponseMessageSender responseMessageSender) {

        this.logStart(LOGGER, messageMetadata, "setActivityCalendar");

        try {
            LOGGER.info(LOG_SEPERATOR);
            LOGGER.info("*******************In protocol adapter****************");
            LOGGER.info(LOG_SEPERATOR);
            LOGGER.info("**********************0-0:13.0.0.255******************");
            LOGGER.info(LOG_SEPERATOR);
            LOGGER.info("Activity Calendar to set on the device: {}", activityCalendar.getCalendarName());
            LOGGER.info("********************* activityCalendar " + activityCalendar);

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

        try {

            LOGGER.info("Alarm Notifications to set on the device: {}", alarmNotifications);

            final DlmsDevice device = this.domainHelperService
                    .findDlmsDevice(messageMetadata.getDeviceIdentification());

            final ClientConnection conn = this.dlmsConnectionFactory.getConnection(device);

            try {
                final AccessResultCode accessResultCode = this.setAlarmNotificationsCommandExecutor.execute(conn,
                        alarmNotifications);
                if (AccessResultCode.SUCCESS != accessResultCode) {
                    throw new ProtocolAdapterException("AccessResultCode for set alarm notifications was not SUCCESS: "
                            + accessResultCode);
                }
            } finally {
                if (conn != null && conn.isConnected()) {
                    conn.close();
                }
            }

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during setAlarmNotifications", e);
            final OsgpException ex = this.ensureOsgpException(e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
        }
    }

}
