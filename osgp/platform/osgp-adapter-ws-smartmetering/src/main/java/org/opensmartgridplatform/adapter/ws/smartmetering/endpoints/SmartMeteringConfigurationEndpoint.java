/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.endpoints;

import java.util.List;

import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.MessagePriority;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.ResponseUrl;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.ScheduleTime;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.AsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.EncryptionKeyStatus;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.FirmwareVersion;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GenerateAndReplaceKeysAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GenerateAndReplaceKeysAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GenerateAndReplaceKeysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GenerateAndReplaceKeysResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetConfigurationObjectAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetConfigurationObjectAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetConfigurationObjectRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetConfigurationObjectResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusByChannelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusByChannelAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusByChannelResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetPushNotificationAlarmAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetPushNotificationAlarmResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ReplaceKeysAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ReplaceKeysAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ReplaceKeysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ReplaceKeysResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarRequestData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsRequestData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupAlarmAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupAlarmAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupAlarmRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupAlarmRequestData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupAlarmResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsRequestData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetRandomisationSettingsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetRandomisationSettingsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetRandomisationSettingsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetRandomisationSettingsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareRequest;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.services.ConfigurationService;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActivityCalendar;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmNotifications;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EncryptionKeyStatusType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.FirmwareVersionResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushNotificationAlarm;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetMbusUserKeyByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.UpdateFirmwareRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.UpdateFirmwareResponse;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class SmartMeteringConfigurationEndpoint extends SmartMeteringEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringConfigurationEndpoint.class);
    private static final String SMARTMETER_CONFIGURATION_NAMESPACE =
            "http://www.opensmartgridplatform" + ".org/schemas/smartmetering/sm-configuration/2014/10";

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ConfigurationMapper configurationMapper;

    public SmartMeteringConfigurationEndpoint() {
        // Default constructor
    }

    /**
     * Starts the proces of retrieving the firmware version(s) of the device
     * specified in the {@link GetFirmwareVersionRequest}
     *
     * @param organisationIdentification
     *         {@link String} containing the identification of the
     *         organization
     * @param request
     *         the {@link GetFirmwareVersionRequest}
     * @param messagePriority
     *         the message priority
     * @param scheduleTime
     *         the time the message is scheduled
     *
     * @return the {@link GetFirmwareVersionAsyncResponse} containing the
     *         correlation id
     *
     * @throws OsgpException
     */
    @PayloadRoot(localPart = "GetFirmwareVersionRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public GetFirmwareVersionAsyncResponse getFirmwareVersion(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetFirmwareVersionRequest request, @MessagePriority final String messagePriority,
            @ScheduleTime final String scheduleTime, @ResponseUrl final String responseUrl) throws OsgpException {

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
            this.saveResponseUrlIfNeeded(correlationUid, responseUrl);
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
     *         {@link String} containing the identification of the
     *         organization
     * @param request
     *         {@link GetFirmwareVersionAsyncRequest} containing the
     *         correlation id as the response identifier
     *
     * @return {@link GetFirmwareVersionResponse} containing the firmware
     *         version(s) for the device.
     *
     * @throws OsgpException
     *         is thrown when the correlationId cannot be found in the
     *         database
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
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);
            if (responseData != null) {
                response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));

                if (responseData.getMessageData() != null) {
                    final List<FirmwareVersion> target = response.getFirmwareVersion();
                    final FirmwareVersionResponse firmwareVersionResponse =
                            (FirmwareVersionResponse) responseData.getMessageData();
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
            @ScheduleTime final String scheduleTime, @ResponseUrl final String responseUrl) throws OsgpException {

        LOGGER.info("UpdateFirmware Request received from organisation {} for device {}.", organisationIdentification,
                request.getDeviceIdentification());

        final UpdateFirmwareAsyncResponse response = new UpdateFirmwareAsyncResponse();

        try {
            final UpdateFirmwareRequestData updateFirmwareRequestData = this.configurationMapper.map(request,
                    UpdateFirmwareRequestData.class);

            final String correlationUid = this.configurationService.enqueueUpdateFirmwareRequest(
                    organisationIdentification, request.getDeviceIdentification(), updateFirmwareRequestData,
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.configurationMapper.map(scheduleTime, Long.class));

            response.setCorrelationUid(correlationUid);
            response.setDeviceIdentification(request.getDeviceIdentification());
            this.saveResponseUrlIfNeeded(correlationUid, responseUrl);

        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "UpdateFirmwareAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareResponse getUpdateFirmwareResponse(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final UpdateFirmwareAsyncRequest request) throws OsgpException {

        LOGGER.info("GetUpdateFirmwareResponse Request received from organisation {} for device: {}.",
                organisationIdentification, request.getDeviceIdentification());

        final org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareResponse response = new org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareResponse();

        try {
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            this.throwExceptionIfResultNotOk(responseData, "updating firmware");

            if (responseData != null) {
                response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));

                if (responseData.getMessageData() != null) {
                    final List<FirmwareVersion> target = response.getFirmwareVersion();
                    final UpdateFirmwareResponse updateFirmwareResponse =
                            (UpdateFirmwareResponse) responseData.getMessageData();
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
            @RequestPayload final SetAdministrativeStatusRequest request, @MessagePriority final String messagePriority,
            @ScheduleTime final String scheduleTime, @ResponseUrl final String responseUrl) throws OsgpException {

        final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AdministrativeStatusType dataRequest =
                this.configurationMapper.map(
                        request.getEnabled(),
                        org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AdministrativeStatusType.class);

        final String correlationUid = this.configurationService.requestSetAdministrativeStatus(
                organisationIdentification, request.getDeviceIdentification(), dataRequest,
                MessagePriorityEnum.getMessagePriority(messagePriority),
                this.configurationMapper.map(scheduleTime, Long.class));

        final SetAdministrativeStatusAsyncResponse asyncResponse =
                new org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ObjectFactory().createSetAdministrativeStatusAsyncResponse();
        asyncResponse.setCorrelationUid(correlationUid);
        asyncResponse.setDeviceIdentification(request.getDeviceIdentification());

        this.saveResponseUrlIfNeeded(correlationUid, responseUrl);

        return asyncResponse;
    }

    @PayloadRoot(localPart = "SetAdministrativeStatusAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetAdministrativeStatusResponse retrieveSetAdministrativeStatusResponse(
            @RequestPayload final SetAdministrativeStatusAsyncRequest request) throws OsgpException {

        SetAdministrativeStatusResponse response = null;
        try {
            response = new SetAdministrativeStatusResponse();
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
            if (responseData.getMessageData() instanceof String) {
                response.setDescription((String) responseData.getMessageData());
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
            @RequestPayload final GetAdministrativeStatusRequest request, @MessagePriority final String messagePriority,
            @ScheduleTime final String scheduleTime, @ResponseUrl final String responseUrl) throws OsgpException {

        final String correlationUid = this.configurationService.requestGetAdministrativeStatus(
                organisationIdentification, request.getDeviceIdentification(),
                MessagePriorityEnum.getMessagePriority(messagePriority),
                this.configurationMapper.map(scheduleTime, Long.class));

        final GetAdministrativeStatusAsyncResponse asyncResponse = new GetAdministrativeStatusAsyncResponse();

        asyncResponse.setCorrelationUid(correlationUid);
        asyncResponse.setDeviceIdentification(request.getDeviceIdentification());

        this.saveResponseUrlIfNeeded(correlationUid, responseUrl);

        return asyncResponse;
    }

    @PayloadRoot(localPart = "GetAdministrativeStatusAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public GetAdministrativeStatusResponse retrieveGetAdministrativeStatusResponse(
            @RequestPayload final GetAdministrativeStatusAsyncRequest request) throws OsgpException {

        GetAdministrativeStatusResponse response = null;
        try {
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            this.throwExceptionIfResultNotOk(responseData, "retrieving the administrative status");

            response = new GetAdministrativeStatusResponse();
            final AdministrativeStatusType dataRequest = this.configurationMapper.map(responseData.getMessageData(),
                    AdministrativeStatusType.class);
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
            @ScheduleTime final String scheduleTime, @ResponseUrl final String responseUrl) throws OsgpException {

        final SetSpecialDaysAsyncResponse response = new SetSpecialDaysAsyncResponse();

        final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecialDaysRequest dataRequest =
                this.configurationMapper.map(
                        request, org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecialDaysRequest.class);

        final String correlationUid = this.configurationService.enqueueSetSpecialDaysRequest(organisationIdentification,
                dataRequest.getDeviceIdentification(), dataRequest,
                MessagePriorityEnum.getMessagePriority(messagePriority),
                this.configurationMapper.map(scheduleTime, Long.class));

        response.setCorrelationUid(correlationUid);
        response.setDeviceIdentification(request.getDeviceIdentification());

        this.saveResponseUrlIfNeeded(correlationUid, responseUrl);

        return response;
    }

    @PayloadRoot(localPart = "SetSpecialDaysAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetSpecialDaysResponse getSetSpecialDaysResponse(@RequestPayload final SetSpecialDaysAsyncRequest request)
            throws OsgpException {

        SetSpecialDaysResponse response = null;
        try {
            response = new SetSpecialDaysResponse();
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
            if (responseData.getMessageData() instanceof String) {
                response.setDescription((String) responseData.getMessageData());
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
            @ScheduleTime final String scheduleTime, @ResponseUrl final String responseUrl) throws OsgpException {

        final SetConfigurationObjectAsyncResponse response = new SetConfigurationObjectAsyncResponse();

        final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest dataRequest = this.configurationMapper.map(
                request,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest.class);

        final String correlationUid = this.configurationService.enqueueSetConfigurationObjectRequest(
                organisationIdentification, dataRequest.getDeviceIdentification(), dataRequest,
                MessagePriorityEnum.getMessagePriority(messagePriority),
                this.configurationMapper.map(scheduleTime, Long.class));

        response.setCorrelationUid(correlationUid);
        response.setDeviceIdentification(request.getDeviceIdentification());

        this.saveResponseUrlIfNeeded(correlationUid, responseUrl);

        return response;
    }

    @PayloadRoot(localPart = "SetConfigurationObjectAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetConfigurationObjectResponse getSetConfigurationObjectResponse(
            @RequestPayload final SetConfigurationObjectAsyncRequest request) throws OsgpException {

        SetConfigurationObjectResponse response = null;
        try {
            response = new SetConfigurationObjectResponse();
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
            if (responseData.getMessageData() instanceof String) {
                response.setDescription((String) responseData.getMessageData());
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
            @MessagePriority final String messagePriority, @ScheduleTime final String scheduleTime,
            @ResponseUrl final String responseUrl) throws OsgpException {

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
            this.saveResponseUrlIfNeeded(correlationUid, responseUrl);
        } catch (final Exception e) {

            LOGGER.error(
                    "Exception: {} while setting Encryption Key Exchange On G-Meter on device: {} for organisation {}.",
                    e.getMessage(), request.getDeviceIdentification(), organisationIdentification, e);
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "SetEncryptionKeyExchangeOnGMeterAsyncRequest", namespace =
            SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetEncryptionKeyExchangeOnGMeterResponse retrieveSetEncryptionKeyExchangeOnGMeterResponse(
            @RequestPayload final SetEncryptionKeyExchangeOnGMeterAsyncRequest request) throws OsgpException {

        SetEncryptionKeyExchangeOnGMeterResponse response = null;
        try {
            response = new SetEncryptionKeyExchangeOnGMeterResponse();
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
            if (responseData.getMessageData() instanceof String) {
                response.setDescription((String) responseData.getMessageData());
            }
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "GetMbusEncryptionKeyStatusRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public GetMbusEncryptionKeyStatusAsyncResponse getMbusEncryptionKeyStatus(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetMbusEncryptionKeyStatusRequest request,
            @MessagePriority final String messagePriority, @ScheduleTime final String scheduleTime,
            @ResponseUrl final String responseUrl) throws OsgpException {

        LOGGER.info("Get M-Bus encryption key status request received from organisation {} for device {}",
                organisationIdentification, request.getDeviceIdentification());

        GetMbusEncryptionKeyStatusAsyncResponse asyncResponse = null;
        try {
            final String correlationUid = this.configurationService.enqueueGetMbusEncryptionKeyStatusRequest(
                    organisationIdentification, request.getDeviceIdentification(),
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.configurationMapper.map(scheduleTime, Long.class));

            asyncResponse = new GetMbusEncryptionKeyStatusAsyncResponse();

            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceIdentification(request.getDeviceIdentification());
            this.saveResponseUrlIfNeeded(correlationUid, responseUrl);
        } catch (final Exception e) {
            this.handleException(e);
        }

        return asyncResponse;
    }

    @PayloadRoot(localPart = "GetMbusEncryptionKeyStatusAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public GetMbusEncryptionKeyStatusResponse getGetMBusEncryptionKeyStatusResponse(
            @RequestPayload final GetMbusEncryptionKeyStatusAsyncRequest request) throws OsgpException {

        GetMbusEncryptionKeyStatusResponse response = null;
        try {
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            this.throwExceptionIfResultNotOk(responseData, "retrieving the M-Bus encryption key status.");

            response = new GetMbusEncryptionKeyStatusResponse();
            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
            response.setEncryptionKeyStatus(
                    EncryptionKeyStatus.fromValue(((EncryptionKeyStatusType) responseData.getMessageData()).name()));

        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "GetMbusEncryptionKeyStatusByChannelRequest", namespace =
            SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public GetMbusEncryptionKeyStatusByChannelAsyncResponse getMbusEncryptionKeyStatusByChannel(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetMbusEncryptionKeyStatusByChannelRequest request,
            @MessagePriority final String messagePriority, @ScheduleTime final String scheduleTime,
            @ResponseUrl final String responseUrl) throws OsgpException {

        LOGGER.info("Get M-Bus encryption key status by channel request received from organisation {} for device {}",
                organisationIdentification, request.getGatewayDeviceIdentification());

        GetMbusEncryptionKeyStatusByChannelAsyncResponse asyncResponse = null;
        try {
            final String correlationUid = this.configurationService.enqueueGetMbusEncryptionKeyStatusByChannelRequest(
                    organisationIdentification, request.getGatewayDeviceIdentification(),
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.configurationMapper.map(scheduleTime, Long.class),
                    request.getGetMbusEncryptionKeyStatusByChannelRequestData().getChannel());

            asyncResponse = new GetMbusEncryptionKeyStatusByChannelAsyncResponse();

            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceIdentification(request.getGatewayDeviceIdentification());
            this.saveResponseUrlIfNeeded(correlationUid, responseUrl);
        } catch (final Exception e) {
            this.handleException(e);
        }

        return asyncResponse;
    }

    @PayloadRoot(localPart = "GetMbusEncryptionKeyStatusByChannelAsyncRequest", namespace =
            SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public GetMbusEncryptionKeyStatusByChannelResponse getGetMBusEncryptionKeyStatusByChannelResponse(
            @RequestPayload final GetMbusEncryptionKeyStatusByChannelAsyncRequest request) throws OsgpException {

        GetMbusEncryptionKeyStatusByChannelResponse response = null;
        try {
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            this.throwExceptionIfResultNotOk(responseData, "retrieving the M-Bus encryption key status by channel.");

            response = new GetMbusEncryptionKeyStatusByChannelResponse();
            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
            response.setEncryptionKeyStatus(
                    EncryptionKeyStatus.fromValue(((EncryptionKeyStatusType) responseData.getMessageData()).name()));

        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "SetPushSetupAlarmRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetPushSetupAlarmAsyncResponse setPushSetupAlarm(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetPushSetupAlarmRequest request, @MessagePriority final String messagePriority,
            @ScheduleTime final String scheduleTime, @ResponseUrl final String responseUrl) throws OsgpException {

        LOGGER.info("Incoming SetPushSetupAlarmRequest for meter: {}.", request.getDeviceIdentification());

        final SetPushSetupAlarmAsyncResponse response = new SetPushSetupAlarmAsyncResponse();

        final String deviceIdentification = request.getDeviceIdentification();
        final SetPushSetupAlarmRequestData requestData = request.getSetPushSetupAlarmRequestData();

        final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupAlarm pushSetupAlarm =
                this.configurationMapper.map(
                        requestData.getPushSetupAlarm(),
                        org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupAlarm.class);

        final String correlationUid = this.configurationService.enqueueSetPushSetupAlarmRequest(
                organisationIdentification, deviceIdentification, pushSetupAlarm,
                MessagePriorityEnum.getMessagePriority(messagePriority),
                this.configurationMapper.map(scheduleTime, Long.class));

        response.setCorrelationUid(correlationUid);
        response.setDeviceIdentification(deviceIdentification);

        this.saveResponseUrlIfNeeded(correlationUid, responseUrl);

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
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
            if (responseData.getMessageData() instanceof String) {
                response.setDescription((String) responseData.getMessageData());
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
            @ScheduleTime final String scheduleTime, @ResponseUrl final String responseUrl) throws OsgpException {

        LOGGER.info("Incoming SetPushSetupSmsRequest for meter: {}.", request.getDeviceIdentification());

        final SetPushSetupSmsAsyncResponse response = new SetPushSetupSmsAsyncResponse();

        final String deviceIdentification = request.getDeviceIdentification();
        final SetPushSetupSmsRequestData requestData = request.getSetPushSetupSmsRequestData();

        final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupSms pushSetupSms =
                this.configurationMapper.map(
                        requestData.getPushSetupSms(),
                        org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupSms.class);

        final String correlationUid = this.configurationService.enqueueSetPushSetupSmsRequest(
                organisationIdentification, deviceIdentification, pushSetupSms,
                MessagePriorityEnum.getMessagePriority(messagePriority),
                this.configurationMapper.map(scheduleTime, Long.class));

        response.setCorrelationUid(correlationUid);
        response.setDeviceIdentification(deviceIdentification);

        this.saveResponseUrlIfNeeded(correlationUid, responseUrl);

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
            final ResponseData meterResponseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

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
            @ScheduleTime final String scheduleTime, @ResponseUrl final String responseUrl) throws OsgpException {

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
            this.saveResponseUrlIfNeeded(correlationUid, responseUrl);
        } catch (final Exception e) {

            LOGGER.error("Exception: {} while setting activity calendar on device: {} for organisation {}.",
                    e.getMessage(), request.getDeviceIdentification(), organisationIdentification, e);

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
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
            if (responseData.getMessageData() instanceof String) {
                response.setDescription((String) responseData.getMessageData());
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
            @ScheduleTime final String scheduleTime, @ResponseUrl final String responseUrl) throws OsgpException {

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
            this.saveResponseUrlIfNeeded(correlationUid, responseUrl);
        } catch (final Exception e) {

            LOGGER.error("Exception: {} while setting alarm notifications on device: {} for organisation {}.",
                    e.getMessage(), request.getDeviceIdentification(), organisationIdentification, e);

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
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
            if (responseData.getMessageData() instanceof String) {
                response.setDescription((String) responseData.getMessageData());
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
            @ScheduleTime final String scheduleTime, @ResponseUrl final String responseUrl) throws OsgpException {

        ReplaceKeysAsyncResponse asyncResponse = null;
        try {
            final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetKeysRequestData keySet =
                    this.configurationMapper.map(
                            request.getSetKeysRequestData(),
                            org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetKeysRequestData.class);

            final String correlationUid = this.configurationService.enqueueReplaceKeysRequest(
                    organisationIdentification, request.getDeviceIdentification(), keySet,
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.configurationMapper.map(scheduleTime, Long.class));

            asyncResponse =
                    new org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ObjectFactory().createReplaceKeysAsyncResponse();

            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceIdentification(request.getDeviceIdentification());
            this.saveResponseUrlIfNeeded(correlationUid, responseUrl);
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
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            this.throwExceptionIfResultNotOk(responseData, "replacing keys on the device");

            response = new ReplaceKeysResponse();
            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
            if (responseData.getMessageData() instanceof String) {
                response.setDescription((String) responseData.getMessageData());
            }

        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "GenerateAndReplaceKeysRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public GenerateAndReplaceKeysAsyncResponse generateAndReplaceKeys(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GenerateAndReplaceKeysRequest request, @MessagePriority final String messagePriority,
            @ScheduleTime final String scheduleTime, @ResponseUrl final String responseUrl) throws OsgpException {

        GenerateAndReplaceKeysAsyncResponse asyncResponse = null;
        try {
            LOGGER.info("Generate and replace keys request received from organisation {} for device {}.",
                    organisationIdentification, request.getDeviceIdentification());

            final String correlationUid = this.configurationService.enqueueGenerateAndReplaceKeysRequest(
                    organisationIdentification, request.getDeviceIdentification(),
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.configurationMapper.map(scheduleTime, Long.class));

            asyncResponse =
                    new org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ObjectFactory().createGenerateAndReplaceKeysAsyncResponse();

            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceIdentification(request.getDeviceIdentification());
            this.saveResponseUrlIfNeeded(correlationUid, responseUrl);
        } catch (final Exception e) {
            this.handleException(e);
        }

        return asyncResponse;
    }

    @PayloadRoot(localPart = "GenerateAndReplaceKeysAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public GenerateAndReplaceKeysResponse getGenerateAndReplaceKeysResponse(
            @RequestPayload final GenerateAndReplaceKeysAsyncRequest request) throws OsgpException {

        GenerateAndReplaceKeysResponse response = null;
        try {
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            response = new GenerateAndReplaceKeysResponse();
            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
            if (responseData.getMessageData() instanceof String) {
                response.setDescription((String) responseData.getMessageData());
            }

        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "GetPushNotificationAlarmAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public GetPushNotificationAlarmResponse getPushNotificationAlarm(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetPushNotificationAlarmAsyncRequest request) throws OsgpException {

        LOGGER.info("GetPushNotificationAlarmRequest Request received from organisation {} for device: {}.",
                organisationIdentification, request.getDeviceIdentification());

        final GetPushNotificationAlarmResponse response = new GetPushNotificationAlarmResponse();

        try {
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);
            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
            if (responseData != null) {

                final PushNotificationAlarm p = (PushNotificationAlarm) responseData.getMessageData();

                response.setDecodedMessage(p.toString());
                response.setEncodedMessage(p.getAlarmBytes());

                response.getAlarm().addAll(this.configurationMapper.mapAsList(p.getAlarms(),
                        org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.AlarmType.class));

            }
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "SetClockConfigurationRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetClockConfigurationAsyncResponse setClockConfiguration(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetClockConfigurationRequest request, @MessagePriority final String messagePriority,
            @ScheduleTime final String scheduleTime, @ResponseUrl final String responseUrl) throws OsgpException {

        SetClockConfigurationAsyncResponse asyncResponse = null;
        try {
            final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetClockConfigurationRequestData clockConfiguration = this.configurationMapper.map(
                    request.getSetClockConfigurationData(),
                    org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetClockConfigurationRequestData.class);

            final String correlationUid = this.configurationService.enqueueSetClockConfigurationRequest(
                    organisationIdentification, request.getDeviceIdentification(), clockConfiguration,
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.configurationMapper.map(scheduleTime, Long.class));

            asyncResponse = new SetClockConfigurationAsyncResponse();

            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceIdentification(request.getDeviceIdentification());
            this.saveResponseUrlIfNeeded(correlationUid, responseUrl);
        } catch (final Exception e) {
            this.handleException(e);
        }

        return asyncResponse;
    }

    @PayloadRoot(localPart = "SetClockConfigurationAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetClockConfigurationResponse getSetClockConfigurationResponse(
            @RequestPayload final SetClockConfigurationAsyncRequest request) throws OsgpException {

        SetClockConfigurationResponse response = null;
        try {
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            response = new SetClockConfigurationResponse();
            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
            if (responseData.getMessageData() instanceof String) {
                response.setDescription((String) responseData.getMessageData());
            }

        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "GetConfigurationObjectRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public GetConfigurationObjectAsyncResponse getConfigurationObject(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetConfigurationObjectRequest request, @MessagePriority final String messagePriority,
            @ScheduleTime final String scheduleTime, @ResponseUrl final String responseUrl) throws OsgpException {

        final GetConfigurationObjectAsyncResponse response = new GetConfigurationObjectAsyncResponse();

        final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetConfigurationObjectRequest dataRequest = this.configurationMapper.map(
                request,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetConfigurationObjectRequest.class);

        final String correlationUid = this.configurationService.enqueueGetConfigurationObjectRequest(
                organisationIdentification, dataRequest.getDeviceIdentification(), dataRequest,
                MessagePriorityEnum.getMessagePriority(messagePriority),
                this.configurationMapper.map(scheduleTime, Long.class));

        response.setCorrelationUid(correlationUid);
        response.setDeviceIdentification(request.getDeviceIdentification());

        this.saveResponseUrlIfNeeded(correlationUid, responseUrl);

        return response;
    }

    @PayloadRoot(localPart = "GetConfigurationObjectAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public GetConfigurationObjectResponse getGetConfigurationObjectResponse(
            @RequestPayload final GetConfigurationObjectAsyncRequest request) throws OsgpException {

        GetConfigurationObjectResponse response = null;
        try {
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetConfigurationObjectResponse.class,
                    ComponentType.WS_SMART_METERING);

            this.throwExceptionIfResultNotOk(responseData, "retrieving the configuration object");

            response = this.configurationMapper.map(responseData.getMessageData(),
                    GetConfigurationObjectResponse.class);
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "ConfigureDefinableLoadProfileRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public ConfigureDefinableLoadProfileAsyncResponse configureDefinableLoadProfile(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final ConfigureDefinableLoadProfileRequest request,
            @MessagePriority final String messagePriority, @ScheduleTime final String scheduleTime,
            @ResponseUrl final String responseUrl) throws OsgpException {

        ConfigureDefinableLoadProfileAsyncResponse asyncResponse = null;
        try {
            final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DefinableLoadProfileConfigurationData definableLoadProfileConfiguration = this.configurationMapper.map(
                    request.getDefinableLoadProfileConfigurationData(),
                    org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DefinableLoadProfileConfigurationData.class);

            final String correlationUid = this.configurationService.enqueueConfigureDefinableLoadProfileRequest(
                    organisationIdentification, request.getDeviceIdentification(), definableLoadProfileConfiguration,
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.configurationMapper.map(scheduleTime, Long.class));

            asyncResponse = new ConfigureDefinableLoadProfileAsyncResponse();

            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceIdentification(request.getDeviceIdentification());
            this.saveResponseUrlIfNeeded(correlationUid, responseUrl);
        } catch (final Exception e) {
            this.handleException(e);
        }

        return asyncResponse;
    }

    @PayloadRoot(localPart = "ConfigureDefinableLoadProfileAsyncRequest", namespace =
            SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public ConfigureDefinableLoadProfileResponse getConfigureDefinableLoadProfileResponse(
            @RequestPayload final ConfigureDefinableLoadProfileAsyncRequest request) throws OsgpException {

        ConfigureDefinableLoadProfileResponse response = null;
        try {
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            response = new ConfigureDefinableLoadProfileResponse();
            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
            if (responseData.getMessageData() instanceof String) {
                response.setDescription((String) responseData.getMessageData());
            }

        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "SetMbusUserKeyByChannelRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetMbusUserKeyByChannelAsyncResponse setMbusUserKeyByChannel(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetMbusUserKeyByChannelRequest request, @MessagePriority final String messagePriority,
            @ScheduleTime final String scheduleTime, @ResponseUrl final String responseUrl) throws OsgpException {

        LOGGER.info("Incoming SetMbusUserKeyByChannelRequest for gateway: {}.", request.getDeviceIdentification());

        SetMbusUserKeyByChannelAsyncResponse response = null;
        try {
            final SetMbusUserKeyByChannelRequestData setMbusUserKeyByChannelRequestData = this.configurationMapper.map(
                    request.getSetMbusUserKeyByChannelRequestData(), SetMbusUserKeyByChannelRequestData.class);

            response = new SetMbusUserKeyByChannelAsyncResponse();
            final String deviceIdentification = request.getDeviceIdentification();

            final String correlationUid = this.configurationService.enqueueSetMbusUserKeyByChannelRequest(
                    organisationIdentification, deviceIdentification, setMbusUserKeyByChannelRequestData,
                    MessagePriorityEnum.getMessagePriority(messagePriority),
                    this.configurationMapper.map(scheduleTime, Long.class));

            response.setCorrelationUid(correlationUid);
            response.setDeviceIdentification(request.getDeviceIdentification());
            this.saveResponseUrlIfNeeded(correlationUid, responseUrl);
        } catch (final Exception e) {

            LOGGER.error("Exception: {} while setting M-Bus User Key By Channel for G-Meter behind gateway: {} for "
                            + "organisation {}.", e.getMessage(), request.getDeviceIdentification(),
                    organisationIdentification,
                    e);
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "SetMbusUserKeyByChannelAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetMbusUserKeyByChannelResponse getSetMbusUserKeyByChannelResponse(
            @RequestPayload final SetMbusUserKeyByChannelAsyncRequest request) throws OsgpException {

        SetMbusUserKeyByChannelResponse response = null;
        try {
            response = new SetMbusUserKeyByChannelResponse();
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
            if (responseData.getMessageData() instanceof String) {
                response.setDescription((String) responseData.getMessageData());
            }
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

    @PayloadRoot(localPart = "SetRandomisationSettingsRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetRandomisationSettingsAsyncResponse setRandomisationSettings(
            @OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetRandomisationSettingsRequest request,
            @MessagePriority final String messagePriority, @ScheduleTime final String scheduleTime,
            @ResponseUrl final String responseUrl) throws OsgpException {

        LOGGER.info("-- calling setRandomisationSettings ");

        final SetRandomisationSettingsAsyncResponse response = new SetRandomisationSettingsAsyncResponse();

        final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetRandomisationSettingsRequest dataRequest = this.configurationMapper.map(
                request,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetRandomisationSettingsRequest.class);

        final String correlationUid = this.configurationService.enqueueSetRandomisationSettingsRequest(
                organisationIdentification, dataRequest.getDeviceIdentification(), dataRequest,
                MessagePriorityEnum.getMessagePriority(messagePriority),
                this.configurationMapper.map(scheduleTime, Long.class));

        response.setCorrelationUid(correlationUid);
        response.setDeviceIdentification(request.getDeviceIdentification());

        this.saveResponseUrlIfNeeded(correlationUid, responseUrl);

        return response;
    }

    @PayloadRoot(localPart = "SetRandomisationSettingsAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
    @ResponsePayload
    public SetRandomisationSettingsResponse getSetRandomisationSettingsResponse(
            @RequestPayload final SetRandomisationSettingsAsyncRequest request) throws OsgpException {

        LOGGER.info("-- calling getSetRandomisationSettingsResponse ");

        SetRandomisationSettingsResponse response = null;
        try {
            response = new SetRandomisationSettingsResponse();
            final ResponseData responseData = this.responseDataService.dequeue(request.getCorrelationUid(),
                    ComponentType.WS_SMART_METERING);

            response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
            if (responseData.getMessageData() instanceof String) {
                response.setDescription((String) responseData.getMessageData());
            }
        } catch (final Exception e) {
            this.handleException(e);
        }
        return response;
    }

}
