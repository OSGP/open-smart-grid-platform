// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.endpoints;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.ws.domain.entities.ApplicationDataLookupKey;
import org.opensmartgridplatform.adapter.ws.domain.entities.ApplicationKeyConfiguration;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ApplicationKeyConfigurationRepository;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.BypassRetry;
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
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.FirmwareVersionGas;
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
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetKeysResponseData;
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
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetKeyOnGMeterAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetKeyOnGMeterAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetKeyOnGMeterRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetKeyOnGMeterResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupAlarmAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupAlarmAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupAlarmRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupAlarmResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupLastGaspAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupLastGaspAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupLastGaspRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupLastGaspResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupUdpAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupUdpAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupUdpRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupUdpResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetRandomisationSettingsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetRandomisationSettingsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetRandomisationSettingsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetRandomisationSettingsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetThdConfigurationAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetThdConfigurationAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetThdConfigurationRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetThdConfigurationResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareResponse;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.services.RequestService;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActivityCalendar;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmNotifications;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EncryptionKeyStatusType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.FirmwareVersionGasResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.FirmwareVersionResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetFirmwareVersionQuery;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetKeysRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushNotificationAlarm;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetKeyOnGMeterRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetMbusUserKeyByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetPushSetupUdpRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetRandomisationSettingsRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ThdConfiguration;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.UpdateFirmwareRequestData;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.security.RsaEncrypter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Slf4j
@Endpoint
public class SmartMeteringConfigurationEndpoint extends SmartMeteringEndpoint {

  private static final String SMARTMETER_CONFIGURATION_NAMESPACE =
      "http://www.opensmartgridplatform.org/schemas/smartmetering/sm-configuration/2014/10";

  @Autowired private RequestService requestService;

  @Autowired private ConfigurationMapper configurationMapper;

  @Autowired private ApplicationKeyConfigurationRepository applicationKeyConfigurationRepository;

  @Autowired
  @Qualifier("decrypterForGxfSmartMetering")
  private RsaEncrypter decrypterForGxfSmartMetering;

  @Autowired private String webserviceNotificationApplicationName;

  public SmartMeteringConfigurationEndpoint() {
    // Default constructor
  }

