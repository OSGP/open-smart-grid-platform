/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.SecurityUtils.KeyId;
import org.osgp.adapter.protocol.dlms.domain.commands.GetAdministrativeStatusCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetFirmwareVersionCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.ReplaceKeyCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetActivityCalendarCommandActivationExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetActivityCalendarCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetAdministrativeStatusCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetAlarmNotificationsCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetConfigurationObjectCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetPushSetupAlarmCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.SetSpecialDaysCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionFactory;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
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
import com.alliander.osgp.dto.valueobjects.smartmetering.KeySet;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupAlarm;
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
    private SetPushSetupAlarmCommandExecutor setPushSetupAlarmCommandExecutor;

    @Autowired
    private SetActivityCalendarCommandExecutor setActivityCalendarCommandExecutor;

    @Autowired
    private SetActivityCalendarCommandActivationExecutor setActivityCalendarCommandActivationExecutor;

    @Autowired
    private SetAdministrativeStatusCommandExecutor setAdministrativeStatusCommandExecutor;

    @Autowired
    private GetAdministrativeStatusCommandExecutor getAdministrativeStatusCommandExecutor;

    @Autowired
    private GetFirmwareVersionCommandExecutor getFirmwareVersionCommandExecutor;

    @Autowired
    private ReplaceKeyCommandExecutor replaceKeyCommandExecutor;

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

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

            final DlmsDevice device = this.domainHelperService.findDlmsDevice(messageMetadata);
            conn = this.dlmsConnectionFactory.getConnection(device);

            final AccessResultCode accessResultCode = this.setSpecialDaysCommandExecutor.execute(conn, device,
                    specialDays);
            if (!AccessResultCode.SUCCESS.equals(accessResultCode)) {
                throw new ProtocolAdapterException("Set special days reported result is: " + accessResultCode);
            }

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during set special days", e);
            final OsgpException ex = this.ensureOsgpException(e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender,
                    specialDaysRequest);
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

            final GprsOperationModeType gprsOperationModeType = configurationObject.getGprsOperationMode();
            final ConfigurationFlags configurationFlags = configurationObject.getConfigurationFlags();

            LOGGER.info("******************************************************");
            LOGGER.info("******** Configuration Object: 0-0:94.31.3.255 *******");
            LOGGER.info("******************************************************");
            LOGGER.info("Operation mode:{} ", gprsOperationModeType.value());
            LOGGER.info("Flags:");

            for (final ConfigurationFlag configurationFlag : configurationFlags.getConfigurationFlag()) {
                LOGGER.info("Flag : {}, enabled = {}", configurationFlag.getConfigurationFlagType().toString(),
                        configurationFlag.isEnabled());
            }
            LOGGER.info("******************************************************");

            final DlmsDevice device = this.domainHelperService.findDlmsDevice(messageMetadata);
            conn = this.dlmsConnectionFactory.getConnection(device);

            final AccessResultCode accessResultCode = this.setConfigurationObjectCommandExecutor.execute(conn, device,
                    configurationObject);
            if (!AccessResultCode.SUCCESS.equals(accessResultCode)) {
                throw new ProtocolAdapterException("Set configuration object reported result is: " + accessResultCode);
            }

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during set Configuration Object", e);
            final OsgpException ex = this.ensureOsgpException(e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender,
                    setConfigurationObjectRequest);
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
            device = this.domainHelperService.findDlmsDevice(messageMetadata);

            LOGGER.info("Device for Set Administrative Status is: {}", device);

            conn = this.dlmsConnectionFactory.getConnection(device);
            this.setAdministrativeStatusCommandExecutor.execute(conn, device, administrativeStatusType);

            final AccessResultCode accessResultCode = this.setAdministrativeStatusCommandExecutor.execute(conn, device,
                    administrativeStatusType);
            if (AccessResultCode.SUCCESS != accessResultCode) {
                throw new ProtocolAdapterException("AccessResultCode for set administrative status was not SUCCESS: "
                        + accessResultCode);
            }

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during setAdministrativeStatus", e);
            final OsgpException ex = this.ensureOsgpException(e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender,
                    administrativeStatusType);
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

            final DlmsDevice device = this.domainHelperService.findDlmsDevice(messageMetadata);

            conn = this.dlmsConnectionFactory.getConnection(device);

            final AccessResultCode accessResultCode = this.setAlarmNotificationsCommandExecutor.execute(conn, device,
                    alarmNotifications);
            if (AccessResultCode.SUCCESS != accessResultCode) {
                throw new ProtocolAdapterException("AccessResultCode for set alarm notifications was not SUCCESS: "
                        + accessResultCode);
            }

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during setAlarmNotifications", e);
            final OsgpException ex = this.ensureOsgpException(e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender,
                    alarmNotifications);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    public void requestGetAdministrativeStatus(final DlmsDeviceMessageMetadata messageMetadata,
            final DeviceResponseMessageSender responseMessageSender) {

        this.logStart(LOGGER, messageMetadata, "requestGetAdministrativeStatus");

        LnClientConnection conn = null;
        try {
            final DlmsDevice device = this.domainHelperService.findDlmsDevice(messageMetadata);

            conn = this.dlmsConnectionFactory.getConnection(device);

            final AdministrativeStatusType administrativeStatusType = this.getAdministrativeStatusCommandExecutor
                    .execute(conn, device, null);

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
            device = this.domainHelperService.findDlmsDevice(messageMetadata);

            LOGGER.info("Device for Activity Calendar is: {}", device);

            conn = this.dlmsConnectionFactory.getConnection(device);
            this.setActivityCalendarCommandExecutor.execute(conn, device, activityCalendar);

            final MethodResultCode methodResult = this.setActivityCalendarCommandActivationExecutor.execute(conn,
                    device, null);

            if (!MethodResultCode.SUCCESS.equals(methodResult)) {
                throw new ProtocolAdapterException("AccessResultCode for set Activity Calendar: " + methodResult);
            }

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender,
                    "Set Activity Calendar Result is OK for device id: " + deviceIdentification + " calendar name: "
                            + activityCalendar.getCalendarName());

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during setActivityCalendar", e);
            final OsgpException ex = this.ensureOsgpException(e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender,
                    activityCalendar);
        } finally {
            if (conn != null) {
                LOGGER.info("Closing connection with {}", device.getDeviceIdentification());
                conn.close();
            }
        }

    }

    public void setPushSetupAlarm(final DlmsDeviceMessageMetadata messageMetadata, final PushSetupAlarm pushSetupAlarm,
            final DeviceResponseMessageSender responseMessageSender) {

        this.logStart(LOGGER, messageMetadata, "setPushSetupAlarm");

        LnClientConnection conn = null;
        try {

            LOGGER.info("Push Setup Alarm to set on the device: {}", pushSetupAlarm);

            final DlmsDevice device = this.domainHelperService.findDlmsDevice(messageMetadata);

            conn = this.dlmsConnectionFactory.getConnection(device);

            final AccessResultCode accessResultCode = this.setPushSetupAlarmCommandExecutor.execute(conn, device,
                    pushSetupAlarm);

            if (AccessResultCode.SUCCESS != accessResultCode) {
                throw new ProtocolAdapterException("AccessResultCode for set push setup alarm was not SUCCESS: "
                        + accessResultCode);
            }

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during setPushSetupAlarm", e);
            final OsgpException ex = this.ensureOsgpException(e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender,
                    pushSetupAlarm);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    public void requestFirmwareVersion(final DlmsDeviceMessageMetadata messageMetadata,
            final DeviceResponseMessageSender responseMessageSender) {

        this.logStart(LOGGER, messageMetadata, "requestFirmwareVersion");

        LnClientConnection conn = null;
        try {
            final DlmsDevice device = this.domainHelperService.findDlmsDevice(messageMetadata);
            conn = this.dlmsConnectionFactory.getConnection(device);

            final String firmwareVersion = this.getFirmwareVersionCommandExecutor.execute(conn, device, null);

            // Send placeholder version number
            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender,
                    firmwareVersion);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during requestFirmwareVersion", e);
            final OsgpException ex = this.ensureOsgpException(e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    public void replaceKeys(final DlmsDeviceMessageMetadata messageMetadata, final KeySet keySet,
            final DeviceResponseMessageSender responseMessageSender) {

        this.logStart(LOGGER, messageMetadata, "replaceKeys");

        final LnClientConnection conn = null;

        try {
            final DlmsDevice device = this.domainHelperService.findDlmsDevice(messageMetadata);
            this.replaceKeySet(conn, device, keySet);
            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender);
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during replace keys", e);
            final OsgpException ex = this.ensureOsgpException(e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender,
                    keySet);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    private void replaceKeySet(final LnClientConnection conn, final DlmsDevice device, final KeySet keySet)
            throws ProtocolAdapterException {
        try {
            LOGGER.info("Keys to set on the device {}: {}", device.getDeviceIdentification(), keySet);
            this.executeReplaceKey(device, conn, keySet.getAuthenticationKey(), SecurityKeyType.E_METER_AUTHENTICATION,
                    KeyId.AUTHENTICATION_KEY);
            this.executeReplaceKey(device, conn, keySet.getEncryptionKey(), SecurityKeyType.E_METER_ENCRYPTION,
                    KeyId.GLOBAL_UNICAST_ENCRYPTION_KEY);
        } finally {
            // Store keys even when an exception was thrown, to be able to
            // retrieve key status in case the key was set on the device.
            this.dlmsDeviceRepository.save(device);
        }
    }

    /**
     * Replace a key on the meter.
     *
     *
     * @param device
     *            Device entity.
     * @param conn
     *            Connection with the meter.
     * @param key
     *            Ket value to be set.
     * @param securityKeyType
     *            type op the key.
     * @param keyId
     *            type of the key in jDLMS terms.
     * @throws ProtocolAdapterException
     *             when anything goes wrong a ProtocolAdapterException is
     *             thrown.
     */
    private void executeReplaceKey(final DlmsDevice device, final LnClientConnection conn, final byte[] key,
            final SecurityKeyType securityKeyType, final KeyId keyId) throws ProtocolAdapterException {

        try {
            // Add the new key and store in the repo
            final SecurityKey newKey = new SecurityKey(device, securityKeyType, Hex.encodeHexString(key), null, null);
            device.addSecurityKey(newKey);

            /**
             * Waiting for update of jDLMS library.
             */
            // // Send the key to the device.
            // final MethodResultCode methodResultCode =
            // this.replaceKeyCommandExecutor.execute(conn, device,
            // ReplaceKeyCommandExecutor.wrap(key, keyId));
            // if (!MethodResultCode.SUCCESS.equals(methodResultCode)) {
            // throw new
            // ProtocolAdapterException("AccessResultCode for replace keys was not SUCCESS: "
            // + methodResultCode);
            // }

            final Date now = new Date();
            final SecurityKey oldKey = device.getValidSecurityKey(securityKeyType);
            oldKey.setValidTo(now);
            newKey.setValidFrom(now);
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during replaceKeys.", e);
            throw new ProtocolAdapterException(e.getMessage());
        }
    }
}
