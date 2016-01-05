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
import org.openmuc.jdlms.MethodResultCode;
import org.osgp.adapter.protocol.dlms.domain.commands.DlmsHelperService;
import org.osgp.adapter.protocol.dlms.domain.commands.GetAdministrativeStatusCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetActivityCalendarCommandActivationExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetActivityCalendarCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetAdministrativeStatusCommandExecutor;
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
import com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusType;
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
    private DlmsHelperService dlmsHelperService;

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

    @Autowired
    private SetActivityCalendarCommandActivationExecutor setActivityCalendarCommandActivationExecutor;

    @Autowired
    private SetAdministrativeStatusCommandExecutor setAdministrativeStatusCommandExecutor;

    @Autowired
    private GetAdministrativeStatusCommandExecutor getAdministrativeStatusCommandExecutor;

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

    public void requestSetAdministrativeStatus(final DlmsDeviceMessageMetadata messageMetadata,
            final AdministrativeStatusType administrativeStatusType,
            final DeviceResponseMessageSender responseMessageSender) {

        this.logStart(LOGGER, messageMetadata, "requestSetAdministration");

        LnClientConnection conn = null;
        DlmsDevice device = null;
        try {
            final String deviceIdentification = messageMetadata.getDeviceIdentification();
            device = this.domainHelperService.findDlmsDevice(deviceIdentification);

            LOGGER.info("Device for Set Administrative Status is: {}", device);

            conn = this.dlmsConnectionFactory.getConnection(device);
            this.setAdministrativeStatusCommandExecutor.execute(conn, administrativeStatusType);

            final AccessResultCode accessResultCode = this.setAdministrativeStatusCommandExecutor.execute(conn,
                    administrativeStatusType);
            if (AccessResultCode.SUCCESS != accessResultCode) {
                throw new ProtocolAdapterException("AccessResultCode for set administrative status was not SUCCESS: "
                        + accessResultCode);
            }

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during setAdministrativeStatus", e);
            final OsgpException ex = this.ensureOsgpException(e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
        } finally {
            if (conn != null) {
                LOGGER.info("Closing connection with {}", device.getDeviceIdentification());
                conn.close();
            }
        }

    }

    public void requestGetAdministrativeStatus(final DlmsDeviceMessageMetadata messageMetadata,
            final DeviceResponseMessageSender responseMessageSender) {

        this.logStart(LOGGER, messageMetadata, "requestGetAdministrativeStatus");

        LnClientConnection conn = null;
        try {
            final DlmsDevice device = this.domainHelperService
                    .findDlmsDevice(messageMetadata.getDeviceIdentification());

            conn = this.dlmsConnectionFactory.getConnection(device);

            final AdministrativeStatusType administrativeStatusType = this.getAdministrativeStatusCommandExecutor
                    .execute(conn, null);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender,
                    administrativeStatusType);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during getAdministrativeStatus", e);
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

        LnClientConnection conn = null;
        DlmsDevice device = null;
        try {
            final String deviceIdentification = messageMetadata.getDeviceIdentification();
            device = this.domainHelperService.findDlmsDevice(deviceIdentification);

            LOGGER.info("Device for Activity Calendar is: {}", device);

            conn = this.dlmsConnectionFactory.getConnection(device);
            this.setActivityCalendarCommandExecutor.execute(conn, activityCalendar);

            final MethodResultCode methodResult = this.setActivityCalendarCommandActivationExecutor.execute(conn, null);

            if (!MethodResultCode.SUCCESS.equals(methodResult)) {
                throw new ProtocolAdapterException("AccessResultCode for set Activity Calendar: " + methodResult);
            }

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender,
                    "Set Activity Calendar Result is OK for device id: " + deviceIdentification + " calendar name: "
                            + activityCalendar.getCalendarName());

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during setActivityCalendar", e);
            final OsgpException ex = this.ensureOsgpException(e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
        } finally {
            if (conn != null) {
                LOGGER.info("Closing connection with {}", device.getDeviceIdentification());
                conn.close();
            }
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
