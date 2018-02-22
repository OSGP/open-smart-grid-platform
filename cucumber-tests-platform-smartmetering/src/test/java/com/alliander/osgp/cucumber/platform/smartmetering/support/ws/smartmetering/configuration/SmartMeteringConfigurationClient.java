/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GenerateAndReplaceKeysAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GenerateAndReplaceKeysAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GenerateAndReplaceKeysRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GenerateAndReplaceKeysResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusByChannelAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusByChannelAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusByChannelRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusByChannelResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ReplaceKeysAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ReplaceKeysAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ReplaceKeysRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ReplaceKeysResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareResponse;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.SmartMeteringBaseClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;
import com.alliander.osgp.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Component
public class SmartMeteringConfigurationClient extends SmartMeteringBaseClient {

    @Autowired
    private DefaultWebServiceTemplateFactory smartMeteringConfigurationWebServiceTemplateFactory;

    public UpdateFirmwareAsyncResponse updateFirmware(final UpdateFirmwareRequest updateFirmwareRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        return (UpdateFirmwareAsyncResponse) this.getTemplate().marshalSendAndReceive(updateFirmwareRequest);
    }

    public UpdateFirmwareResponse getUpdateFirmwareResponse(final UpdateFirmwareAsyncRequest updateFirmwareAsyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = updateFirmwareAsyncRequest.getCorrelationUid();
        this.waitForNotification(correlationUid);

        return (UpdateFirmwareResponse) this.getTemplate().marshalSendAndReceive(updateFirmwareAsyncRequest);
    }

    public GetFirmwareVersionAsyncResponse getFirmwareVersion(final GetFirmwareVersionRequest getFirmwareVersionRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return (GetFirmwareVersionAsyncResponse) this.getTemplate().marshalSendAndReceive(getFirmwareVersionRequest);
    }

    public GetFirmwareVersionResponse retrieveGetFirmwareVersionResponse(
            final GetFirmwareVersionAsyncRequest getFirmwareVersionAsyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = getFirmwareVersionAsyncRequest.getCorrelationUid();
        this.waitForNotification(correlationUid);

        return (GetFirmwareVersionResponse) this.getTemplate().marshalSendAndReceive(getFirmwareVersionAsyncRequest);
    }

    public SetActivityCalendarAsyncResponse setActivityCalendar(
            final SetActivityCalendarRequest setActivityCalendarRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return (SetActivityCalendarAsyncResponse) this.getTemplate().marshalSendAndReceive(setActivityCalendarRequest);
    }

    public SetActivityCalendarResponse getSetActivityCalendarResponse(
            final SetActivityCalendarAsyncRequest setActivityCalendarAsyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = setActivityCalendarAsyncRequest.getCorrelationUid();
        this.waitForNotification(correlationUid);

        return (SetActivityCalendarResponse) this.getTemplate().marshalSendAndReceive(setActivityCalendarAsyncRequest);
    }

    public GetAdministrativeStatusAsyncResponse getAdministrativeStatus(
            final GetAdministrativeStatusRequest getAdministrativeStatusRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return (GetAdministrativeStatusAsyncResponse) this.getTemplate()
                .marshalSendAndReceive(getAdministrativeStatusRequest);
    }

    public GetAdministrativeStatusResponse retrieveGetAdministrativeStatusResponse(
            final GetAdministrativeStatusAsyncRequest getAdministrativeStatusAsyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = getAdministrativeStatusAsyncRequest.getCorrelationUid();
        this.waitForNotification(correlationUid);

        return (GetAdministrativeStatusResponse) this.getTemplate()
                .marshalSendAndReceive(getAdministrativeStatusAsyncRequest);
    }

    public SetAdministrativeStatusAsyncResponse setAdministrativeStatus(
            final SetAdministrativeStatusRequest setAdministrativeStatusRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return (SetAdministrativeStatusAsyncResponse) this.getTemplate()
                .marshalSendAndReceive(setAdministrativeStatusRequest);
    }

    public SetAdministrativeStatusResponse retrieveSetAdministrativeStatusResponse(
            final SetAdministrativeStatusAsyncRequest setAdministrativeStatusAsyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = setAdministrativeStatusAsyncRequest.getCorrelationUid();
        this.waitForNotification(correlationUid);

        return (SetAdministrativeStatusResponse) this.getTemplate()
                .marshalSendAndReceive(setAdministrativeStatusAsyncRequest);
    }

    public SetAlarmNotificationsAsyncResponse setAlarmNotifications(
            final SetAlarmNotificationsRequest setAlarmNotificationsRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return (SetAlarmNotificationsAsyncResponse) this.getTemplate()
                .marshalSendAndReceive(setAlarmNotificationsRequest);
    }

