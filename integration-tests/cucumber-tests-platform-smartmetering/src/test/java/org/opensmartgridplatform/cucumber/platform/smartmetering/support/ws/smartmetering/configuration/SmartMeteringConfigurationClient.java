/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GenerateAndReplaceKeysAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GenerateAndReplaceKeysAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GenerateAndReplaceKeysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GenerateAndReplaceKeysResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionGasAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionGasAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionGasRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionGasResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetKeysAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetKeysAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetKeysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetKeysResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusByChannelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusByChannelAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusByChannelResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ReplaceKeysAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ReplaceKeysAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ReplaceKeysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ReplaceKeysResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAlarmNotificationsRequest;
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
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsRequest;
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
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareResponse;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.SmartMeteringBaseClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

@Component
public class SmartMeteringConfigurationClient extends SmartMeteringBaseClient {

  @Autowired
  private DefaultWebServiceTemplateFactory smartMeteringConfigurationWebServiceTemplateFactory;

  public UpdateFirmwareAsyncResponse updateFirmware(
      final UpdateFirmwareRequest updateFirmwareRequest) throws WebServiceSecurityException {

    return (UpdateFirmwareAsyncResponse)
        this.getTemplate().marshalSendAndReceive(updateFirmwareRequest);
  }

  public UpdateFirmwareResponse getUpdateFirmwareResponse(
      final UpdateFirmwareAsyncRequest updateFirmwareAsyncRequest)
      throws WebServiceSecurityException {

    final String correlationUid = updateFirmwareAsyncRequest.getCorrelationUid();
    this.waitForNotification(correlationUid);

    return (UpdateFirmwareResponse)
        this.getTemplate().marshalSendAndReceive(updateFirmwareAsyncRequest);
  }

  public GetFirmwareVersionAsyncResponse getFirmwareVersion(
      final GetFirmwareVersionRequest getFirmwareVersionRequest)
      throws WebServiceSecurityException {
    return (GetFirmwareVersionAsyncResponse)
        this.getTemplate().marshalSendAndReceive(getFirmwareVersionRequest);
  }

  public GetFirmwareVersionGasAsyncResponse getFirmwareVersionGas(
      final GetFirmwareVersionGasRequest getFirmwareVersionRequest)
      throws WebServiceSecurityException {
    return (GetFirmwareVersionGasAsyncResponse)
        this.getTemplate().marshalSendAndReceive(getFirmwareVersionRequest);
  }

  public GetFirmwareVersionResponse retrieveGetFirmwareVersionResponse(
      final GetFirmwareVersionAsyncRequest getFirmwareVersionAsyncRequest)
      throws WebServiceSecurityException {

    final String correlationUid = getFirmwareVersionAsyncRequest.getCorrelationUid();
    this.waitForNotification(correlationUid);

    return (GetFirmwareVersionResponse)
        this.getTemplate().marshalSendAndReceive(getFirmwareVersionAsyncRequest);
  }

  public GetFirmwareVersionGasResponse retrieveGetFirmwareVersionGasResponse(
      final GetFirmwareVersionGasAsyncRequest gasAsyncRequest) throws WebServiceSecurityException {

    final String correlationUid = gasAsyncRequest.getCorrelationUid();
    this.waitForNotification(correlationUid);

    return (GetFirmwareVersionGasResponse)
        this.getTemplate().marshalSendAndReceive(gasAsyncRequest);
  }

  public SetActivityCalendarAsyncResponse setActivityCalendar(
      final SetActivityCalendarRequest setActivityCalendarRequest)
      throws WebServiceSecurityException {
    return (SetActivityCalendarAsyncResponse)
        this.getTemplate().marshalSendAndReceive(setActivityCalendarRequest);
  }

  public SetActivityCalendarResponse getSetActivityCalendarResponse(
      final SetActivityCalendarAsyncRequest setActivityCalendarAsyncRequest)
      throws WebServiceSecurityException {

    final String correlationUid = setActivityCalendarAsyncRequest.getCorrelationUid();
    this.waitForNotification(correlationUid);

    return (SetActivityCalendarResponse)
        this.getTemplate().marshalSendAndReceive(setActivityCalendarAsyncRequest);
  }

  public GetAdministrativeStatusAsyncResponse getAdministrativeStatus(
      final GetAdministrativeStatusRequest getAdministrativeStatusRequest)
      throws WebServiceSecurityException {
    return (GetAdministrativeStatusAsyncResponse)
        this.getTemplate().marshalSendAndReceive(getAdministrativeStatusRequest);
  }

