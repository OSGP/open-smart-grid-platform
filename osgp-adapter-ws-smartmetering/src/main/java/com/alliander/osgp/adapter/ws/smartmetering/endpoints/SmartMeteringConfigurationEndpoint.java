/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.endpoints;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.alliander.osgp.adapter.ws.endpointinterceptors.MessagePriority;
import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.endpointinterceptors.ScheduleTime;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.AsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.FirmwareVersion;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ReplaceKeysAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ReplaceKeysAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ReplaceKeysRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ReplaceKeysResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarRequestData;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsRequestData;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupAlarmAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupAlarmAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupAlarmRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupAlarmRequestData;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupAlarmResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsRequestData;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareRequest;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.ConfigurationService;
import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.MeterResponseData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActivityCalendar;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotifications;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.FirmwareVersionResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.UpdateFirmwareResponse;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.wsheaderattribute.priority.MessagePriorityEnum;

@Endpoint
public class SmartMeteringConfigurationEndpoint extends SmartMeteringEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringConfigurationEndpoint.class);
    private static final String SMARTMETER_CONFIGURATION_NAMESPACE = "http://www.alliander.com/schemas/osgp/smartmetering/sm-configuration/2014/10";

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ConfigurationMapper configurationMapper;

    public SmartMeteringConfigurationEndpoint() {
        // Default constructor
    }

    /**
     *
     * Starts the proces of retrieving the firmware version(s) of the device
     * specified in the {@link GetFirmwareVersionRequest}
     *
     * @param organisationIdentification
     *            {@link String} containing the identification of the
     *            organization
     * @param request
     *            the {@link GetFirmwareVersionRequest}
     * @param messagePriority
     *            the message priority
     * @param scheduleTime
     *            the time the message is scheduled
     * @return the {@link GetFirmwareVersionAsyncResponse} containing the
     *         correlation id
     * @throws OsgpException
     */
    @PayloadRoot(localPart = "GetFirmwareVersionRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public GetFirmwareVersionAsyncResponse getFirmwareVersion(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetFirmwareVersionRequest request, @MessagePriority final String messagePriority,
            @ScheduleTime final String scheduleTime) throws OsgpException {

        LOGGER.info("GetFirmwareVersion Request received from organisation {} for device {}.",
                organisationIdentification, request.getDeviceIdentification());

        final GetFirmwareVersionAsyncResponse response = new GetFirmwareVersionAsyncResponse();

        try {
            final String correlationUid = this.configurationService.enqueueGetFirmwareRequest(
                    organisationIdentification, request.getDeviceIdentification(),
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.configurationMapper.map(scheduleTime, Long.class));
            response.setCorrelationUid(correlationUid);
            response.setDeviceIdentification(request.getDeviceIdentification());
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    /**
     * Gets the Firmware version response from the database (if it is there) and
     * returns {@link GetFirmwareVersionResponse} containing those firmware
     * versions.
     *
     * @param organisationIdentification
     *            {@link String} containing the identification of the
     *            organization
     * @param request
     *            {@link GetFirmwareVersionAsyncRequest} containing the
     *            correlation id as the response identifier
     * @return {@link GetFirmwareVersionResponse} containing the firmware
     *         version(s) for the device.
     * @throws OsgpException
     *             is thrown when the correlationId cannot be found in the
     *             database
     */
    @PayloadRoot(localPart = "GetFirmwareVersionAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public GetFirmwareVersionResponse getGetFirmwareVersionResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetFirmwareVersionAsyncRequest request) throws OsgpException {

        LOGGER.info("GetFirmwareVersionResponse Request received from organisation {} for device: {}.",
                organisationIdentification, request.getDeviceIdentification());

        final GetFirmwareVersionResponse response = new GetFirmwareVersionResponse();

        try {
            final MeterResponseData meterResponseData = this.configurationService.dequeueGetFirmwareResponse(request
                    .getCorrelationUid());
            if (meterResponseData != null) {
                response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));

                if (meterResponseData.getMessageData() != null) {
                    final List<FirmwareVersion> target = response.getFirmwareVersion();
                    final FirmwareVersionResponse firmwareVersionResponse = (FirmwareVersionResponse) meterResponseData
                            .getMessageData();
                    target.addAll(this.configurationMapper.mapAsList(firmwareVersionResponse.getFirmwareVersions(),
                            FirmwareVersion.class));
                } else {
                    LOGGER.info("Get Firmware Version firmware is null");
                }
            }
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "UpdateFirmwareRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public UpdateFirmwareAsyncResponse updateFirmware(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final UpdateFirmwareRequest request, @MessagePriority final String messagePriority,
            @ScheduleTime final String scheduleTime) throws OsgpException {

        LOGGER.info("UpdateFirmware Request received from organisation {} for device {}.", organisationIdentification,
                request.getDeviceIdentification());

        final UpdateFirmwareAsyncResponse response = new UpdateFirmwareAsyncResponse();

        try {
            final String correlationUid = this.configurationService.enqueueUpdateFirmwareRequest(
                    organisationIdentification, request.getDeviceIdentification(), request.getFirmwareIdentification(),
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.configurationMapper.map(scheduleTime, Long.class));

            response.setCorrelationUid(correlationUid);
            response.setDeviceIdentification(request.getDeviceIdentification());

        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "UpdateFirmwareAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareResponse getUpdateFirmwareResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final UpdateFirmwareAsyncRequest request) throws OsgpException {

        LOGGER.info("GetUpdateFirmwareResponse Request received from organisation {} for device: {}.",
                organisationIdentification, request.getDeviceIdentification());

        final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareResponse response = new com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareResponse();

        try {
            final MeterResponseData meterResponseData = this.configurationService.dequeueUpdateFirmwareResponse(request
                    .getCorrelationUid());

            this.throwExceptionIfResultNotOk(meterResponseData, "updating firmware");

            if (meterResponseData != null) {
                response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));

                if (meterResponseData.getMessageData() != null) {
                    final List<FirmwareVersion> target = response.getFirmwareVersion();
                    final UpdateFirmwareResponse updateFirmwareResponse = (UpdateFirmwareResponse) meterResponseData
                            .getMessageData();
                    target.addAll(this.configurationMapper.mapAsList(updateFirmwareResponse.getFirmwareVersions(),
                            FirmwareVersion.class));
                } else {
                    LOGGER.info("Update Firmware response is null");
                }
            }
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "SetAdministrativeStatusRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetAdministrativeStatusAsyncResponse setAdministrativeStatus(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetAdministrativeStatusRequest request,
            @MessagePriority final String messagePriority, @ScheduleTime final String scheduleTime)
            throws OsgpException {

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusType dataRequest = this.configurationMapper
                .map(request.getEnabled(),
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusType.class);

        final String correlationUid = this.configurationService.requestSetAdministrativeStatus(
                organisationIdentification, request.getDeviceIdentification(), dataRequest,
                MessagePriorityEnum.getMessagePriority(messagePriority),
                this.configurationMapper.map(scheduleTime, Long.class));

        final SetAdministrativeStatusAsyncResponse asyncResponse = new com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ObjectFactory()
                .createSetAdministrativeStatusAsyncResponse();
        asyncResponse.setCorrelationUid(correlationUid);
        asyncResponse.setDeviceIdentification(request.getDeviceIdentification());

        return asyncResponse;
    }

    @PayloadRoot(localPart = "SetAdministrativeStatusAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetAdministrativeStatusResponse retrieveSetAdministrativeStatusResponse(
            @RequestPayload final SetAdministrativeStatusAsyncRequest request) throws OsgpException {

        SetAdministrativeStatusResponse response = null;
        try {
            response = new SetAdministrativeStatusResponse();
            final MeterResponseData meterResponseData = this.configurationService
                    .dequeueSetAdministrativeStatusResponse(request.getCorrelationUid());

            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            if (meterResponseData.getMessageData() instanceof String) {
                response.setDescription((String) meterResponseData.getMessageData());
            }
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "GetAdministrativeStatusRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public GetAdministrativeStatusAsyncResponse getAdministrativeStatus(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetAdministrativeStatusRequest request,
            @MessagePriority final String messagePriority, @ScheduleTime final String scheduleTime)
            throws OsgpException {

        final String correlationUid = this.configurationService.requestGetAdministrativeStatus(
                organisationIdentification, request.getDeviceIdentification(),
                MessagePriorityEnum.getMessagePriority(messagePriority),
                this.configurationMapper.map(scheduleTime, Long.class));

        final GetAdministrativeStatusAsyncResponse asyncResponse = new GetAdministrativeStatusAsyncResponse();

        asyncResponse.setCorrelationUid(correlationUid);
        asyncResponse.setDeviceIdentification(request.getDeviceIdentification());

        return asyncResponse;
    }

    @PayloadRoot(localPart = "GetAdministrativeStatusAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public GetAdministrativeStatusResponse retrieveGetAdministrativeStatusResponse(
            @RequestPayload final GetAdministrativeStatusAsyncRequest request) throws OsgpException {

        GetAdministrativeStatusResponse response = null;
        try {
            final MeterResponseData meterResponseData = this.configurationService
                    .dequeueGetAdministrativeStatusResponse(request.getCorrelationUid());

            this.throwExceptionIfResultNotOk(meterResponseData, "retrieving the administrative status");

            response = new GetAdministrativeStatusResponse();
            final AdministrativeStatusType dataRequest = this.configurationMapper.map(
                    meterResponseData.getMessageData(), AdministrativeStatusType.class);
            response.setEnabled(dataRequest);

        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "SetSpecialDaysRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetSpecialDaysAsyncResponse setSpecialDaysData(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetSpecialDaysRequest request, @MessagePriority final String messagePriority,
            @ScheduleTime final String scheduleTime) throws OsgpException {

        final SetSpecialDaysAsyncResponse response = new SetSpecialDaysAsyncResponse();

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequest dataRequest = this.configurationMapper
                .map(request, com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequest.class);

        final String correlationUid = this.configurationService.enqueueSetSpecialDaysRequest(
                organisationIdentification, dataRequest.getDeviceIdentification(), dataRequest,
                MessagePriorityEnum.getMessagePriority(messagePriority),
                this.configurationMapper.map(scheduleTime, Long.class));

        response.setCorrelationUid(correlationUid);
        response.setDeviceIdentification(request.getDeviceIdentification());

        return response;
    }

    @PayloadRoot(localPart = "SetSpecialDaysAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetSpecialDaysResponse getSetSpecialDaysResponse(@RequestPayload final SetSpecialDaysAsyncRequest request)
            throws OsgpException {

        SetSpecialDaysResponse response = null;
        try {
            response = new SetSpecialDaysResponse();
            final MeterResponseData meterResponseData = this.configurationService.dequeueSetSpecialDaysResponse(request
                    .getCorrelationUid());

            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            if (meterResponseData.getMessageData() instanceof String) {
                response.setDescription((String) meterResponseData.getMessageData());
            }
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "SetConfigurationObjectRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetConfigurationObjectAsyncResponse setConfigurationObject(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetConfigurationObjectRequest request, @MessagePriority final String messagePriority,
            @ScheduleTime final String scheduleTime) throws OsgpException {

        final SetConfigurationObjectAsyncResponse response = new SetConfigurationObjectAsyncResponse();

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest dataRequest = this.configurationMapper
                .map(request,
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest.class);

        final String correlationUid = this.configurationService.enqueueSetConfigurationObjectRequest(
                organisationIdentification, dataRequest.getDeviceIdentification(), dataRequest,
                MessagePriorityEnum.getMessagePriority(messagePriority),
                this.configurationMapper.map(scheduleTime, Long.class));

        response.setCorrelationUid(correlationUid);
        response.setDeviceIdentification(request.getDeviceIdentification());

        return response;
    }

    @PayloadRoot(localPart = "SetConfigurationObjectAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetConfigurationObjectResponse getSetConfigurationObjectResponse(
            @RequestPayload final SetConfigurationObjectAsyncRequest request) throws OsgpException {

        SetConfigurationObjectResponse response = null;
        try {
            response = new SetConfigurationObjectResponse();
            final MeterResponseData meterResponseData = this.configurationService
                    .dequeueSetConfigurationObjectResponse(request.getCorrelationUid());

            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            if (meterResponseData.getMessageData() instanceof String) {
                response.setDescription((String) meterResponseData.getMessageData());
            }
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "SetEncryptionKeyExchangeOnGMeterAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetEncryptionKeyExchangeOnGMeterResponse retrieveSetEncryptionKeyExchangeOnGMeterResponse(
            @RequestPayload final SetEncryptionKeyExchangeOnGMeterAsyncRequest request) throws OsgpException {

        SetEncryptionKeyExchangeOnGMeterResponse response = null;
        try {
            response = new SetEncryptionKeyExchangeOnGMeterResponse();
            final MeterResponseData meterResponseData = this.configurationService
                    .dequeueSetAdministrativeStatusResponse(request.getCorrelationUid());

            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            if (meterResponseData.getMessageData() instanceof String) {
                response.setDescription((String) meterResponseData.getMessageData());
            }
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "SetEncryptionKeyExchangeOnGMeterRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetEncryptionKeyExchangeOnGMeterAsyncResponse setEncryptionKeyExchangeOnGMeter(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetEncryptionKeyExchangeOnGMeterRequest request,
            @MessagePriority final String messagePriority, @ScheduleTime final String scheduleTime)
            throws OsgpException {

        LOGGER.info("Incoming SetEncryptionKeyExchangeOnGMeterRequest for meter: {}.",
                request.getDeviceIdentification());

        SetEncryptionKeyExchangeOnGMeterAsyncResponse response = null;
        try {
            response = new SetEncryptionKeyExchangeOnGMeterAsyncResponse();
            final String deviceIdentification = request.getDeviceIdentification();

            final String correlationUid = this.configurationService.enqueueSetEncryptionKeyExchangeOnGMeterRequest(
                    organisationIdentification, deviceIdentification,
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.configurationMapper.map(scheduleTime, Long.class));

            response.setCorrelationUid(correlationUid);
            response.setDeviceIdentification(request.getDeviceIdentification());
        } catch (final Exception e) {

            LOGGER.error(
                    "Exception: {} while setting Encryption Key Exchange On G-Meter on device: {} for organisation {}.",
                    new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification }, e);
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "SetPushSetupAlarmRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetPushSetupAlarmAsyncResponse setPushSetupAlarm(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetPushSetupAlarmRequest request, @MessagePriority final String messagePriority,
            @ScheduleTime final String scheduleTime) throws OsgpException {

        LOGGER.info("Incoming SetPushSetupAlarmRequest for meter: {}.", request.getDeviceIdentification());

        final SetPushSetupAlarmAsyncResponse response = new SetPushSetupAlarmAsyncResponse();

        final String deviceIdentification = request.getDeviceIdentification();
        final SetPushSetupAlarmRequestData requestData = request.getSetPushSetupAlarmRequestData();

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupAlarm pushSetupAlarm = this.configurationMapper
                .map(requestData.getPushSetupAlarm(),
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupAlarm.class);

        final String correlationUid = this.configurationService.enqueueSetPushSetupAlarmRequest(
                organisationIdentification, deviceIdentification, pushSetupAlarm,
                MessagePriorityEnum.getMessagePriority(messagePriority),
                this.configurationMapper.map(scheduleTime, Long.class));

        response.setCorrelationUid(correlationUid);
        response.setDeviceIdentification(deviceIdentification);

        return response;
    }

    @PayloadRoot(localPart = "SetPushSetupAlarmAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetPushSetupAlarmResponse getSetPushSetupAlarmResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetPushSetupAlarmAsyncRequest request) throws OsgpException {

        LOGGER.info("Incoming SetPushSetupAlarmAsyncRequest for organisation {} for meter: {}.",
                organisationIdentification, request.getDeviceIdentification());

        SetPushSetupAlarmResponse response = null;
        try {
            response = new SetPushSetupAlarmResponse();
            final MeterResponseData meterResponseData = this.configurationService
                    .dequeueSetPushSetupAlarmResponse(request.getCorrelationUid());

            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            if (meterResponseData.getMessageData() instanceof String) {
                response.setDescription((String) meterResponseData.getMessageData());
            }
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "SetPushSetupSmsRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetPushSetupSmsAsyncResponse setPushSetupSms(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetPushSetupSmsRequest request, @MessagePriority final String messagePriority,
            @ScheduleTime final String scheduleTime) throws OsgpException {

        LOGGER.info("Incoming SetPushSetupSmsRequest for meter: {}.", request.getDeviceIdentification());

        final SetPushSetupSmsAsyncResponse response = new SetPushSetupSmsAsyncResponse();

        final String deviceIdentification = request.getDeviceIdentification();
        final SetPushSetupSmsRequestData requestData = request.getSetPushSetupSmsRequestData();

        final com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupSms pushSetupSms = this.configurationMapper
                .map(requestData.getPushSetupSms(),
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupSms.class);

        final String correlationUid = this.configurationService.enqueueSetPushSetupSmsRequest(
                organisationIdentification, deviceIdentification, pushSetupSms,
                MessagePriorityEnum.getMessagePriority(messagePriority),
                this.configurationMapper.map(scheduleTime, Long.class));

        response.setCorrelationUid(correlationUid);
        response.setDeviceIdentification(deviceIdentification);

        return response;
    }

    @PayloadRoot(localPart = "SetPushSetupSmsAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetPushSetupSmsResponse getSetPushSetupSmsResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetPushSetupSmsAsyncRequest request) throws OsgpException {

        LOGGER.info("Incoming SetPushSetupAlarmAsyncRequest for organisation {} for meter: {}.",
                organisationIdentification, request.getDeviceIdentification());

        SetPushSetupSmsResponse response = null;
        try {
            response = new SetPushSetupSmsResponse();
            final MeterResponseData meterResponseData = this.configurationService
                    .dequeueSetPushSetupSmsResponse(request.getCorrelationUid());

            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            if (meterResponseData.getMessageData() instanceof String) {
                response.setDescription((String) meterResponseData.getMessageData());
            }
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "SetActivityCalendarRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetActivityCalendarAsyncResponse setActivityCalendar(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetActivityCalendarRequest request, @MessagePriority final String messagePriority,
            @ScheduleTime final String scheduleTime) throws OsgpException {

        LOGGER.info("Incoming SetActivityCalendarRequest for meter: {}.", request.getDeviceIdentification());

        SetActivityCalendarAsyncResponse response = null;
        try {
            response = new SetActivityCalendarAsyncResponse();
            final String deviceIdentification = request.getDeviceIdentification();
            final SetActivityCalendarRequestData requestData = request.getActivityCalendarData();

            final ActivityCalendar activityCalendar = this.configurationMapper.map(requestData.getActivityCalendar(),
                    ActivityCalendar.class);

            final String correlationUid = this.configurationService.enqueueSetActivityCalendarRequest(
                    organisationIdentification, deviceIdentification, activityCalendar,
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.configurationMapper.map(scheduleTime, Long.class));

            response.setCorrelationUid(correlationUid);
            response.setDeviceIdentification(request.getDeviceIdentification());
        } catch (final Exception e) {

            LOGGER.error("Exception: {} while setting activity calendar on device: {} for organisation {}.",
                    new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification }, e);

            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "SetActivityCalendarAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetActivityCalendarResponse getSetActivityCalendarResponse(
            @RequestPayload final SetActivityCalendarAsyncRequest request) throws OsgpException {

        LOGGER.info("Incoming retrieveSetActivityCalendarResponse for meter: {}", request.getDeviceIdentification());

        SetActivityCalendarResponse response = null;
        try {
            response = new SetActivityCalendarResponse();
            final MeterResponseData meterResponseData = this.configurationService
                    .dequeueSetActivityCalendarResponse(request.getCorrelationUid());

            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            if (meterResponseData.getMessageData() instanceof String) {
                response.setDescription((String) meterResponseData.getMessageData());
            }
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "SetAlarmNotificationsRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetAlarmNotificationsAsyncResponse setAlarmNotifications(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetAlarmNotificationsRequest request, @MessagePriority final String messagePriority,
            @ScheduleTime final String scheduleTime) throws OsgpException {

        LOGGER.info("Incoming SetAlarmNotificationsRequest for meter: {}.", request.getDeviceIdentification());

        SetAlarmNotificationsAsyncResponse response = null;
        try {
            response = new SetAlarmNotificationsAsyncResponse();
            final String deviceIdentification = request.getDeviceIdentification();
            final SetAlarmNotificationsRequestData requestData = request.getSetAlarmNotificationsRequestData();

            final AlarmNotifications alarmNotifications = this.configurationMapper.map(
                    requestData.getAlarmNotifications(), AlarmNotifications.class);

            final String correlationUid = this.configurationService.enqueueSetAlarmNotificationsRequest(
                    organisationIdentification, deviceIdentification, alarmNotifications,
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.configurationMapper.map(scheduleTime, Long.class));

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceIdentification(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);
        } catch (final Exception e) {

            LOGGER.error("Exception: {} while setting alarm notifications on device: {} for organisation {}.",
                    new Object[] { e.getMessage(), request.getDeviceIdentification(), organisationIdentification }, e);

            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "SetAlarmNotificationsAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetAlarmNotificationsResponse getSetAlarmNotificationsResponse(
            @RequestPayload final SetAlarmNotificationsAsyncRequest request) throws OsgpException {

        SetAlarmNotificationsResponse response = null;
        try {
            response = new SetAlarmNotificationsResponse();
            final MeterResponseData meterResponseData = this.configurationService
                    .dequeueSetConfigurationObjectResponse(request.getCorrelationUid());

            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            if (meterResponseData.getMessageData() instanceof String) {
                response.setDescription((String) meterResponseData.getMessageData());
            }
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "ReplaceKeysRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public ReplaceKeysAsyncResponse replaceKeys(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final ReplaceKeysRequest request, @MessagePriority final String messagePriority,
            @ScheduleTime final String scheduleTime) throws OsgpException {

        ReplaceKeysAsyncResponse asyncResponse = null;
        try {
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.SetKeysRequestData keySet = this.configurationMapper
                    .map(request.getSetKeysRequestData(),
                            com.alliander.osgp.domain.core.valueobjects.smartmetering.SetKeysRequestData.class);

            final String correlationUid = this.configurationService.enqueueReplaceKeysRequest(
                    organisationIdentification, request.getDeviceIdentification(), keySet,
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.configurationMapper.map(scheduleTime, Long.class));

            asyncResponse = new com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ObjectFactory()
                    .createReplaceKeysAsyncResponse();

            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceIdentification(request.getDeviceIdentification());
        } catch (final Exception e) {
            this.handleException(e);
        }

        return asyncResponse;
    }

    @PayloadRoot(localPart = "ReplaceKeysAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public ReplaceKeysResponse getReplaceKeysResponse(@RequestPayload final ReplaceKeysAsyncRequest request)
            throws OsgpException {

        ReplaceKeysResponse response = null;
        try {
            final MeterResponseData meterResponseData = this.configurationService.dequeueReplaceKeysResponse(request
                    .getCorrelationUid());

            response = new ReplaceKeysResponse();
            response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
            if (meterResponseData.getMessageData() instanceof String) {
                response.setDescription((String) meterResponseData.getMessageData());
            }

        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

}