    public SetAlarmNotificationsResponse retrieveSetAlarmNotificationsResponse(
            final SetAlarmNotificationsAsyncRequest setAlarmNotificationsAsyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = setAlarmNotificationsAsyncRequest.getCorrelationUid();
        this.waitForNotification(correlationUid);

        return (SetAlarmNotificationsResponse) this.getTemplate()
                .marshalSendAndReceive(setAlarmNotificationsAsyncRequest);
    }

    public SetConfigurationObjectAsyncResponse setConfigurationObject(
            final SetConfigurationObjectRequest setConfigurationObjectRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return (SetConfigurationObjectAsyncResponse) this.getTemplate()
                .marshalSendAndReceive(setConfigurationObjectRequest);
    }

    public SetConfigurationObjectResponse retrieveSetConfigurationObjectResponse(
            final SetConfigurationObjectAsyncRequest setConfigurationObjectAsyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = setConfigurationObjectAsyncRequest.getCorrelationUid();
        this.waitForNotification(correlationUid);

        return (SetConfigurationObjectResponse) this.getTemplate()
                .marshalSendAndReceive(setConfigurationObjectAsyncRequest);
    }

    public SetSpecialDaysAsyncResponse setSpecialDays(final SetSpecialDaysRequest setSpecialDaysRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return (SetSpecialDaysAsyncResponse) this.getTemplate().marshalSendAndReceive(setSpecialDaysRequest);
    }

    public SetSpecialDaysResponse retrieveSetSpecialDaysResponse(
            final SetSpecialDaysAsyncRequest setSpecialDaysAsyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = setSpecialDaysAsyncRequest.getCorrelationUid();
        this.waitForNotification(correlationUid);

        return (SetSpecialDaysResponse) this.getTemplate().marshalSendAndReceive(setSpecialDaysAsyncRequest);
    }

    public SetEncryptionKeyExchangeOnGMeterAsyncResponse setEncryptionKeyExchangeOnGMeter(
            final SetEncryptionKeyExchangeOnGMeterRequest setEncryptionKeyExchangeOnGMeterRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return (SetEncryptionKeyExchangeOnGMeterAsyncResponse) this.getTemplate()
                .marshalSendAndReceive(setEncryptionKeyExchangeOnGMeterRequest);
    }

    public SetEncryptionKeyExchangeOnGMeterResponse retrieveSetEncryptionKeyExchangeOnGMeterResponse(
            final SetEncryptionKeyExchangeOnGMeterAsyncRequest setEncryptionKeyExchangeOnGMeterAsyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = setEncryptionKeyExchangeOnGMeterAsyncRequest.getCorrelationUid();
        this.waitForNotification(correlationUid);

        return (SetEncryptionKeyExchangeOnGMeterResponse) this.getTemplate()
                .marshalSendAndReceive(setEncryptionKeyExchangeOnGMeterAsyncRequest);
    }

    public GetMbusEncryptionKeyStatusAsyncResponse getMbusEncryptionKeyStatus(
            final GetMbusEncryptionKeyStatusRequest getMbusEncryptionKeyStatusRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return (GetMbusEncryptionKeyStatusAsyncResponse) this.getTemplate()
                .marshalSendAndReceive(getMbusEncryptionKeyStatusRequest);
    }

    public GetMbusEncryptionKeyStatusResponse retrieveGetMbusEncryptionKeyStatusResponse(
            final GetMbusEncryptionKeyStatusAsyncRequest getMbusEncryptionKeyStatusAsyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = getMbusEncryptionKeyStatusAsyncRequest.getCorrelationUid();
        this.waitForNotification(correlationUid);

        return (GetMbusEncryptionKeyStatusResponse) this.getTemplate()
                .marshalSendAndReceive(getMbusEncryptionKeyStatusAsyncRequest);
    }

    public SetMbusUserKeyByChannelAsyncResponse setMbusUserKeyByChannel(
            final SetMbusUserKeyByChannelRequest setMbusUserKeyByChannelRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return (SetMbusUserKeyByChannelAsyncResponse) this.getTemplate()
                .marshalSendAndReceive(setMbusUserKeyByChannelRequest);
    }

    public SetMbusUserKeyByChannelResponse getSetMbusUserKeyByChannelResponse(
            final SetMbusUserKeyByChannelAsyncRequest setMbusUserKeyByChannelAsyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = setMbusUserKeyByChannelAsyncRequest.getCorrelationUid();
        this.waitForNotification(correlationUid);

        return (SetMbusUserKeyByChannelResponse) this.getTemplate()
                .marshalSendAndReceive(setMbusUserKeyByChannelAsyncRequest);
    }