  public GetAdministrativeStatusResponse retrieveGetAdministrativeStatusResponse(
      final GetAdministrativeStatusAsyncRequest getAdministrativeStatusAsyncRequest)
      throws WebServiceSecurityException {

    final String correlationUid = getAdministrativeStatusAsyncRequest.getCorrelationUid();
    this.waitForNotification(correlationUid);

    return (GetAdministrativeStatusResponse)
        this.getTemplate().marshalSendAndReceive(getAdministrativeStatusAsyncRequest);
  }

  public SetAdministrativeStatusAsyncResponse setAdministrativeStatus(
      final SetAdministrativeStatusRequest setAdministrativeStatusRequest)
      throws WebServiceSecurityException {
    return (SetAdministrativeStatusAsyncResponse)
        this.getTemplate().marshalSendAndReceive(setAdministrativeStatusRequest);
  }

  public SetAdministrativeStatusResponse retrieveSetAdministrativeStatusResponse(
      final SetAdministrativeStatusAsyncRequest setAdministrativeStatusAsyncRequest)
      throws WebServiceSecurityException {

    final String correlationUid = setAdministrativeStatusAsyncRequest.getCorrelationUid();
    this.waitForNotification(correlationUid);

    return (SetAdministrativeStatusResponse)
        this.getTemplate().marshalSendAndReceive(setAdministrativeStatusAsyncRequest);
  }

  public SetAlarmNotificationsAsyncResponse setAlarmNotifications(
      final SetAlarmNotificationsRequest setAlarmNotificationsRequest)
      throws WebServiceSecurityException {
    return (SetAlarmNotificationsAsyncResponse)
        this.getTemplate().marshalSendAndReceive(setAlarmNotificationsRequest);
  }

  public SetAlarmNotificationsResponse retrieveSetAlarmNotificationsResponse(
      final SetAlarmNotificationsAsyncRequest setAlarmNotificationsAsyncRequest)
      throws WebServiceSecurityException {

    final String correlationUid = setAlarmNotificationsAsyncRequest.getCorrelationUid();
    this.waitForNotification(correlationUid);

    return (SetAlarmNotificationsResponse)
        this.getTemplate().marshalSendAndReceive(setAlarmNotificationsAsyncRequest);
  }

  public SetConfigurationObjectAsyncResponse setConfigurationObject(
      final SetConfigurationObjectRequest setConfigurationObjectRequest)
      throws WebServiceSecurityException {
    return (SetConfigurationObjectAsyncResponse)
        this.getTemplate().marshalSendAndReceive(setConfigurationObjectRequest);
  }

  public SetConfigurationObjectResponse retrieveSetConfigurationObjectResponse(
      final SetConfigurationObjectAsyncRequest setConfigurationObjectAsyncRequest)
      throws WebServiceSecurityException {

    final String correlationUid = setConfigurationObjectAsyncRequest.getCorrelationUid();
    this.waitForNotification(correlationUid);

    return (SetConfigurationObjectResponse)
        this.getTemplate().marshalSendAndReceive(setConfigurationObjectAsyncRequest);
  }

  public SetSpecialDaysAsyncResponse setSpecialDays(
      final SetSpecialDaysRequest setSpecialDaysRequest) throws WebServiceSecurityException {
    return (SetSpecialDaysAsyncResponse)
        this.getTemplate().marshalSendAndReceive(setSpecialDaysRequest);
  }

  public SetSpecialDaysResponse retrieveSetSpecialDaysResponse(
      final SetSpecialDaysAsyncRequest setSpecialDaysAsyncRequest)
      throws WebServiceSecurityException {

    final String correlationUid = setSpecialDaysAsyncRequest.getCorrelationUid();
    this.waitForNotification(correlationUid);

    return (SetSpecialDaysResponse)
        this.getTemplate().marshalSendAndReceive(setSpecialDaysAsyncRequest);
  }

  public SetEncryptionKeyExchangeOnGMeterAsyncResponse setEncryptionKeyExchangeOnGMeter(
      final SetEncryptionKeyExchangeOnGMeterRequest setEncryptionKeyExchangeOnGMeterRequest)
      throws WebServiceSecurityException {
    return (SetEncryptionKeyExchangeOnGMeterAsyncResponse)
        this.getTemplate().marshalSendAndReceive(setEncryptionKeyExchangeOnGMeterRequest);
  }