  /**
   * Starts the proces of retrieving the firmware version(s) of the device specified in the {@link
   * GetFirmwareVersionRequest}
   *
   * @param organisationIdentification {@link String} containing the identification of the
   *     organization
   * @param request the {@link GetFirmwareVersionRequest}
   * @param messagePriority the message priority
   * @param scheduleTime the time the message is scheduled
   * @return the {@link GetFirmwareVersionAsyncResponse} containing the correlation id
   * @throws OsgpException
   */
  @PayloadRoot(
      localPart = "GetFirmwareVersionRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public GetFirmwareVersionAsyncResponse getFirmwareVersion(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetFirmwareVersionRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.GET_FIRMWARE_VERSION)
            .withMessageType(MessageType.GET_FIRMWARE_VERSION)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(
            requestMessageMetadata, new GetFirmwareVersionQuery());

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(asyncResponse, GetFirmwareVersionAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "GetFirmwareVersionGasRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public GetFirmwareVersionGasAsyncResponse getFirmwareVersionGas(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetFirmwareVersionGasRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.GET_FIRMWARE_VERSION)
            .withMessageType(MessageType.GET_FIRMWARE_VERSION)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(
            requestMessageMetadata, new GetFirmwareVersionQuery(true));

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(asyncResponse, GetFirmwareVersionGasAsyncResponse.class);
  }

  /**
   * Gets the Firmware version response from the database (if it is there) and returns {@link
   * GetFirmwareVersionResponse} containing those firmware versions.
   *
   * @param organisationIdentification {@link String} containing the identification of the
   *     organization
   * @param request {@link GetFirmwareVersionAsyncRequest} containing the correlation id as the
   *     response identifier
   * @return {@link GetFirmwareVersionResponse} containing the firmware version(s) for the device.
   * @throws OsgpException is thrown when the correlationId cannot be found in the database
   */
  @PayloadRoot(
      localPart = "GetFirmwareVersionAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public GetFirmwareVersionResponse getGetFirmwareVersionResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetFirmwareVersionAsyncRequest request)
      throws OsgpException {

    log.info(
        "GetFirmwareVersionResponse Request received from organisation {} for device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    final GetFirmwareVersionResponse response = new GetFirmwareVersionResponse();

    try {
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);
      if (responseData != null) {
        response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));

        if (responseData.getMessageData() != null) {
          final List<FirmwareVersion> target = response.getFirmwareVersion();
          final FirmwareVersionResponse firmwareVersionResponse =
              (FirmwareVersionResponse) responseData.getMessageData();
          target.addAll(
              this.configurationMapper.mapAsList(
                  firmwareVersionResponse.getFirmwareVersions(), FirmwareVersion.class));
        } else {
          log.info("Get Firmware Version firmware is null");
        }
      }
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  @PayloadRoot(
      localPart = "GetFirmwareVersionGasAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public GetFirmwareVersionGasResponse getGetFirmwareVersionGasResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetFirmwareVersionGasAsyncRequest request)
      throws OsgpException {

    log.info(
        "GetFirmwareVersionGasResponse request received from organisation {} for device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    final GetFirmwareVersionGasResponse response = new GetFirmwareVersionGasResponse();

    try {
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);
      if (responseData != null) {
        response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));

        if (responseData.getMessageData() != null) {
          final FirmwareVersionGasResponse firmwareVersionGasResponse =
              (FirmwareVersionGasResponse) responseData.getMessageData();
          final FirmwareVersionGas firmwareVersionGas =
              this.configurationMapper.map(
                  firmwareVersionGasResponse.getFirmwareVersion(), FirmwareVersionGas.class);
          response.setFirmwareVersion(firmwareVersionGas);
        } else {
          log.info("GetFirmwareVersionGas: firmware is null");
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
      @RequestPayload final UpdateFirmwareRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final UpdateFirmwareRequestData updateFirmwareRequestData =
        this.configurationMapper.map(request, UpdateFirmwareRequestData.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.UPDATE_FIRMWARE)
            .withMessageType(MessageType.UPDATE_FIRMWARE)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(
            requestMessageMetadata, updateFirmwareRequestData);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(asyncResponse, UpdateFirmwareAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "UpdateFirmwareAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
          .UpdateFirmwareResponse
      getUpdateFirmwareResponse(
          @OrganisationIdentification final String organisationIdentification,
          @RequestPayload final UpdateFirmwareAsyncRequest request)
          throws OsgpException {

    log.info(
        "GetUpdateFirmwareResponse Request received from organisation {} for device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    UpdateFirmwareResponse response = null;
    try {
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "updating firmware");

      response = new UpdateFirmwareResponse();
      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  @PayloadRoot(
      localPart = "SetAdministrativeStatusRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetAdministrativeStatusAsyncResponse setAdministrativeStatus(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetAdministrativeStatusRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AdministrativeStatusType
        dataRequest =
            this.configurationMapper.map(
                request.getEnabled(),
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                    .AdministrativeStatusType.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.SET_ADMINISTRATIVE_STATUS)
            .withMessageType(MessageType.SET_ADMINISTRATIVE_STATUS)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, dataRequest);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(asyncResponse, SetAdministrativeStatusAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "SetAdministrativeStatusAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetAdministrativeStatusResponse retrieveSetAdministrativeStatusResponse(
      @RequestPayload final SetAdministrativeStatusAsyncRequest request) throws OsgpException {

    SetAdministrativeStatusResponse response = null;
    try {
      response = new SetAdministrativeStatusResponse();
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      if (responseData.getMessageData() instanceof String) {
        response.setDescription((String) responseData.getMessageData());
      }
    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(
      localPart = "GetAdministrativeStatusRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public GetAdministrativeStatusAsyncResponse getAdministrativeStatus(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetAdministrativeStatusRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.GET_ADMINISTRATIVE_STATUS)
            .withMessageType(MessageType.GET_ADMINISTRATIVE_STATUS)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(
            requestMessageMetadata,
            org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                .AdministrativeStatusType.UNDEFINED);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(asyncResponse, GetAdministrativeStatusAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "GetAdministrativeStatusAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public GetAdministrativeStatusResponse retrieveGetAdministrativeStatusResponse(
      @RequestPayload final GetAdministrativeStatusAsyncRequest request) throws OsgpException {

    GetAdministrativeStatusResponse response = null;
    try {
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "retrieving the administrative status");

      response = new GetAdministrativeStatusResponse();
      final AdministrativeStatusType dataRequest =
          this.configurationMapper.map(
              responseData.getMessageData(), AdministrativeStatusType.class);
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
      @RequestPayload final SetSpecialDaysRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecialDaysRequest
        dataRequest =
            this.configurationMapper.map(
                request,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecialDaysRequest
                    .class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.SET_SPECIAL_DAYS)
            .withMessageType(MessageType.SET_SPECIAL_DAYS)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, dataRequest);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(asyncResponse, SetSpecialDaysAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "SetSpecialDaysAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetSpecialDaysResponse getSetSpecialDaysResponse(
      @RequestPayload final SetSpecialDaysAsyncRequest request) throws OsgpException {

    SetSpecialDaysResponse response = null;
    try {
      response = new SetSpecialDaysResponse();
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      if (responseData.getMessageData() instanceof String) {
        response.setDescription((String) responseData.getMessageData());
      }
    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(
      localPart = "SetConfigurationObjectRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetConfigurationObjectAsyncResponse setConfigurationObject(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetConfigurationObjectRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
            .SetConfigurationObjectRequest
        dataRequest =
            this.configurationMapper.map(
                request,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                    .SetConfigurationObjectRequest.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.SET_CONFIGURATION_OBJECT)
            .withMessageType(MessageType.SET_CONFIGURATION_OBJECT)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, dataRequest);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(asyncResponse, SetConfigurationObjectAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "SetConfigurationObjectAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetConfigurationObjectResponse getSetConfigurationObjectResponse(
      @RequestPayload final SetConfigurationObjectAsyncRequest request) throws OsgpException {

    SetConfigurationObjectResponse response = null;
    try {
      response = new SetConfigurationObjectResponse();
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      if (responseData.getMessageData() instanceof String) {
        response.setDescription((String) responseData.getMessageData());
      }
    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(localPart = "SetKeyOnGMeterRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetKeyOnGMeterAsyncResponse setKeyOnGMeter(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetKeyOnGMeterRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final SetKeyOnGMeterRequestData dataRequest =
        this.configurationMapper.map(
            request.getSetKeyOnGMeterRequestData(), SetKeyOnGMeterRequestData.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.SET_KEY_ON_G_METER)
            .withMessageType(MessageType.SET_KEY_ON_G_METER)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, dataRequest);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(asyncResponse, SetKeyOnGMeterAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "SetKeyOnGMeterAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetKeyOnGMeterResponse retrieveSetKeyOnGMeterResponse(
      @RequestPayload final SetKeyOnGMeterAsyncRequest request) throws OsgpException {

    SetKeyOnGMeterResponse response = null;
    try {
      response = new SetKeyOnGMeterResponse();
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      if (responseData.getMessageData() instanceof String) {
        response.setDescription((String) responseData.getMessageData());
      }
    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(
      localPart = "GetMbusEncryptionKeyStatusRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public GetMbusEncryptionKeyStatusAsyncResponse getMbusEncryptionKeyStatus(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetMbusEncryptionKeyStatusRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.GET_MBUS_ENCRYPTION_KEY_STATUS)
            .withMessageType(MessageType.GET_MBUS_ENCRYPTION_KEY_STATUS)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, null);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(
        asyncResponse, GetMbusEncryptionKeyStatusAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "GetMbusEncryptionKeyStatusAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public GetMbusEncryptionKeyStatusResponse getGetMBusEncryptionKeyStatusResponse(
      @RequestPayload final GetMbusEncryptionKeyStatusAsyncRequest request) throws OsgpException {

    GetMbusEncryptionKeyStatusResponse response = null;
    try {
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "retrieving the M-Bus encryption key status.");

      response = new GetMbusEncryptionKeyStatusResponse();
      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      response.setEncryptionKeyStatus(
          EncryptionKeyStatus.fromValue(
              ((EncryptionKeyStatusType) responseData.getMessageData()).name()));

    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(
      localPart = "GetMbusEncryptionKeyStatusByChannelRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public GetMbusEncryptionKeyStatusByChannelAsyncResponse getMbusEncryptionKeyStatusByChannel(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetMbusEncryptionKeyStatusByChannelRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final GetMbusEncryptionKeyStatusByChannelRequestData requestData =
        this.configurationMapper.map(
            request.getGetMbusEncryptionKeyStatusByChannelRequestData(),
            GetMbusEncryptionKeyStatusByChannelRequestData.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getGatewayDeviceIdentification())
            .withDeviceFunction(DeviceFunction.GET_MBUS_ENCRYPTION_KEY_STATUS_BY_CHANNEL)
            .withMessageType(MessageType.GET_MBUS_ENCRYPTION_KEY_STATUS_BY_CHANNEL)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, requestData);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(
        asyncResponse, GetMbusEncryptionKeyStatusByChannelAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "GetMbusEncryptionKeyStatusByChannelAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public GetMbusEncryptionKeyStatusByChannelResponse getGetMBusEncryptionKeyStatusByChannelResponse(
      @RequestPayload final GetMbusEncryptionKeyStatusByChannelAsyncRequest request)
      throws OsgpException {

    GetMbusEncryptionKeyStatusByChannelResponse response = null;
    try {
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(
          responseData, "retrieving the M-Bus encryption key status by channel.");

      response = new GetMbusEncryptionKeyStatusByChannelResponse();
      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      response.setEncryptionKeyStatus(
          EncryptionKeyStatus.fromValue(
              ((EncryptionKeyStatusType) responseData.getMessageData()).name()));

    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(
      localPart = "SetPushSetupAlarmRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetPushSetupAlarmAsyncResponse setPushSetupAlarm(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetPushSetupAlarmRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupAlarm
        pushSetupAlarm =
            this.configurationMapper.map(
                request.getSetPushSetupAlarmRequestData().getPushSetupAlarm(),
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupAlarm
                    .class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.SET_PUSH_SETUP_ALARM)
            .withMessageType(MessageType.SET_PUSH_SETUP_ALARM)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, pushSetupAlarm);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(asyncResponse, SetPushSetupAlarmAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "SetPushSetupAlarmAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetPushSetupAlarmResponse getSetPushSetupAlarmResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetPushSetupAlarmAsyncRequest request)
      throws OsgpException {

    log.info(
        "Incoming SetPushSetupAlarmAsyncRequest for organisation {} for meter: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    SetPushSetupAlarmResponse response = null;
    try {
      response = new SetPushSetupAlarmResponse();
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      if (responseData.getMessageData() instanceof String) {
        response.setDescription((String) responseData.getMessageData());
      }
    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(
      localPart = "SetPushSetupLastGaspRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetPushSetupLastGaspAsyncResponse setPushSetupLastGasp(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetPushSetupLastGaspRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupLastGasp
        pushSetupLastGasp =
            this.configurationMapper.map(
                request.getSetPushSetupLastGaspRequestData().getPushSetupLastGasp(),
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupLastGasp
                    .class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.SET_PUSH_SETUP_LAST_GASP)
            .withMessageType(MessageType.SET_PUSH_SETUP_LAST_GASP)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, pushSetupLastGasp);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(asyncResponse, SetPushSetupLastGaspAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "SetPushSetupLastGaspAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetPushSetupLastGaspResponse getSetPushSetupLastGaspResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetPushSetupLastGaspAsyncRequest request)
      throws OsgpException {

    log.info(
        "Incoming SetPushSetupLastGaspAsyncRequest for organisation {} for meter: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    SetPushSetupLastGaspResponse response = null;
    try {
      response = new SetPushSetupLastGaspResponse();
      final ResponseData meterResponseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

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
      @RequestPayload final SetPushSetupSmsRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupSms
        pushSetupSms =
            this.configurationMapper.map(
                request.getSetPushSetupSmsRequestData().getPushSetupSms(),
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupSms
                    .class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.SET_PUSH_SETUP_SMS)
            .withMessageType(MessageType.SET_PUSH_SETUP_SMS)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, pushSetupSms);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(asyncResponse, SetPushSetupSmsAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "SetPushSetupSmsAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetPushSetupSmsResponse getSetPushSetupSmsResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetPushSetupSmsAsyncRequest request)
      throws OsgpException {

    log.info(
        "Incoming SetPushSetupSmsAsyncRequest for organisation {} for meter: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    SetPushSetupSmsResponse response = null;
    try {
      response = new SetPushSetupSmsResponse();
      final ResponseData meterResponseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
      if (meterResponseData.getMessageData() instanceof String) {
        response.setDescription((String) meterResponseData.getMessageData());
      }
    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(localPart = "SetPushSetupUdpRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetPushSetupUdpAsyncResponse setPushSetupUdp(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetPushSetupUdpRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final SetPushSetupUdpRequestData pushSetupUdpRequestData =
        this.configurationMapper.map(request, SetPushSetupUdpRequestData.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.SET_PUSH_SETUP_UDP)
            .withMessageType(MessageType.SET_PUSH_SETUP_UDP)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, pushSetupUdpRequestData);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(asyncResponse, SetPushSetupUdpAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "SetPushSetupUdpAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetPushSetupUdpResponse getSetPushSetupUdpResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetPushSetupUdpAsyncRequest request)
      throws OsgpException {

    log.info(
        "Incoming SetPushSetupUdpAsyncRequest for organisation {} for meter: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    SetPushSetupUdpResponse response = null;
    try {
      response = new SetPushSetupUdpResponse();
      final ResponseData meterResponseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
      if (meterResponseData.getMessageData() instanceof String) {
        response.setDescription((String) meterResponseData.getMessageData());
      }
    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(
      localPart = "SetThdConfigurationRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetThdConfigurationAsyncResponse setThdConfiguration(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetThdConfigurationRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final ThdConfiguration thdConfiguration =
        this.configurationMapper.map(
            request.getSetThdConfigurationRequestData().getThdConfiguration(),
            ThdConfiguration.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.SET_THD_CONFIGURATION)
            .withMessageType(MessageType.SET_THD_CONFIGURATION)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, thdConfiguration);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(asyncResponse, SetThdConfigurationAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "SetThdConfigurationAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetThdConfigurationResponse getSetThdConfigurationResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetThdConfigurationAsyncRequest request)
      throws OsgpException {

    log.info(
        "Incoming SetThdConfigurationAsyncRequest for organisation {} for meter: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    SetThdConfigurationResponse response = null;
    try {
      response = new SetThdConfigurationResponse();
      final ResponseData meterResponseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      response.setResult(OsgpResultType.fromValue(meterResponseData.getResultType().getValue()));
      if (meterResponseData.getMessageData() instanceof String) {
        response.setDescription((String) meterResponseData.getMessageData());
      }
    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(
      localPart = "SetActivityCalendarRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetActivityCalendarAsyncResponse setActivityCalendar(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetActivityCalendarRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final ActivityCalendar activityCalendar =
        this.configurationMapper.map(
            request.getActivityCalendarData().getActivityCalendar(), ActivityCalendar.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.SET_ACTIVITY_CALENDAR)
            .withMessageType(MessageType.SET_ACTIVITY_CALENDAR)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, activityCalendar);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(asyncResponse, SetActivityCalendarAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "SetActivityCalendarAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetActivityCalendarResponse getSetActivityCalendarResponse(
      @RequestPayload final SetActivityCalendarAsyncRequest request) throws OsgpException {

    log.info(
        "Incoming retrieveSetActivityCalendarResponse for meter: {}",
        request.getDeviceIdentification());

    SetActivityCalendarResponse response = null;
    try {
      response = new SetActivityCalendarResponse();
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      if (responseData.getMessageData() instanceof String) {
        response.setDescription((String) responseData.getMessageData());
      }
    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(
      localPart = "SetAlarmNotificationsRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetAlarmNotificationsAsyncResponse setAlarmNotifications(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetAlarmNotificationsRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final AlarmNotifications alarmNotifications =
        this.configurationMapper.map(
            request.getSetAlarmNotificationsRequestData().getAlarmNotifications(),
            AlarmNotifications.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.SET_ALARM_NOTIFICATIONS)
            .withMessageType(MessageType.SET_ALARM_NOTIFICATIONS)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, alarmNotifications);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(asyncResponse, SetAlarmNotificationsAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "SetAlarmNotificationsAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetAlarmNotificationsResponse getSetAlarmNotificationsResponse(
      @RequestPayload final SetAlarmNotificationsAsyncRequest request) throws OsgpException {

    SetAlarmNotificationsResponse response = null;
    try {
      response = new SetAlarmNotificationsResponse();
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

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
  public ReplaceKeysAsyncResponse replaceKeys(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final ReplaceKeysRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetKeysRequestData
        keySet =
            this.configurationMapper.map(
                request.getSetKeysRequestData(),
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetKeysRequestData
                    .class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.REPLACE_KEYS)
            .withMessageType(MessageType.REPLACE_KEYS)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, keySet);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(asyncResponse, ReplaceKeysAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "ReplaceKeysAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public ReplaceKeysResponse getReplaceKeysResponse(
      @RequestPayload final ReplaceKeysAsyncRequest request) throws OsgpException {

    ReplaceKeysResponse response = null;
    try {
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

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

  @PayloadRoot(
      localPart = "GenerateAndReplaceKeysRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public GenerateAndReplaceKeysAsyncResponse generateAndReplaceKeys(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GenerateAndReplaceKeysRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.GENERATE_AND_REPLACE_KEYS)
            .withMessageType(MessageType.GENERATE_AND_REPLACE_KEYS)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, null);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(asyncResponse, GenerateAndReplaceKeysAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "GenerateAndReplaceKeysAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public GenerateAndReplaceKeysResponse getGenerateAndReplaceKeysResponse(
      @RequestPayload final GenerateAndReplaceKeysAsyncRequest request) throws OsgpException {

    GenerateAndReplaceKeysResponse response = null;
    try {
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

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

  @PayloadRoot(
      localPart = "GetPushNotificationAlarmAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public GetPushNotificationAlarmResponse getPushNotificationAlarm(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetPushNotificationAlarmAsyncRequest request)
      throws OsgpException {

    log.info(
        "GetPushNotificationAlarmRequest Request received from organisation {} for device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    final GetPushNotificationAlarmResponse response = new GetPushNotificationAlarmResponse();

    try {
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);
      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      if (responseData != null) {

        final PushNotificationAlarm p = (PushNotificationAlarm) responseData.getMessageData();

        response.setDecodedMessage(p.toString());
        response.setEncodedMessage(p.getAlarmBytes());

        response
            .getAlarm()
            .addAll(
                this.configurationMapper.mapAsList(
                    p.getAlarms(),
                    org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.AlarmType
                        .class));
      }
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  @PayloadRoot(
      localPart = "SetClockConfigurationRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetClockConfigurationAsyncResponse setClockConfiguration(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetClockConfigurationRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
            .SetClockConfigurationRequestData
        clockConfiguration =
            this.configurationMapper.map(
                request.getSetClockConfigurationData(),
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                    .SetClockConfigurationRequestData.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.SET_CLOCK_CONFIGURATION)
            .withMessageType(MessageType.SET_CLOCK_CONFIGURATION)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, clockConfiguration);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(asyncResponse, SetClockConfigurationAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "SetClockConfigurationAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetClockConfigurationResponse getSetClockConfigurationResponse(
      @RequestPayload final SetClockConfigurationAsyncRequest request) throws OsgpException {

    SetClockConfigurationResponse response = null;
    try {
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

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

  @PayloadRoot(
      localPart = "GetConfigurationObjectRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public GetConfigurationObjectAsyncResponse getConfigurationObject(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetConfigurationObjectRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
            .GetConfigurationObjectRequest
        dataRequest =
            this.configurationMapper.map(
                request,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                    .GetConfigurationObjectRequest.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.GET_CONFIGURATION_OBJECT)
            .withMessageType(MessageType.GET_CONFIGURATION_OBJECT)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, dataRequest);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(asyncResponse, GetConfigurationObjectAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "GetConfigurationObjectAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public GetConfigurationObjectResponse getGetConfigurationObjectResponse(
      @RequestPayload final GetConfigurationObjectAsyncRequest request) throws OsgpException {

    GetConfigurationObjectResponse response = null;
    try {
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(),
              org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                  .GetConfigurationObjectResponse.class,
              ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "retrieving the configuration object");

      response =
          this.configurationMapper.map(
              responseData.getMessageData(), GetConfigurationObjectResponse.class);
    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(
      localPart = "ConfigureDefinableLoadProfileRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public ConfigureDefinableLoadProfileAsyncResponse configureDefinableLoadProfile(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final ConfigureDefinableLoadProfileRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
            .DefinableLoadProfileConfigurationData
        definableLoadProfileConfiguration =
            this.configurationMapper.map(
                request.getDefinableLoadProfileConfigurationData(),
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                    .DefinableLoadProfileConfigurationData.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.CONFIGURE_DEFINABLE_LOAD_PROFILE)
            .withMessageType(MessageType.CONFIGURE_DEFINABLE_LOAD_PROFILE)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(
            requestMessageMetadata, definableLoadProfileConfiguration);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(
        asyncResponse, ConfigureDefinableLoadProfileAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "ConfigureDefinableLoadProfileAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public ConfigureDefinableLoadProfileResponse getConfigureDefinableLoadProfileResponse(
      @RequestPayload final ConfigureDefinableLoadProfileAsyncRequest request)
      throws OsgpException {

    ConfigureDefinableLoadProfileResponse response = null;
    try {
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

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

  @PayloadRoot(
      localPart = "SetMbusUserKeyByChannelRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetMbusUserKeyByChannelAsyncResponse setMbusUserKeyByChannel(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetMbusUserKeyByChannelRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final SetMbusUserKeyByChannelRequestData setMbusUserKeyByChannelRequestData =
        this.configurationMapper.map(
            request.getSetMbusUserKeyByChannelRequestData(),
            SetMbusUserKeyByChannelRequestData.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.SET_MBUS_USER_KEY_BY_CHANNEL)
            .withMessageType(MessageType.SET_MBUS_USER_KEY_BY_CHANNEL)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(
            requestMessageMetadata, setMbusUserKeyByChannelRequestData);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(asyncResponse, SetMbusUserKeyByChannelAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "SetMbusUserKeyByChannelAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetMbusUserKeyByChannelResponse getSetMbusUserKeyByChannelResponse(
      @RequestPayload final SetMbusUserKeyByChannelAsyncRequest request) throws OsgpException {

    SetMbusUserKeyByChannelResponse response = null;
    try {
      response = new SetMbusUserKeyByChannelResponse();
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      if (responseData.getMessageData() instanceof String) {
        response.setDescription((String) responseData.getMessageData());
      }
    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(
      localPart = "SetRandomisationSettingsRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetRandomisationSettingsAsyncResponse setRandomisationSettings(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetRandomisationSettingsRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final SetRandomisationSettingsRequestData dataRequest =
        this.configurationMapper.map(
            request.getSetRandomisationSettingsData(), SetRandomisationSettingsRequestData.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.SET_RANDOMISATION_SETTINGS)
            .withMessageType(MessageType.SET_RANDOMISATION_SETTINGS)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, dataRequest);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(asyncResponse, SetRandomisationSettingsAsyncResponse.class);
  }

  @PayloadRoot(
      localPart = "SetRandomisationSettingsAsyncRequest",
      namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public SetRandomisationSettingsResponse getSetRandomisationSettingsResponse(
      @RequestPayload final SetRandomisationSettingsAsyncRequest request) throws OsgpException {

    log.info("-- calling getSetRandomisationSettingsResponse ");

    SetRandomisationSettingsResponse response = null;
    try {
      response = new SetRandomisationSettingsResponse();
      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));
      if (responseData.getMessageData() instanceof String) {
        response.setDescription((String) responseData.getMessageData());
      }
    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(localPart = "GetKeysRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public GetKeysAsyncResponse getKeys(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetKeysRequest request,
      @MessagePriority final String messagePriority,
      @ScheduleTime final String scheduleTime,
      @ResponseUrl final String responseUrl,
      @BypassRetry final String bypassRetry)
      throws OsgpException {

    final GetKeysRequestData dataRequest =
        this.configurationMapper.map(request.getGetKeysData(), GetKeysRequestData.class);

    final RequestMessageMetadata requestMessageMetadata =
        RequestMessageMetadata.newBuilder()
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(request.getDeviceIdentification())
            .withDeviceFunction(DeviceFunction.GET_KEYS)
            .withMessageType(MessageType.GET_KEYS)
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduleTime)
            .withBypassRetry(bypassRetry)
            .build();

    final AsyncResponse asyncResponse =
        this.requestService.enqueueAndSendRequest(requestMessageMetadata, dataRequest);

    this.saveResponseUrlIfNeeded(asyncResponse.getCorrelationUid(), responseUrl);

    return this.configurationMapper.map(asyncResponse, GetKeysAsyncResponse.class);
  }

  @PayloadRoot(localPart = "GetKeysAsyncRequest", namespace = SMARTMETER_CONFIGURATION_NAMESPACE)
  @ResponsePayload
  public GetKeysResponse getGetKeysResponse(@RequestPayload final GetKeysAsyncRequest request)
      throws OsgpException {

    GetKeysResponse response = null;
    try {
      response = new GetKeysResponse();

      final ResponseData responseData =
          this.responseDataService.get(
              request.getCorrelationUid(), ComponentType.WS_SMART_METERING);

      this.throwExceptionIfResultNotOk(responseData, "getting keys");

      response.setResult(OsgpResultType.fromValue(responseData.getResultType().getValue()));

      if (responseData.getMessageData() != null) {
        final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetKeysResponse
            getKeysResponse =
                (org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetKeysResponse)
                    responseData.getMessageData();

        final List<GetKeysResponseData> keysEncryptedWithGxfKey =
            this.configurationMapper.mapAsList(
                getKeysResponse.getKeys(), GetKeysResponseData.class);

        final List<GetKeysResponseData> keysEncryptedForApplication =
            this.reencryptKeysInGetKeysResponse(
                keysEncryptedWithGxfKey, responseData.getOrganisationIdentification());

        response.getGetKeysResponseData().addAll(keysEncryptedForApplication);
      } else {
        log.info("Get keys response is null");
      }
    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  private List<GetKeysResponseData> reencryptKeysInGetKeysResponse(
      final List<GetKeysResponseData> keysEncryptedWithGxfKey,
      final String organisationIdentification)
      throws OsgpException {
    final RsaEncrypter applicationRsaEncrypter =
        this.getRsaEncrypterForApplication(organisationIdentification);

    return keysEncryptedWithGxfKey.stream()
        .map(key -> this.reencryptKeyInGetKeysResponse(key, applicationRsaEncrypter))
        .collect(Collectors.toList());
  }

  private GetKeysResponseData reencryptKeyInGetKeysResponse(
      final GetKeysResponseData data, final RsaEncrypter applicationRsaEncrypter) {

    if (data.getSecretValue() != null) {
      final byte[] decryptedKey = this.decrypterForGxfSmartMetering.decrypt(data.getSecretValue());
      data.setSecretValue(applicationRsaEncrypter.encrypt(decryptedKey));
    }

    return data;
  }

  private RsaEncrypter getRsaEncrypterForApplication(final String organisationIdentification)
      throws OsgpException {

    final ApplicationKeyConfiguration applicationKeyConfiguration =
        this.applicationKeyConfigurationRepository
            .findById(
                new ApplicationDataLookupKey(
                    organisationIdentification, this.webserviceNotificationApplicationName))
            .orElseThrow(
                () ->
                    new OsgpException(
                        ComponentType.WS_SMART_METERING,
                        "No public key found for application "
                            + this.webserviceNotificationApplicationName
                            + " and organisation "
                            + organisationIdentification));

    final String publicKeyLocation = applicationKeyConfiguration.getPublicKeyLocation();

    try {
      final Resource keyResource = new FileSystemResource(publicKeyLocation);
      final RsaEncrypter applicationRsaEncrypter = new RsaEncrypter();
      applicationRsaEncrypter.setPublicKeyStore(keyResource.getFile());
      return applicationRsaEncrypter;
    } catch (final IOException e) {
      throw new OsgpException(
          ComponentType.WS_SMART_METERING,
          "Could not get public key file for application "
              + this.webserviceNotificationApplicationName
              + " and organisation "
              + organisationIdentification);
    }
  }
}