    public ReplaceKeysAsyncResponse replaceKeys(final ReplaceKeysRequest replaceKeysRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        return (ReplaceKeysAsyncResponse) this.getTemplate().marshalSendAndReceive(replaceKeysRequest);
    }

    public ReplaceKeysResponse getReplaceKeysResponse(final ReplaceKeysAsyncRequest replaceKeysAsyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = replaceKeysAsyncRequest.getCorrelationUid();
        this.waitForNotification(correlationUid);

        return (ReplaceKeysResponse) this.getTemplate().marshalSendAndReceive(replaceKeysAsyncRequest);
    }

    public GenerateAndReplaceKeysAsyncResponse generateAndReplaceKeys(
            final GenerateAndReplaceKeysRequest generateAndReplaceKeysRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        return (GenerateAndReplaceKeysAsyncResponse) this.getTemplate()
                .marshalSendAndReceive(generateAndReplaceKeysRequest);
    }

    public GenerateAndReplaceKeysResponse getGenerateAndReplaceKeysResponse(
            final GenerateAndReplaceKeysAsyncRequest generateAndReplaceKeysAsyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = generateAndReplaceKeysAsyncRequest.getCorrelationUid();
        this.waitForNotification(correlationUid);

        return (GenerateAndReplaceKeysResponse) this.getTemplate()
                .marshalSendAndReceive(generateAndReplaceKeysAsyncRequest);
    }

    public SetClockConfigurationAsyncResponse setClockConfiguration(
            final SetClockConfigurationRequest setClockConfigurationRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return (SetClockConfigurationAsyncResponse) this.getTemplate()
                .marshalSendAndReceive(setClockConfigurationRequest);
    }

    public SetClockConfigurationResponse getSetClockConfigurationResponse(
            final SetClockConfigurationAsyncRequest setClockConfigurationAsyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = setClockConfigurationAsyncRequest.getCorrelationUid();
        this.waitForNotification(correlationUid);

        return (SetClockConfigurationResponse) this.getTemplate()
                .marshalSendAndReceive(setClockConfigurationAsyncRequest);
    }

    public ConfigureDefinableLoadProfileAsyncResponse configureDefinableLoadProfile(
            final ConfigureDefinableLoadProfileRequest configureDefinableLoadProfileRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return (ConfigureDefinableLoadProfileAsyncResponse) this.getTemplate()
                .marshalSendAndReceive(configureDefinableLoadProfileRequest);
    }

    public ConfigureDefinableLoadProfileResponse getConfigureDefinableLoadProfileResponse(
            final ConfigureDefinableLoadProfileAsyncRequest configureDefinableLoadProfileAsyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = configureDefinableLoadProfileAsyncRequest.getCorrelationUid();
        this.waitForNotification(correlationUid);

        return (ConfigureDefinableLoadProfileResponse) this.getTemplate()
                .marshalSendAndReceive(configureDefinableLoadProfileAsyncRequest);
    }

    public SetPushSetupSmsAsyncResponse setPushSetupSms(final SetPushSetupSmsRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return (SetPushSetupSmsAsyncResponse) this.getTemplate().marshalSendAndReceive(request);
    }

    public SetPushSetupSmsResponse getSetPushSetupSmsResponse(final SetPushSetupSmsAsyncRequest asyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = asyncRequest.getCorrelationUid();
        this.waitForNotification(correlationUid);

        return (SetPushSetupSmsResponse) this.getTemplate().marshalSendAndReceive(asyncRequest);
    }

    private WebServiceTemplate getTemplate() throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return this.smartMeteringConfigurationWebServiceTemplateFactory
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
    }

    public GetMbusEncryptionKeyStatusByChannelAsyncResponse getMbusEncryptionKeyStatusByChannel(
            final GetMbusEncryptionKeyStatusByChannelRequest getMbusEncryptionKeyStatusByChannelRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return (GetMbusEncryptionKeyStatusByChannelAsyncResponse) this.getTemplate()
                .marshalSendAndReceive(getMbusEncryptionKeyStatusByChannelRequest);
    }

    public GetMbusEncryptionKeyStatusByChannelResponse retrieveGetMbusEncryptionKeyStatusByChannelResponse(
            final GetMbusEncryptionKeyStatusByChannelAsyncRequest getMbusEncryptionKeyStatusByChannelAsyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = getMbusEncryptionKeyStatusByChannelAsyncRequest.getCorrelationUid();
        this.waitForNotification(correlationUid);

        return (GetMbusEncryptionKeyStatusByChannelResponse) this.getTemplate()
                .marshalSendAndReceive(getMbusEncryptionKeyStatusByChannelAsyncRequest);
    }
}