  public SetEncryptionKeyExchangeOnGMeterResponse retrieveSetEncryptionKeyExchangeOnGMeterResponse(
      final SetEncryptionKeyExchangeOnGMeterAsyncRequest
          setEncryptionKeyExchangeOnGMeterAsyncRequest)
      throws WebServiceSecurityException {

    final String correlationUid = setEncryptionKeyExchangeOnGMeterAsyncRequest.getCorrelationUid();
    this.waitForNotification(correlationUid);

    return (SetEncryptionKeyExchangeOnGMeterResponse)
        this.getTemplate().marshalSendAndReceive(setEncryptionKeyExchangeOnGMeterAsyncRequest);
  }

  public GetMbusEncryptionKeyStatusAsyncResponse getMbusEncryptionKeyStatus(
      final GetMbusEncryptionKeyStatusRequest getMbusEncryptionKeyStatusRequest)
      throws WebServiceSecurityException {
    return (GetMbusEncryptionKeyStatusAsyncResponse)
        this.getTemplate().marshalSendAndReceive(getMbusEncryptionKeyStatusRequest);
  }

  public GetMbusEncryptionKeyStatusResponse retrieveGetMbusEncryptionKeyStatusResponse(
      final GetMbusEncryptionKeyStatusAsyncRequest getMbusEncryptionKeyStatusAsyncRequest)
      throws WebServiceSecurityException {

    final String correlationUid = getMbusEncryptionKeyStatusAsyncRequest.getCorrelationUid();
    this.waitForNotification(correlationUid);

    return (GetMbusEncryptionKeyStatusResponse)
        this.getTemplate().marshalSendAndReceive(getMbusEncryptionKeyStatusAsyncRequest);
  }

  public SetMbusUserKeyByChannelAsyncResponse setMbusUserKeyByChannel(
      final SetMbusUserKeyByChannelRequest setMbusUserKeyByChannelRequest)
      throws WebServiceSecurityException {
    return (SetMbusUserKeyByChannelAsyncResponse)
        this.getTemplate().marshalSendAndReceive(setMbusUserKeyByChannelRequest);
  }

  public SetMbusUserKeyByChannelResponse getSetMbusUserKeyByChannelResponse(
      final SetMbusUserKeyByChannelAsyncRequest setMbusUserKeyByChannelAsyncRequest)
      throws WebServiceSecurityException {

    final String correlationUid = setMbusUserKeyByChannelAsyncRequest.getCorrelationUid();
    this.waitForNotification(correlationUid);

    return (SetMbusUserKeyByChannelResponse)
        this.getTemplate().marshalSendAndReceive(setMbusUserKeyByChannelAsyncRequest);
  }

  public ReplaceKeysAsyncResponse replaceKeys(final ReplaceKeysRequest replaceKeysRequest)
      throws WebServiceSecurityException {

    return (ReplaceKeysAsyncResponse) this.getTemplate().marshalSendAndReceive(replaceKeysRequest);
  }

  public ReplaceKeysResponse getReplaceKeysResponse(
      final ReplaceKeysAsyncRequest replaceKeysAsyncRequest) throws WebServiceSecurityException {

    final String correlationUid = replaceKeysAsyncRequest.getCorrelationUid();
    this.waitForNotification(correlationUid);

    return (ReplaceKeysResponse) this.getTemplate().marshalSendAndReceive(replaceKeysAsyncRequest);
  }

  public GenerateAndReplaceKeysAsyncResponse generateAndReplaceKeys(
      final GenerateAndReplaceKeysRequest generateAndReplaceKeysRequest)
      throws WebServiceSecurityException {

    return (GenerateAndReplaceKeysAsyncResponse)
        this.getTemplate().marshalSendAndReceive(generateAndReplaceKeysRequest);
  }

  public GenerateAndReplaceKeysResponse getGenerateAndReplaceKeysResponse(
      final GenerateAndReplaceKeysAsyncRequest generateAndReplaceKeysAsyncRequest)
      throws WebServiceSecurityException {

    final String correlationUid = generateAndReplaceKeysAsyncRequest.getCorrelationUid();
    this.waitForNotification(correlationUid);

    return (GenerateAndReplaceKeysResponse)
        this.getTemplate().marshalSendAndReceive(generateAndReplaceKeysAsyncRequest);
  }

  public SetClockConfigurationAsyncResponse setClockConfiguration(
      final SetClockConfigurationRequest setClockConfigurationRequest)
      throws WebServiceSecurityException {
    return (SetClockConfigurationAsyncResponse)
        this.getTemplate().marshalSendAndReceive(setClockConfigurationRequest);
  }

