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

    public UpdateFirmwareAsyncResponse updateFirmware(final UpdateFirmwareRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        return (UpdateFirmwareAsyncResponse) this.getTemplate().marshalSendAndReceive(request);
    }

    public UpdateFirmwareResponse getUpdateFirmwareResponse(final UpdateFirmwareAsyncRequest asyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = asyncRequest.getCorrelationUid();
        this.waitForDlmsResponseData(correlationUid);

        return (UpdateFirmwareResponse) this.getTemplate().marshalSendAndReceive(asyncRequest);
    }

    public GetFirmwareVersionAsyncResponse getFirmwareVersion(final GetFirmwareVersionRequest getFirmwareVersionRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return (GetFirmwareVersionAsyncResponse) this.getTemplate().marshalSendAndReceive(getFirmwareVersionRequest);
    }

    public GetFirmwareVersionResponse retrieveGetFirmwareVersionResponse(
            final GetFirmwareVersionAsyncRequest getFirmwareVersionAsyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = getFirmwareVersionAsyncRequest.getCorrelationUid();
        this.waitForDlmsResponseData(correlationUid);

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
        this.waitForDlmsResponseData(correlationUid);

        return (SetActivityCalendarResponse) this.getTemplate().marshalSendAndReceive(setActivityCalendarAsyncRequest);
    }

    public GetAdministrativeStatusAsyncResponse getAdministrativeStatus(final GetAdministrativeStatusRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return (GetAdministrativeStatusAsyncResponse) this.getTemplate().marshalSendAndReceive(request);
    }

    public GetAdministrativeStatusResponse retrieveGetAdministrativeStatusResponse(
            final GetAdministrativeStatusAsyncRequest asyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = asyncRequest.getCorrelationUid();
        this.waitForDlmsResponseData(correlationUid);

        return (GetAdministrativeStatusResponse) this.getTemplate().marshalSendAndReceive(asyncRequest);
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
        this.waitForDlmsResponseData(correlationUid);

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
        this.waitForDlmsResponseData(correlationUid);

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
        this.waitForDlmsResponseData(correlationUid);

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
        this.waitForDlmsResponseData(correlationUid);

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
        this.waitForDlmsResponseData(correlationUid);

        return (SetEncryptionKeyExchangeOnGMeterResponse) this.getTemplate()
                .marshalSendAndReceive(setEncryptionKeyExchangeOnGMeterAsyncRequest);
    }

    public ReplaceKeysAsyncResponse replaceKeys(final ReplaceKeysRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        return (ReplaceKeysAsyncResponse) this.getTemplate().marshalSendAndReceive(request);
    }

    public ReplaceKeysResponse getReplaceKeysResponse(final ReplaceKeysAsyncRequest asyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = asyncRequest.getCorrelationUid();
        this.waitForDlmsResponseData(correlationUid);

        return (ReplaceKeysResponse) this.getTemplate().marshalSendAndReceive(asyncRequest);
    }

    public GenerateAndReplaceKeysAsyncResponse generateAndReplaceKeys(final GenerateAndReplaceKeysRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        return (GenerateAndReplaceKeysAsyncResponse) this.getTemplate().marshalSendAndReceive(request);
    }

    public GenerateAndReplaceKeysResponse getGenerateAndReplaceKeysResponse(
            final GenerateAndReplaceKeysAsyncRequest asyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = asyncRequest.getCorrelationUid();
        this.waitForDlmsResponseData(correlationUid);

        return (GenerateAndReplaceKeysResponse) this.getTemplate().marshalSendAndReceive(asyncRequest);
    }

    public SetClockConfigurationAsyncResponse setClockConfiguration(final SetClockConfigurationRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return (SetClockConfigurationAsyncResponse) this.getTemplate().marshalSendAndReceive(request);
    }

    public SetClockConfigurationResponse getSetClockConfigurationResponse(
            final SetClockConfigurationAsyncRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = request.getCorrelationUid();
        this.waitForDlmsResponseData(correlationUid);

        return (SetClockConfigurationResponse) this.getTemplate().marshalSendAndReceive(request);
    }

    private WebServiceTemplate getTemplate() throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return this.smartMeteringConfigurationWebServiceTemplateFactory
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
    }
}