  public SetClockConfigurationResponse getSetClockConfigurationResponse(
      final SetClockConfigurationAsyncRequest setClockConfigurationAsyncRequest)
      throws WebServiceSecurityException {

    final String correlationUid = setClockConfigurationAsyncRequest.getCorrelationUid();
    this.waitForNotification(correlationUid);

    return (SetClockConfigurationResponse)
        this.getTemplate().marshalSendAndReceive(setClockConfigurationAsyncRequest);
  }

  public ConfigureDefinableLoadProfileAsyncResponse configureDefinableLoadProfile(
      final ConfigureDefinableLoadProfileRequest configureDefinableLoadProfileRequest)
      throws WebServiceSecurityException {
    return (ConfigureDefinableLoadProfileAsyncResponse)
        this.getTemplate().marshalSendAndReceive(configureDefinableLoadProfileRequest);
  }

  public ConfigureDefinableLoadProfileResponse getConfigureDefinableLoadProfileResponse(
      final ConfigureDefinableLoadProfileAsyncRequest configureDefinableLoadProfileAsyncRequest)
      throws WebServiceSecurityException {

    final String correlationUid = configureDefinableLoadProfileAsyncRequest.getCorrelationUid();
    this.waitForNotification(correlationUid);

    return (ConfigureDefinableLoadProfileResponse)
        this.getTemplate().marshalSendAndReceive(configureDefinableLoadProfileAsyncRequest);
  }

  public SetPushSetupSmsAsyncResponse setPushSetupSms(final SetPushSetupSmsRequest request)
      throws WebServiceSecurityException {
    return (SetPushSetupSmsAsyncResponse) this.getTemplate().marshalSendAndReceive(request);
  }

  public SetPushSetupSmsResponse getSetPushSetupSmsResponse(
      final SetPushSetupSmsAsyncRequest asyncRequest) throws WebServiceSecurityException {

    final String correlationUid = asyncRequest.getCorrelationUid();
    this.waitForNotification(correlationUid);

    return (SetPushSetupSmsResponse) this.getTemplate().marshalSendAndReceive(asyncRequest);
  }

  private WebServiceTemplate getTemplate() throws WebServiceSecurityException {
    return this.smartMeteringConfigurationWebServiceTemplateFactory.getTemplate(
        this.getOrganizationIdentification(), this.getUserName());
  }

  public GetMbusEncryptionKeyStatusByChannelAsyncResponse getMbusEncryptionKeyStatusByChannel(
      final GetMbusEncryptionKeyStatusByChannelRequest getMbusEncryptionKeyStatusByChannelRequest)
      throws WebServiceSecurityException {
    return (GetMbusEncryptionKeyStatusByChannelAsyncResponse)
        this.getTemplate().marshalSendAndReceive(getMbusEncryptionKeyStatusByChannelRequest);
  }

  public GetMbusEncryptionKeyStatusByChannelResponse
      retrieveGetMbusEncryptionKeyStatusByChannelResponse(
          final GetMbusEncryptionKeyStatusByChannelAsyncRequest
              getMbusEncryptionKeyStatusByChannelAsyncRequest)
          throws WebServiceSecurityException {

    final String correlationUid =
        getMbusEncryptionKeyStatusByChannelAsyncRequest.getCorrelationUid();
    this.waitForNotification(correlationUid);

    return (GetMbusEncryptionKeyStatusByChannelResponse)
        this.getTemplate().marshalSendAndReceive(getMbusEncryptionKeyStatusByChannelAsyncRequest);
  }

  public SetRandomisationSettingsAsyncResponse setRandomisationSettings(
      final SetRandomisationSettingsRequest request) throws WebServiceSecurityException {
    return (SetRandomisationSettingsAsyncResponse)
        this.getTemplate().marshalSendAndReceive(request);
  }

  public SetRandomisationSettingsResponse retrieveSetRandomisationSettingsResponse(
      final SetRandomisationSettingsAsyncRequest asyncRequest) throws WebServiceSecurityException {

    final String correlationUid = asyncRequest.getCorrelationUid();
    this.waitForNotification(correlationUid);

    return (SetRandomisationSettingsResponse)
        this.getTemplate().marshalSendAndReceive(asyncRequest);
  }

  public GetKeysAsyncResponse getKeys(final GetKeysRequest request)
      throws WebServiceSecurityException {
    return (GetKeysAsyncResponse) this.getTemplate().marshalSendAndReceive(request);
  }

  public GetKeysResponse retrieveGetKeysResponse(final GetKeysAsyncRequest asyncRequest)
      throws WebServiceSecurityException {

    final String correlationUid = asyncRequest.getCorrelationUid();
    this.waitForNotification(correlationUid);

    return (GetKeysResponse) this.getTemplate().marshalSendAndReceive(asyncRequest);
  }
}
